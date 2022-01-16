package com.piociek.fxgl.pong;

public enum Direction {
    LEFT(-1), RIGHT(1);

    Direction(int value) {
        this.value = value;
    }

    int value;
}
