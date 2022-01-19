package com.piociek.fxgl.spaceinvaders.movement;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum MovementDirection {
    LEFT(-1, 0), RIGHT(1, 0), DOWN(0, 1);

    @Getter
    private final int dx, dy;
}
