package com.spaceoperators.controller;

import com.spaceoperators.model.entity.History;
import com.spaceoperators.service.PlayerStatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/player")
public class PlayerStatsController {

    private final PlayerStatsService playerStatsService;

    public PlayerStatsController(PlayerStatsService playerStatsService) {
        this.playerStatsService = playerStatsService;
    }

    @GetMapping("/{playerId}/stats")
    public List<History> getPlayerStats(@PathVariable String playerId) {
        return playerStatsService.getStatsForPlayer(playerId);
    }
}
