package com.spaceoperators.controller.websocket;

import com.spaceoperators.model.response.PlayerData;
import com.spaceoperators.model.response.PlayerListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.spaceoperators.dao.SessionDao;
import com.spaceoperators.model.ConnectRequest;
import com.spaceoperators.model.ConnectResponse;
import com.spaceoperators.model.Player;

import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class MessageController {

	@Autowired
	private SessionDao sessionDao;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	/**
	 * Traite les connexions des joueurs.
	 */
	@MessageMapping("/connect")
	public void connectToGame(ConnectRequest request) {
		processPlayerEvent(request, "connect");
	}

	/**
	 * Traite les déconnexions des joueurs.
	 */
	@MessageMapping("/disconnect")
	public void disconnectFromGame(ConnectRequest request) {
		processPlayerEvent(request, "disconnect");
	}

	/**
	 * Gère les événements des joueurs (connexion ou déconnexion).
	 */
	private void processPlayerEvent(ConnectRequest request, String action) {
		if (request.getGameId() == null || request.getGameId().isEmpty() ||
				request.getPlayerId() == null || request.getPlayerId().isEmpty() ||
				request.getPlayerName() == null || request.getPlayerName().isEmpty()) {
			sendErrorMessage("Invalid data provided for " + action);
			return;
		}

		try {
			if ("connect".equals(action)) {
				// Ajouter le joueur à la session
				sessionDao.insertSession(request.getGameId(), request.getPlayerId(), request.getPlayerName());
				System.out.println("Player connected: " + request.getPlayerName() +
						" (ID: " + request.getPlayerId() + ") to game " + request.getGameId());
			} else if ("disconnect".equals(action)) {
				// Retirer le joueur de la session
				sessionDao.removeSession(request.getGameId(), request.getPlayerId());
				System.out.println("Player disconnected: " + request.getPlayerName() +
						" (ID: " + request.getPlayerId() + ") from game " + request.getGameId());
			}

			// Récupérer la liste complète des joueurs connectés à la session
			List<Player> players = sessionDao.getPlayersInSession(request.getGameId());

			// Créer un objet JSON avec le format attendu
			PlayerListResponse playerListResponse = new PlayerListResponse("players", new PlayerData(players));

			// Notifier tous les abonnés de la liste mise à jour
			messagingTemplate.convertAndSend("/topic/game/" + request.getGameId(), playerListResponse);

		} catch (Exception e) {
			e.printStackTrace();
			sendErrorMessage("Failed to " + action + " player to game: " + e.getMessage());
		}
	}

	/**
	 * Envoie un message d'erreur à tous les abonnés.
	 */
	private void sendErrorMessage(String message) {
		messagingTemplate.convertAndSend("/topic/game", new ConnectResponse("error", message));
	}


}
