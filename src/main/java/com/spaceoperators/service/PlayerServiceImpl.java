package com.spaceoperators.service;

import com.spaceoperators.model.entity.Player;
import com.spaceoperators.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

    public Player findById(String id) {
        return playerRepository.findById(id).orElse(null);
    }

    public void update(String id, Player player) {
        if (playerRepository.existsById(id)) {
            player.setPlayerId(id); // s'assurer que l’ID reste cohérent
            playerRepository.save(player);
        }
    }

    public void delete(String id) {
        playerRepository.deleteById(id);
    }
}
