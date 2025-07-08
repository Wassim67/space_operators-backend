package com.spaceoperators.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class PlayerDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlayerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public void registerPlayer(String id, String email, String playerName, String hashedPwd, String role) {
        String sql = "INSERT INTO player (playerId, email, playerName, password, role) VALUES (:id, :email, :playerName, :password, :role)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("email", email)
                .addValue("playerName", playerName)
                .addValue("password", hashedPwd)
                .addValue("role", role);
        jdbcTemplate.update(sql, params);
    }



    public Optional<Map<String, Object>> getPlayerByEmail(String email) {
        String sql = "SELECT * FROM player WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email", email);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public int countGamesByPlayerId(String playerId) {
        String sql = "SELECT COUNT(*) FROM session WHERE id_player = :playerId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("playerId", playerId);
        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    public Optional<Map<String, Object>> getPlayerById(String playerId) {
        System.out.println("GET BY ID");
        String sql = "SELECT playerId, email, playerName, role FROM player WHERE playerId = :playerId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("playerId", playerId);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, params);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<Map<String, Object>> getAllPlayers() {
        String sql = "SELECT playerId, playerName, email, role FROM player";
        return jdbcTemplate.queryForList(sql, new MapSqlParameterSource());
    }

    public List<Map<String, Object>> getPlayerHistory(String playerId) {
        String sql = "SELECT * FROM history WHERE playerId = :playerId ORDER BY someDateColumn DESC";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("playerId", playerId);
        return jdbcTemplate.queryForList(sql, params);
    }
}

