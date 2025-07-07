package com.spaceoperators.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "turn")
public class Turn {

    @Id
    @Column(name = "id_turn")
    private String idTurn;

    @Column(name = "duration")
    private String duration;

    @Column(name = "number")
    private String number;

    @Column(name = "role")
    private String role;

    @ManyToOne
    @JoinColumn(name = "id_game", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "id_task", nullable = false)
    private Task task;

    // Getters/setters
    public String getIdTurn() { return idTurn; }
    public void setIdTurn(String idTurn) { this.idTurn = idTurn; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }

    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
}
