package com.spaceoperators.dao;

import com.spaceoperators.model.Player;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SessionDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public SessionDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    /**
     * Insère une nouvelle session dans la base de données.
     * @param idGame L'ID du jeu associé (obligatoire, doit exister dans game).
     * @param idPlayer L'ID du joueur associé (obligatoire).
     * @return Le nombre de lignes affectées (1 si l'insertion a réussi, 0 sinon).
     */
    public int insertSession(String idGame, String idPlayer, String namePlayer, Boolean isReady) {
        String sql = "INSERT INTO session (id_game, id_player, name_player, isReady) VALUES (:idGame, :idPlayer, :namePlayer, :isReady);";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idGame", idGame)
                .addValue("idPlayer", idPlayer)
                .addValue("namePlayer", namePlayer)
                .addValue("isReady", isReady);
        return jdbcTemplate.update(sql, params);
    }

    /**
     * Met à jour l'état de "isReady" pour un joueur dans la table session.
     * @param idPlayer L'ID du joueur associé (obligatoire).
     * @param isReady L'état de readiness (true ou false).
     * @return Le nombre de lignes affectées (1 si la mise à jour a réussi, 0 sinon).
     */
    public int updatePlayerReady(String idPlayer, boolean isReady) {
        String sql = "UPDATE session SET isReady = :isReady WHERE id_player = :idPlayer";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idPlayer", idPlayer)
                .addValue("isReady", isReady);
        return jdbcTemplate.update(sql, params);
    }


    /**
     * Supprime un joueur d'une session.
     * @param gameId L'ID du jeu.
     * @param playerId L'ID du joueur à supprimer.
     */
    public void removeSession(String gameId, String playerId) {
        String sql = "DELETE FROM session WHERE id_game = :gameId AND id_player = :playerId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gameId", gameId)
                .addValue("playerId", playerId);
        jdbcTemplate.update(sql, params);
    }

    /**
     * Récupère le gameId pour un joueur donné.
     * @param playerId L'ID du joueur.
     * @return Le gameId du joueur.
     */
    public String getGameIdForPlayer(String playerId) {
        String sql = "SELECT id_game FROM session WHERE id_player = :idPlayer LIMIT 1";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idPlayer", playerId);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }


    /**
     * Met à jour le champ "isStarted" pour une session donnée.
     *
     * @param gameId L'identifiant du jeu
     * @return true si au moins une ligne a été mise à jour, false sinon.
     */
    public boolean startSession(String gameId) {
        String sql = "UPDATE session SET isStarted = true WHERE id_game = :gameId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gameId", gameId);
        int rowsAffected = jdbcTemplate.update(sql, params);
        return rowsAffected > 0; // Retourne true si au moins une ligne a été mise à jour
    }



    /**
     * Récupère la liste des joueurs dans une session donnée.
     * @param gameId L'ID du jeu pour lequel on récupère la liste des joueurs.
     * @return La liste des joueurs connectés à la session.
     */
    public List<Player> getPlayersInSession(String gameId) {
        String sql = "SELECT id_player, name_player, isReady FROM session WHERE id_game = :gameId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gameId", gameId);

        // Todo : Ajouter la clé étrangère dans la base de données pour retrouver le nom du joueur !!!!!! (eviter le null)
        return jdbcTemplate.query(sql, params, new RowMapper<Player>() {
            @Override
            public Player mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
                String playerId = rs.getString("id_player");
                String playerName = rs.getString("name_player");
                Boolean isReady = rs.getBoolean("isReady");
                System.out.println("SQL isReady Value: " + rs.getBoolean("isReady"));
                //String playerName = "WSM";
                return new Player(playerId, playerName, isReady);
            }
        });
    }

}
