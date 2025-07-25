package com.spaceoperators.repository;

import com.spaceoperators.model.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, String> {

    Optional<Player> findByEmail(String email);

    int countByPlayerId(String playerId);

}
