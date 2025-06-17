package com.spaceoperators.model.request;

public class PlayerSessionRequest {
	private String gameId;
	private String playerId;
	private String playerName;
	private Boolean isReady;

	// Getters et Setters
	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
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

	public Boolean getIsReadyPlayer() {
		return isReady;
	}
}
