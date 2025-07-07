package com.spaceoperators.repositories;

import com.spaceoperators.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, String> {
    Optional<Player> findByEmail(String email);
}
