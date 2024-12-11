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
    public int insertSession(String idGame, String idPlayer, String namePlayer) {
        String sql = "INSERT INTO session (id_game, id_player, name_player) VALUES (:idGame, :idPlayer, :namePlayer)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idGame", idGame)
                .addValue("idPlayer", idPlayer)
                .addValue("namePlayer", namePlayer);
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
     * Récupère la liste des joueurs dans une session donnée.
     * @param gameId L'ID du jeu pour lequel on récupère la liste des joueurs.
     * @return La liste des joueurs connectés à la session.
     */
    public List<Player> getPlayersInSession(String gameId) {
        String sql = "SELECT id_player, name_player FROM session WHERE id_game = :gameId";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("gameId", gameId);

        // Todo : Ajouter la clé étrangère dans la base de données pour retrouver le nom du joueur !!!!!! (eviter le null)
        return jdbcTemplate.query(sql, params, new RowMapper<Player>() {
            @Override
            public Player mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
                String playerId = rs.getString("id_player");
                String playerName = rs.getString("name_player");
                //String playerName = "WSM";
                return new Player(playerId, playerName);
            }
        });
    }
}
