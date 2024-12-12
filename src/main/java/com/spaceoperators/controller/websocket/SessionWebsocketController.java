package com.spaceoperators.controller.websocket;

import com.spaceoperators.dao.SessionDao;
import com.spaceoperators.model.request.PlayerSessionRequest;
import com.spaceoperators.model.response.OperationMessage;
import com.spaceoperators.model.response.PlayerSessionResponse;
import com.spaceoperators.model.Player;
import com.spaceoperators.model.response.PlayerData;
import com.spaceoperators.model.response.PlayerListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.spaceoperators.service.SessionService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
@CrossOrigin(origins = "*")
public class SessionWebsocketController {

	@Autowired
	private SessionDao sessionDao;

	@Autowired
	private SessionService sessionService;

	// Map pour gérer les planifications par session
	private final Map<String, ScheduledExecutorService> sessionSchedulers = new ConcurrentHashMap<>();

	// Map pour gérer les statuts des sessions
	private final Map<String, Boolean> gameStatus = new ConcurrentHashMap<>();

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();  // Création d'un ScheduledExecutorService c'est pour gerer la planification des tours

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// Constantes pour les types d'actions
	private static final String ACTION_CONNECT = "connect";
	private static final String ACTION_DISCONNECT = "disconnect";
	private static final String ACTION_START = "start";

	/**
	 * Traite les connexions des joueurs via WebSocket.
	 */
	@MessageMapping("/connect")
	public void connectToGame(PlayerSessionRequest request) {
		processPlayerEvent(request, ACTION_CONNECT);
	}

	/**
	 * Traite les déconnexions des joueurs via WebSocket.
	 */
	@MessageMapping("/disconnect")
	public void disconnectFromGame(PlayerSessionRequest request) {
		processPlayerEvent(request, ACTION_DISCONNECT);
	}

	/**
	 * Gère les événements des joueurs (connexion ou déconnexion).
	 *
	 * @param request L'objet contenant les informations du joueur.
	 * @param action  L'action à effectuer : connect ou disconnect.
	 */
	private void processPlayerEvent(PlayerSessionRequest request, String action) {
		if (isInvalidRequest(request)) {
			sendErrorMessage("Invalid data provided for " + action);
			return;
		}

		try {
			if (ACTION_CONNECT.equals(action)) {
				// Ajouter le joueur à la session
				sessionDao.insertSession(request.getGameId(), request.getPlayerId(), request.getPlayerName(), false);
			} else if (ACTION_DISCONNECT.equals(action)) {
				// Retirer le joueur de la session
				sessionDao.removeSession(request.getGameId(), request.getPlayerId());
			}

			// Notifier les joueurs de la mise à jour
			notifyPlayers(request.getGameId());
		} catch (Exception e) {
			sendErrorMessage("Failed to " + action + " player: " + e.getMessage());
		}
	}

	/**
	 * Vérifie si une requête de joueur est invalide.
	 */
	private boolean isInvalidRequest(PlayerSessionRequest request) {
		return request.getGameId() == null || request.getGameId().isEmpty() ||
				request.getPlayerId() == null || request.getPlayerId().isEmpty() ||
				request.getPlayerName() == null || request.getPlayerName().isEmpty();
	}

	/**
	 * Notifie les abonnés WebSocket de la liste mise à jour des joueurs de la session.
	 */
	private void notifyPlayers(String gameId) {
		List<Player> players = sessionDao.getPlayersInSession(gameId);
		PlayerListResponse response = new PlayerListResponse("players", new PlayerData(players));
		messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
	}

	/**
	 * API REST pour mettre à jour l'état "isReady" d'un joueur. (envoie aussi un json pour le front avec la mise à jour des players)
	 *
	 * @param idPlayer L'identifiant du joueur.
	 * @param player   L'objet contenant l'état "isReady".
	 * @return Une réponse JSON indiquant le succès ou l'échec.
	 */
	@PostMapping("/api/player/ready/{idPlayer}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateReadyStatus(
			@PathVariable("idPlayer") String idPlayer, @RequestBody Player player) {
		try {
			if (player.getReady() == null) {
				return ResponseEntity.badRequest().body(Map.of("error", "'isReady' field is missing"));
			}

			// Met à jour l'état "isReady" du joueur
			sessionDao.updatePlayerReady(idPlayer, player.getReady());

			// Récupère le jeu auquel appartient le joueur
			String gameId = sessionDao.getGameIdForPlayer(idPlayer);

			// Notifie les abonnés WebSocket
			notifyPlayers(gameId);

			return ResponseEntity.ok(Map.of("message", "Player readiness updated successfully"));
		} catch (Exception e) {
			return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
		}
	}

	/**
	 * Démarre une session de jeu et lance les tours.
	 *
	 * @param message Le message contenant les données de la session.
	 */
	@MessageMapping("/start")
	public void startGameSession(@RequestBody Map<String, Object> message) {
		try {
			Object data = message.get("data");
			if (!(data instanceof Map)) {
				sendErrorMessage("Invalid message format: 'data' is not a valid object");
				return;
			}

			String gameId = (String) ((Map<?, ?>) data).get("gameId");
			if (gameId == null || gameId.isEmpty()) {
				sendErrorMessage("Invalid message format: 'gameId' is missing or empty");
				return;
			}

			// Notifie les joueurs que la session commence
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", ACTION_START));

			// Initialiser la session comme en cours
			gameStatus.put(gameId, true);

			System.out.println(gameStatus + " voici le game statut");

			// Démarre le prochain tour immédiatement
			sendNextTurn(gameId);

			// Crée un planificateur pour cette session de jeu si ce n'est pas déjà fait
			sessionSchedulers.putIfAbsent(gameId, Executors.newSingleThreadScheduledExecutor());

			// Après le délai du tour on ajoute 5s puis on envoie le prochain tour
			scheduleNextTurn(gameId);

		} catch (Exception e) {
			sendErrorMessage("Failed to process 'start' message: " + e.getMessage());
		}
	}

	private void scheduleNextTurn(String gameId) {
		// Vérifier l'état de la session
		if (!gameStatus.getOrDefault(gameId, true)) {
			// Si le jeu est terminé, ne planifiez pas de nouveau tour
			return;
		}

		int duration = sessionService.getDuration();
		ScheduledExecutorService scheduler = sessionSchedulers.get(gameId);

		// Vérifier si le planificateur existe
		if (scheduler != null) {
			// Planifier le prochain tour seulement si la session est encore en cours
			scheduler.schedule(() -> {
				sendNextTurn(gameId);  // Exécute le tour suivant
				scheduleNextTurn(gameId);  // Replanifie le prochain tour
			}, duration + 5, TimeUnit.SECONDS);  // Délai = durée du tour + 5 secondes d'attente
		}
	}



	/**
	 * Envoie le prochain tour pour une session donnée.
	 *
	 * @param gameId L'identifiant du jeu.
	 */
	private void sendNextTurn(String gameId) {
		// Vérifiez si la session est toujours active (en cours)
		if (!gameStatus.getOrDefault(gameId, true)) {
			// Si la session est terminée, n'envoyez pas de nouveau tour
			return;
		}

		OperationMessage nextTurn = sessionService.getNextTurn();
		if (nextTurn != null) {
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
					"type", "operation",
					"data", nextTurn
			));
		} else {
			// Fin du jeu, notifier les joueurs
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
					"type", "game-end",
					"message", "The game session has ended"
			));

			// Marquer la session comme terminée
			gameStatus.put(gameId, false);

			// Supprimer la session du statut des jeux (plus de sessions terminées dans gameStatus)
			gameStatus.remove(gameId);  // Suppression de la session terminée


			// Supprimer le planificateur pour arrêter tout traitement
			ScheduledExecutorService scheduler = sessionSchedulers.get(gameId);
			if (scheduler != null) {
				scheduler.shutdown(); // Arrêter le planificateur
				sessionSchedulers.remove(gameId); // Supprimer du map
			}
		}
	}





	/**
	 * Envoie un message d'erreur à tous les abonnés.
	 *
	 * @param message Le message d'erreur à envoyer.
	 */
	private void sendErrorMessage(String message) {
		messagingTemplate.convertAndSend("/topic/game", new PlayerSessionResponse("error", message));
	}
}
