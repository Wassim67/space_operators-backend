package com.spaceoperators.service;

import com.spaceoperators.model.entity.History;
import com.spaceoperators.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerStatsService {
    private final HistoryRepository historyRepository;

    public PlayerStatsService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public List<History> getStatsForPlayer(String playerId) {
        return historyRepository.findByPlayerId(playerId);
    }
}
