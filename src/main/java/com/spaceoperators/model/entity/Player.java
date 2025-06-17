package com.spaceoperators.model.entity;

public class Player {
    private String playerId;
    private String playerName;
    private Boolean isReady;

    // Constructeurs, getters et setters
    public Player(String playerId, String playerName, Boolean isReady) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.isReady = isReady;
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
}
