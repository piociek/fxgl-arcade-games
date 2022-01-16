package snake;

import lombok.Getter;

public enum MovementDirection {
    UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0), NONE(0, 0);

    @Getter
    private final int dx, dy;

    MovementDirection(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}
