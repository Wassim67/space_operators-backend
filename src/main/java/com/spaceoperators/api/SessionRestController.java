package com.spaceoperators.api;

import com.spaceoperators.repository.SessionDao;
import com.spaceoperators.model.entity.Player;
import com.spaceoperators.model.response.PlayerData;
import com.spaceoperators.model.response.PlayerListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/player")
@CrossOrigin(origins = "*")
public class SessionRestController {

    private SessionDao sessionDao;
    private SimpMessagingTemplate messagingTemplate;

    public SessionRestController(SessionDao sessionDao, SimpMessagingTemplate messagingTemplate) {
        this.sessionDao = sessionDao;
        this.messagingTemplate = messagingTemplate;
    }
    /**
     * API REST pour mettre à jour l'état "isReady" d'un joueur. (envoie aussi un json pour le front avec la mise à jour des players)
     *
     * @param idPlayer L'identifiant du joueur.
     * @param player   L'objet contenant l'état "isReady".
     * @return Une réponse JSON indiquant le succès ou l'échec.
     */
    @PostMapping("/ready/{idPlayer}")
    public ResponseEntity<Map<String, Object>> updateReadyStatus(
            @PathVariable("idPlayer") String idPlayer,
            @RequestBody Player player) {
        try {
            if (player.getReady() == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "'isReady' field is missing"));
            }

            sessionDao.updatePlayerReady(idPlayer, player.getReady());
            String gameId = sessionDao.getGameIdForPlayer(idPlayer);

            // Envoie la mise à jour aux abonnés WebSocket
            List<Player> players = sessionDao.getPlayersInSession(gameId);
            PlayerListResponse response = new PlayerListResponse("players", new PlayerData(players));
            messagingTemplate.convertAndSend("/topic/game/" + gameId, response);

            return ResponseEntity.ok(Map.of("message", "Player readiness updated successfully"));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
