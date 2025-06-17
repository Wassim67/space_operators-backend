package com.spaceoperators.controller;

import com.spaceoperators.repository.PlayerDao;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/player")
public class PlayerProfileController {

    private final PlayerDao playerDao;

    public PlayerProfileController(PlayerDao playerDao) {
        this.playerDao = playerDao;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile() {
        String playerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // TODO: Supprimer cette vérification de "anonymousUser" une fois le JWT bien intégré
        //if ("anonymousUser".equals(playerId)) {
          //  throw new RuntimeException("Unauthorized: No valid player ID found");
        //}

        int gameCount = playerDao.countGamesByPlayerId(playerId);
        return Map.of("playerId", playerId, "gamesPlayed", gameCount);
    }
}
