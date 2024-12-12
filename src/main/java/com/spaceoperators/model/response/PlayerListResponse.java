package com.spaceoperators.model.response;

public class PlayerListResponse {
    private final String type;
    private final PlayerData data;

    public PlayerListResponse(String type, PlayerData data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public PlayerData getData() {
        return data;
    }

    @Override
    public String toString() {
        return "PlayerListResponse{" +
                "type='" + type + '\'' +
                ", data=" + data +
                '}';
    }

}
