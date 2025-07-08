package com.spaceoperators.model.entity;

import com.spaceoperators.model.response.OperationMessage;
import java.util.List;

public class GameSession {
    private final List<List<OperationMessage>> turns;
    private int currentTurn = 0;
    private int integrity = 100;

    public GameSession(List<List<OperationMessage>> turns) {
        this.turns = turns;
    }

    public List<OperationMessage> getNextTurnForAllPlayers() {
        if (currentTurn >= turns.size()) return null;
        return turns.get(currentTurn++);
    }

    public List<List<OperationMessage>> getTurns() {
        return turns;
    }

    public int getIntegrity() {
        return integrity;
    }

    public void decreaseIntegrity(int amount) {
        this.integrity = Math.max(0, this.integrity - amount);
    }

    public int getCurrentTurn() {
        return this.currentTurn;
    }

    public int getTotalTurns() {
        return this.turns.size();
    }

    public void setIntegrity(int integrity) {
        this.integrity = integrity;
    }


}
