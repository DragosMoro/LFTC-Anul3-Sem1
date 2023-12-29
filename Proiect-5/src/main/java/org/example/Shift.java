package org.example;

public class Shift extends Action {
    private int nextState;

    public Shift(int nextState) {
        this.nextState = nextState;
    }

    public int getNextState() {
        return nextState;
    }

    @Override
    public String toString() {
        return "s" + nextState;
    }
}