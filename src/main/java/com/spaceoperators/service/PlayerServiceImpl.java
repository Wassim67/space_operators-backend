package com.spaceoperators.service;

import com.spaceoperators.model.entity.Player;
import com.spaceoperators.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    @Override
    public void update(String id, Player updatedPlayer) {
        Optional<Player> optional = playerRepository.findById(id);
        if (optional.isPresent()) {
            Player existing = optional.get();
            existing.setPlayerName(updatedPlayer.getPlayerName());
            existing.setEmail(updatedPlayer.getEmail());
            existing.setRole(updatedPlayer.getRole());


            playerRepository.save(existing);
        }
    }

    public void delete(String id) {
        playerRepository.deleteById(id);
    }


}
