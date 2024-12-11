package com.spaceoperators.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class GameDao {

    private final JdbcTemplate jdbcTemplate;

    public GameDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Insère un nouveau jeu dans la base de données.
     * @param idGame L'ID du jeu (doit être unique).
     * @param choiceTheme Le thème choisi pour le jeu.
     * @param integrity L'intégrité du jeu.
     * @param name Le nom du jeu.
     * @param state L'état du jeu.
     * @param idHistory L'ID de l'historique associé (optionnel, peut être null).
     */
    public int insertGame(int idGame, String choiceTheme, int integrity, String name, String state, String idHistory) {
        String sql = "INSERT INTO game (id_game, choice_theme, integrity, name, state, id_history) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, idGame, choiceTheme, integrity, name, state, idHistory);
    }
}

