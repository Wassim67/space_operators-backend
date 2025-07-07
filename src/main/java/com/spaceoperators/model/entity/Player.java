package com.spaceoperators.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
@Table(name = "Player")
public class Player {

    @Id
    @Column(name = "playerId")
    private String playerId;

    @Column(name = "playerName")
    private String playerName;

    @Transient
    private Boolean isReady;
   // private String name;
    private String email;
    private String role;
    private String password;
//    private String id_player;

    public Player(String playerName, String email, String role, String password, String playerId) {
       // this.name = name;
        this.email = email;
        this.role = role;
        this.password = password;
        this.playerId = playerId;
    }



    // Constructeurs, getters et setters
    public Player(String playerId, String playerName, Boolean isReady) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isReady = isReady;
    }

    public Player() {

    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }


    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Boolean getReady() {
        return isReady;
    }

    public void setReady(Boolean ready) {
        isReady = ready;
    }
    @Override
    public String toString() {
        return "Player{" +
                "playerId='" + playerId + '\'' +
                ", playerName='" + playerName + '\'' +
                ", isReady=" + isReady +
                '}';
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    public String getId_player() {
//        return id_player;
//    }
//
//    public void setId_player(String id_player) {
//        this.id_player = id_player;
//    }
}
