package com.piociek.fxgl.spaceinvaders.movement;

import com.almasb.fxgl.entity.Entity;

import java.util.List;

import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.almasb.fxgl.dsl.FXGL.getGameWorld;
import static com.piociek.fxgl.spaceinvaders.SpaceInvadersConstants.*;
import static com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersType.ENEMY;

public class EnemyMovement {

    private final Entity[][] enemies;
    private MovementDirection currentDirection = MovementDirection.RIGHT;
    private MovementDirection previousXDirection = MovementDirection.RIGHT;
    private int boundLeft = 0;
    private int boundRight = ENEMY_COUNT_PER_ROW - 1;
    private double moveYPositionStart;
    private double enemySpeed = ENEMY_SPEED;

    public EnemyMovement(Entity[][] enemies) {
        this.enemies = enemies;
        this.moveYPositionStart = enemies[0][0].getY();
    }

    public void move() {
        List<Entity> enemyEntities = getGameWorld().getEntities()
                .stream()
                .filter(e -> e.getType().equals(ENEMY))
                .filter(Entity::isActive)
                .toList();

        if (!enemyEntities.isEmpty()) {
            enemyEntities.forEach(e -> {
                e.translateX(currentDirection.getDx() * enemySpeed);
                e.translateY(currentDirection.getDy() * enemySpeed);
            });

            checkForMovementChange();
        }
    }

    public void checkMovementBounds() {
        checkLeftMovementBound();
        checkRightMovementBound();
    }

    private void checkForMovementChange() {
        switch (currentDirection) {
            case RIGHT -> {
                Entity entity = getActiveEnemyFromColumn(boundRight);
                if (entity.getRightX() >= getAppWidth() - ENEMY_WIDTH) {
                    currentDirection = MovementDirection.DOWN;
                    previousXDirection = MovementDirection.RIGHT;
                }
            }
            case LEFT -> {
                Entity entity = getActiveEnemyFromColumn(boundLeft);
                if (entity.getX() <= ENEMY_WIDTH) {
                    currentDirection = MovementDirection.DOWN;
                    previousXDirection = MovementDirection.LEFT;
                }
            }
            case DOWN -> {
                Entity entity = getActiveEnemyFromTopRow();
                if (entity.getY() >= moveYPositionStart + ENEMY_SPACING) {
                    currentDirection = previousXDirection ==
                            MovementDirection.RIGHT ? MovementDirection.LEFT : MovementDirection.RIGHT;
                    moveYPositionStart = entity.getY();
                    enemySpeed += ENEMY_SPEED_INCREASE;
                }
            }
        }
    }

    private Entity getActiveEnemyFromColumn(int column) {
        for (int y = 0; y < ENEMY_ROW_COUNT; y++) {
            if (enemies[column][y].isActive()) {
                return enemies[column][y];
            }
        }
        throw new RuntimeException(LOG_EXCEPTION_NOT_POSSIBLE);
    }

    private Entity getActiveEnemyFromTopRow() {
        for (int y = 0; y < ENEMY_ROW_COUNT; y++) {
            for (int x = 0; x < ENEMY_COUNT_PER_ROW; x++) {
                if (enemies[x][y].isActive()) {
                    return enemies[x][y];
                }
            }
        }
        throw new RuntimeException(LOG_EXCEPTION_NOT_POSSIBLE);
    }

    private void checkLeftMovementBound() {
        do {
            for (int y = ENEMY_ROW_COUNT - 1; y >= 0; y--) {
                if (enemies[boundLeft][y].isActive()) {
                    return;
                }
            }
            boundLeft++;
        } while (boundLeft <= boundRight);
    }

    private void checkRightMovementBound() {
        do {
            for (int y = ENEMY_ROW_COUNT - 1; y >= 0; y--) {
                if (enemies[boundRight][y].isActive()) {
                    return;
                }
            }
            boundRight--;
        } while (boundLeft <= boundRight);
    }
}
