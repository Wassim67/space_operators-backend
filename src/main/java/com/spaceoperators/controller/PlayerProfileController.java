package com.spaceoperators.controller;

import com.spaceoperators.repository.PlayerDao;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/profile2")
    public Map<String, Object> getProfile2() {
        System.out.println("PROFILE 2 //////////");

        String playerId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("auth = " + auth);
        System.out.println("principal = " + auth.getPrincipal());
        if (auth == null) {
            System.out.println("AUTHENTICATION IS NULL");
            throw new RuntimeException("User not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal == null) {
            System.out.println("PRINCIPAL IS NULL");
            throw new RuntimeException("User principal is null");
        }
        System.out.println("Principal class = " + principal.getClass().getName());
        System.out.println("Principal toString = " + principal.toString());
        System.out.println("PLAYER ID " + playerId);
        Map<String, Object> player = playerDao.getPlayerById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        int gameCount = playerDao.countGamesByPlayerId(playerId);
        System.out.println("GAME PLAYER " + player);
        return Map.of(
                "playerId", player.get("playerId"),
                "playerName", player.get("playerName"),
                "email", player.get("email"),
                "role", player.get("role"),
                "gamesPlayed", gameCount
        );
    }

    @GetMapping("/all")
    public List<Map<String, Object>> getAllPlayers() {
        return playerDao.getAllPlayers();
    }



}


