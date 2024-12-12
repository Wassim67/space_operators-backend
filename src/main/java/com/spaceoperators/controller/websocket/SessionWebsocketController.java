package com.spaceoperators.controller.websocket;

import com.spaceoperators.dao.SessionDao;
import com.spaceoperators.model.request.PlayerSessionRequest;
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

import java.util.List;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class SessionWebsocketController {

	@Autowired
	private SessionDao sessionDao;

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
	 * Gère les messages de démarrage de session envoyés par le front-end.
	 *
	 * @param message Le message JSON contenant le gameId.
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

			// Notifier que la session a démarré
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", ACTION_START));
		} catch (Exception e) {
			sendErrorMessage("Failed to process 'start' message: " + e.getMessage());
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
