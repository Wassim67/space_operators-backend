package com.spaceoperators.model.response;

import com.spaceoperators.model.entity.Player;
import java.util.List;

public class PlayerData {
    private List<Player> players;

    public PlayerData(List<Player> players) {
        this.players = players;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "players=" + players +
                '}';
    }
}
