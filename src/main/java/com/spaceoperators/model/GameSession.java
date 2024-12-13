package com.spaceoperators.model;

import com.spaceoperators.model.response.OperationMessage;
import java.util.List;

public class GameSession {
    private final List<OperationMessage> turns;
    private int currentTurn;
    private boolean isActive;
    private int integrity;
    private boolean currentTurnCompleted;

    public GameSession(List<OperationMessage> turns) {
        this.turns = turns;
        this.currentTurn = 0;
        this.isActive = true;
        this.integrity = 100; // Commence avec 100% d'intégrité
        this.currentTurnCompleted = false;
    }

    public OperationMessage getNextTurn() {
        if (!isActive || currentTurn >= turns.size()) {
            return null;
        }
        this.currentTurnCompleted = false;
        return turns.get(currentTurn++);
    }

    public int getDuration() {
        if (currentTurn > 0 && currentTurn <= turns.size()) {
            return turns.get(currentTurn - 1).getData().getDuration();
        }
        return -1;
    }

    public boolean isActive() {
        return isActive;
    }

    public void endSession() {
        this.isActive = false;
    }

    public int getIntegrity() {
        return integrity;
    }

    public void decreaseIntegrity(int amount) {
        this.integrity = Math.max(0, this.integrity - amount);
    }

    public void setTurnCompleted(boolean completed) {
        this.currentTurnCompleted = completed;
    }

    public boolean isTurnCompleted() {
        return this.currentTurnCompleted;
    }

    public int getCurrentTurn() {
        return this.currentTurn;
    }

    public int getTotalTurns() {
        return this.turns.size();
    }
}