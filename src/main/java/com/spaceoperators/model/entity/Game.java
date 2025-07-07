package com.spaceoperators.model.entity;


import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "game")
public class Game {

    @Id
    @Column(name = "id_game")
    private String idGame;

    @Column(name = "choice_theme")
    private String choiceTheme;

    @Column(name = "integrity", nullable = false)
    private int integrity;

    @Column(name = "name")
    private String name;

    @Column(name = "state")
    private String state;

    @ManyToOne
    @JoinColumn(name = "id_history")
    private History history;

    // Getters/setters
    public String getIdGame() { return idGame; }
    public void setIdGame(String idGame) { this.idGame = idGame; }

    public String getChoiceTheme() { return choiceTheme; }
    public void setChoiceTheme(String choiceTheme) { this.choiceTheme = choiceTheme; }

    public int getIntegrity() { return integrity; }
    public void setIntegrity(int integrity) { this.integrity = integrity; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public History getHistory() { return history; }
    public void setHistory(History history) { this.history = history; }
}
