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

    public void registerPlayer(String id, String email, String name, String hashedPwd, String role) {
        String sql = "INSERT INTO player (id_player, email, name, pwd, role) VALUES (:id, :email, :name, :pwd, :role)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("email", email)
                .addValue("name", name)
                .addValue("pwd", hashedPwd)
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

}

