package com.spaceoperators.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "history")
public class History {

    @Id
    @Column(name = "id_history")
    private String idHistory;

    @Column(name = "result_game")
    private String resultGame;

    @Column(name = "turns_played")
    private String turnsPlayed;

    // Getters/setters
    public String getIdHistory() { return idHistory; }
    public void setIdHistory(String idHistory) { this.idHistory = idHistory; }

    public String getResultGame() { return resultGame; }
    public void setResultGame(String resultGame) { this.resultGame = resultGame; }

    public String getTurnsPlayed() { return turnsPlayed; }
    public void setTurnsPlayed(String turnsPlayed) { this.turnsPlayed = turnsPlayed; }
}
