package com.spaceoperators.controller.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceoperators.model.entity.History;
import com.spaceoperators.repository.HistoryRepository;
import com.spaceoperators.repository.SessionDao;
import com.spaceoperators.model.entity.GameSession;
import com.spaceoperators.model.request.PlayerSessionRequest;
import com.spaceoperators.model.entity.Player;
import com.spaceoperators.model.response.PlayerData;
import com.spaceoperators.model.response.PlayerListResponse;
import com.spaceoperators.model.response.PlayerSessionResponse;
import com.spaceoperators.model.response.OperationMessage;
import com.spaceoperators.service.SessionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

@Controller
public class SessionWebsocketController {

	private final SessionDao sessionDao;
	private final SessionService sessionService;
	private final Map<String, ScheduledExecutorService> gameSchedulers = new ConcurrentHashMap<>();
	private final Map<String, Boolean> gameStatus = new ConcurrentHashMap<>();
	private final SimpMessagingTemplate messagingTemplate;
	private final HistoryRepository historyRepository;

	private static final String ACTION_CONNECT = "connect";
	private static final String ACTION_DISCONNECT = "disconnect";


	public SessionWebsocketController(SessionDao sessionDao,
									  SessionService sessionService,
									  SimpMessagingTemplate messagingTemplate,
									  HistoryRepository historyRepository) {
		this.sessionDao = sessionDao;
		this.sessionService = sessionService;
		this.messagingTemplate = messagingTemplate;
		this.historyRepository = historyRepository;
	}

	// Map<gameId, Map<playerId, List<buttonIdClickedInOrder>>>
	private final Map<String, Map<String, List<Integer>>> playerProgression = new ConcurrentHashMap<>();

	@MessageMapping("/connect")
	public void connectToGame(PlayerSessionRequest request) {
		processPlayerEvent(request, ACTION_CONNECT);
	}

	@MessageMapping("/disconnect")
	public void disconnectFromGame(PlayerSessionRequest request) {
		processPlayerEvent(request, ACTION_DISCONNECT);
	}

	private void processPlayerEvent(PlayerSessionRequest request, String action) {
		if (isInvalidRequest(request)) {
			sendErrorMessage("Invalid data provided for " + action);
			return;
		}
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

	private boolean isInvalidRequest(PlayerSessionRequest request) {
		return request.getGameId() == null || request.getGameId().isEmpty() ||
				request.getPlayerId() == null || request.getPlayerId().isEmpty() ||
				request.getPlayerName() == null || request.getPlayerName().isEmpty();
	}

	private void notifyPlayers(String gameId) {
		List<Player> players = sessionDao.getPlayersInSession(gameId);
		PlayerListResponse response = new PlayerListResponse("players", new PlayerData(players));
		messagingTemplate.convertAndSend("/topic/game/" + gameId, response);
	}

	@MessageMapping("/start")
	public void startGameSession(Map<String, Object> message) {
		try {
			Object data = message.get("data");
			String gameId = (String) ((Map<?, ?>) data).get("gameId");
			List<Player> players = sessionDao.getPlayersInSession(gameId);

			if (players == null || players.size() < 2) {
				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "error",
						"message", "Il faut minimum 2 joueurs pour commencer la partie !"
				));
				return;
			}

			if (sessionService.getGameSession(gameId) == null) {
				sessionService.createSession(gameId, sessionService.generateTurnsFromDb(players, 10));
			}

			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of("type", "start"));
			sendNextTurn(gameId);

		} catch (Exception e) {
			sendErrorMessage("Failed to process 'start' message: " + e.getMessage());
		}
	}

	@MessageMapping("/finish-operation")
	public void handleFinishOperation(Map<String, Object> message) {
		Object data = message.get("data");
		if (!(data instanceof Map)) return;
		Map<?, ?> dataMap = (Map<?, ?>) data;

		// Log complet reçu (formaté)
		try {
			ObjectMapper mapper = new ObjectMapper();
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dataMap);
			System.out.println("Received finish-operation data from front:\n" + json);
		} catch (Exception e) {
			System.out.println("Error logging finish-operation data: " + e.getMessage());
		}

		String gameId = (String) dataMap.get("gameId");
		String playerId = (String) dataMap.get("operator");
		Integer clickedButtonId = (Integer) dataMap.get("result");

		if (gameId == null || playerId == null || clickedButtonId == null) {
			sendErrorMessage("gameId, playerId ou buttonId manquant dans finish-operation");
			return;
		}

		GameSession session = sessionService.getGameSession(gameId);
		if (session == null) return;

		int currentTurnIdx = session.getCurrentTurn() - 1;
		if (currentTurnIdx < 0 || currentTurnIdx >= session.getTurns().size()) {
			sendErrorMessage("Tour courant invalide");
			return;
		}

		List<OperationMessage> currentTurnOps = session.getTurns().get(currentTurnIdx);
		Optional<OperationMessage> optInspectorOp = currentTurnOps.stream()
				.filter(op -> "inspector".equals(op.getData().getRole()))
				.findFirst();

		if (optInspectorOp.isEmpty()) {
			sendErrorMessage("Pas d'opération inspector trouvée pour ce tour");
			return;
		}

		OperationMessage inspectorOp = optInspectorOp.get();
		List<Integer> expectedSequence = inspectorOp.getData().getResult().getButtons().getIds();

		playerProgression.putIfAbsent(gameId, new ConcurrentHashMap<>());
		Map<String, List<Integer>> progressByPlayer = playerProgression.get(gameId);
		progressByPlayer.putIfAbsent(playerId, new ArrayList<>());
		List<Integer> currentProgress = progressByPlayer.get(playerId);

		int expectedIndex = currentProgress.size();

		// Log comparaison front/back
		System.out.println("Comparaison bouton cliqué avec séquence attendue :");
		System.out.println("Bouton cliqué par le joueur : " + clickedButtonId);
		System.out.println("Prochaine valeur attendue dans la séquence : " +
				(expectedIndex < expectedSequence.size() ? expectedSequence.get(expectedIndex) : "Fin de séquence"));

		if (expectedIndex < expectedSequence.size() && clickedButtonId.equals(expectedSequence.get(expectedIndex))) {
			// Clic correct
			currentProgress.add(clickedButtonId);

			if (currentProgress.size() == expectedSequence.size()) {
				// Séquence terminée => stop timer, reset progression
				ScheduledExecutorService scheduler = gameSchedulers.get(gameId);
				if (scheduler != null) {
					scheduler.shutdownNow();
					gameSchedulers.remove(gameId);
				}

				progressByPlayer.put(playerId, new ArrayList<>());

				System.out.println("Player " + playerId + " a validé la séquence complète pour game " + gameId);
				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "success",
						"message", "Séquence complète validée !",
						"playerId", playerId
				));

				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "integrity",
						"data", Map.of("integrity", session.getIntegrity())
				));

				sendNextTurn(gameId);
			} else {
				System.out.println("Player " + playerId + " a validé une partie de la séquence : " + currentProgress);
				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "progress",
						"message", "Bouton correct, continue la séquence.",
						"playerId", playerId,
						"progress", currentProgress
				));
			}

		} else {
			// Clic incorrect, reset progression
			progressByPlayer.put(playerId, new ArrayList<>());

			System.out.println("Player " + playerId + " a cliqué un mauvais bouton (" + clickedButtonId + ") pour game " + gameId);
			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
					"type", "error",
					"message", "Bouton incorrect, recommence la séquence.",
					"playerId", playerId,
					"clickedButtonId", clickedButtonId
			));
		}
	}




	private void sendNextTurn(String gameId) {
		GameSession session = sessionService.getGameSession(gameId);
		if (session == null) return;
		List<OperationMessage> nextOps = session.getNextTurnForAllPlayers();

		if (nextOps != null) {
			int maxDuration = nextOps.stream()
					.mapToInt(op -> op.getData().getDuration())
					.max()
					.orElse(10);

			for (OperationMessage op : nextOps) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(Map.of(
							"type", "operation",
							"data", op
					));
					System.out.println("ENVOI COMPLET AU FRONT : " + json);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "operation",
						"data", op
				));
			}

			ScheduledExecutorService scheduler = gameSchedulers.computeIfAbsent(gameId,
					id -> java.util.concurrent.Executors.newSingleThreadScheduledExecutor());
			scheduler.schedule(() -> {
				GameSession sess = sessionService.getGameSession(gameId);
				if (sess == null) return;

				int newIntegrity = sess.getIntegrity() - 20;
				sess.setIntegrity(newIntegrity);

				try {
					ObjectMapper objectMapper = new ObjectMapper();
					String json = objectMapper.writeValueAsString(Map.of(
							"type", "integrity",
							"data", Map.of("integrity", newIntegrity)
					));
					System.out.println("ENVOI INTEGRITY AUTO : " + json);
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
						"type", "integrity",
						"data", Map.of("integrity", newIntegrity)
				));

				if (newIntegrity <= 0) {
					int turnsDone = sess.getCurrentTurn();

					messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
							"type", "destroyed",
							"data", Map.of("turns", turnsDone)
					));
					endGame(gameId);
				} else {
					sendNextTurn(gameId);
				}
			}, maxDuration, java.util.concurrent.TimeUnit.SECONDS);

		} else {
			int turnsDone = session.getCurrentTurn();

			messagingTemplate.convertAndSend("/topic/game/" + gameId, Map.of(
					"type", "victory",
					"data", Map.of("turns", turnsDone)
			));
			endGame(gameId);
		}
	}

	private void endGame(String gameId) {
		gameStatus.remove(gameId);
		GameSession session = sessionService.getGameSession(gameId);

		if (session != null) {
			History history = new History();
			history.setIdHistory(gameId);
			history.setTurnsPlayed(String.valueOf(session.getCurrentTurn()));
			history.setResultGame("Integrity at end: " + session.getIntegrity());

			try {
				historyRepository.save(history);
				System.out.println("Historique sauvegardé pour gameId " + gameId);
			} catch (Exception e) {
				System.err.println("Erreur sauvegarde historique : " + e.getMessage());
			}
		}

		sessionService.endSession(gameId);
		ScheduledExecutorService scheduler = gameSchedulers.get(gameId);
		if (scheduler != null) {
			scheduler.shutdown();
			gameSchedulers.remove(gameId);
		}
	}

	private void sendErrorMessage(String message) {
		messagingTemplate.convertAndSend("/topic/game", new PlayerSessionResponse("error", message));
	}

}
