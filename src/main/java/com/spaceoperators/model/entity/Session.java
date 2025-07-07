package com.spaceoperators.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_game")
    private Game game;

    @Column(name = "id_player")
    private String idPlayer;

    @Column(name = "name_player")
    private String namePlayer;

    @Column(name = "isReady", nullable = false)
    private boolean isReady;

    // Getters/setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public String getIdPlayer() { return idPlayer; }
    public void setIdPlayer(String idPlayer) { this.idPlayer = idPlayer; }

    public String getNamePlayer() { return namePlayer; }
    public void setNamePlayer(String namePlayer) { this.namePlayer = namePlayer; }

    public boolean isReady() { return isReady; }
    public void setReady(boolean ready) { isReady = ready; }
}
