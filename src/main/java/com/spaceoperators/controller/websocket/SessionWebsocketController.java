package com.spaceoperators.controller.websocket;

import com.spaceoperators.repository.SessionDao;
import com.spaceoperators.model.entity.GameSession;
import com.spaceoperators.model.request.PlayerSessionRequest;
import com.spaceoperators.model.response.OperationMessage;
import com.spaceoperators.model.response.PlayerSessionResponse;
import com.spaceoperators.model.entity.Player;
import com.spaceoperators.model.response.PlayerData;
import com.spaceoperators.model.response.PlayerListResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import com.spaceoperators.service.SessionService;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
public class SessionWebsocketController {

	private SessionDao sessionDao;

	private SessionService sessionService;

	// Map pour gérer les planifications par session
	private final Map<String, ScheduledExecutorService> gameSchedulers = new ConcurrentHashMap<>();

	// Map pour gérer les statuts des sessions
	private final Map<String, Boolean> gameStatus = new ConcurrentHashMap<>();

	private SimpMessagingTemplate messagingTemplate;

	// Constantes pour les types d'actions
	private static final String ACTION_CONNECT = "connect";
	private static final String ACTION_DISCONNECT = "disconnect";


	public SessionWebsocketController(SessionDao sessionDao,
									  SessionService sessionService,
									  SimpMessagingTemplate messagingTemplate) {
		this.sessionDao = sessionDao;
		this.sessionService = sessionService;
		this.messagingTemplate = messagingTemplate;
	}

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

		// ➔ PROTECTION AJOUTÉE ICI
		if (!sessionDao.gameExists(request.getGameId())) {
			sendErrorMessage("Game with id " + request.getGameId() + " does not exist.");
			return;
		}

		try {
			if (ACTION_CONNECT.equals(action)) {
				sessionDao.removePlayerFromAllSessions(request.getPlayerId());
				sessionDao.insertSession(request.getGameId(), request.getPlayerId(), request.getPlayerName(), false);
			} else if (ACTION_DISCONNECT.equals(action)) {
				sessionDao.removeSession(request.getGameId(), request.getPlayerId());
			}

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
	 * Démarre une session de jeu et lance les tours.
	 *
	 * @param message Le message contenant les données de la session.
	 */
	@MessageMapping("/start")
	public void startGameSession(Map<String, Object> message) {
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

			if (!sessionDao.gameExists(gameId)) {
				sendErrorMessage("Cannot start: game with id " + gameId + " does not exist.");
				return;
			}

			sessionDao.resetReadyStatus(gameId);
			notifyPlayers(gameId);
			sessionService.startNewGame(gameId);
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "start"));
			gameStatus.put(gameId, true);
			sendNextTurn(gameId);
			gameSchedulers.putIfAbsent(gameId, Executors.newSingleThreadScheduledExecutor());
			scheduleNextTurn(gameId);

		} catch (Exception e) {
			sendErrorMessage("Failed to process 'start' message: " + e.getMessage());
		}
	}



	@MessageMapping("/finish-operation")
	public void handleFinishOperation(Map<String, Object> message) {
		try {
			Object data = message.get("data");
			if (!(data instanceof Map)) {
				sendErrorMessage("Invalid message format");
				return;
			}

			Map<?, ?> dataMap = (Map<?, ?>) data;
			String operator = (String) dataMap.get("operator");
			Boolean success = (Boolean) dataMap.get("success");

			// Récupérer le gameId à partir de l'opérateur
			String gameId = sessionDao.getGameIdForPlayer(operator);
			GameSession session = sessionService.getGameSession(gameId);

			if (session != null) {
				session.setTurnCompleted(true);

				if (!success) {
					// Si l'opération a échoué, diminuer l'intégrité
					session.decreaseIntegrity(10);

					// Envoyer la mise à jour de l'intégrité
					messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
							"type", "integrity",
							"data", Map.of("integrity", session.getIntegrity())
					));

					// Vérifier si le vaisseau est détruit
					if (session.getIntegrity() <= 0) {
						// Le vaisseau est détruit, envoyer le message de fin de jeu
						messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
								"type", "destroyed",
								"data", Map.of("turns", session.getCurrentTurn() - 1)
						));
						endGame(gameId);  // Terminer la partie
						return;
					}
				}
			}
		} catch (Exception e) {
			sendErrorMessage("Failed to process finish message: " + e.getMessage());
		}
	}


	private void scheduleNextTurn(String gameId) {
		if (!gameStatus.getOrDefault(gameId, true)) {
			return;
		}

		GameSession session = sessionService.getGameSession(gameId);
		if (session == null) {
			return;
		}

		int duration = session.getDuration();
		ScheduledExecutorService scheduler = gameSchedulers.get(gameId);

		if (scheduler != null) {
			scheduler.schedule(() -> {
				// Vérifier si le tour a été complété
				if (!session.isTurnCompleted()) {
					// Diminuer l'intégrité car le tour n'a pas été complété à temps
					session.decreaseIntegrity(30);

					// Envoyer la mise à jour de l'intégrité
					messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
							"type", "integrity",
							"data", Map.of("integrity", session.getIntegrity())
					));

					// Vérifier si le vaisseau est détruit
					if (session.getIntegrity() <= 0) {
						messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
								"type", "destroyed",
								"data", Map.of("turns", session.getCurrentTurn())
						));
						endGame(gameId);
						return;
					}
				}

				sendNextTurn(gameId);
				scheduleNextTurn(gameId);
			}, duration + 5, TimeUnit.SECONDS);
		}
	}



	/**
	 * Envoie le prochain tour pour une session donnée.
	 *
	 * @param gameId L'identifiant du jeu.
	 */
	private void sendNextTurn(String gameId) {
		GameSession session = sessionService.getGameSession(gameId);
		if (session == null || !gameStatus.getOrDefault(gameId, false)) {
			return;
		}

		OperationMessage nextTurn = session.getNextTurn();
		if (nextTurn != null) {
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
					"type", "operation",
					"data", nextTurn
			));

			// Vérifier si c'est le dernier tour réussi (20 tours)
			if (session.getCurrentTurn() >= 5 && session.getIntegrity() > 0) {
				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "victory"
				));
				endGame(gameId);
			}
		} else {
			endGame(gameId);
		}
	}

	private void endGame(String gameId) {
		// Nettoyage de la session
		gameStatus.remove(gameId);
		sessionService.endSession(gameId);

		ScheduledExecutorService scheduler = gameSchedulers.get(gameId);
		if (scheduler != null) {
			scheduler.shutdown();
			gameSchedulers.remove(gameId);
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
