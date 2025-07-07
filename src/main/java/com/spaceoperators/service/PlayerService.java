package com.spaceoperators.service;

import com.spaceoperators.model.entity.Player;
import com.spaceoperators.repositories.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

public interface PlayerService {
    List<Player> findAll();
    Player findById(String id);
    void update(String id, Player player);
    void delete(String id);
}