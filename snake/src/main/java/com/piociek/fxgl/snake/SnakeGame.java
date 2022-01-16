package com.piociek.fxgl.snake;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.piociek.fxgl.snake.SnakeConstants.*;

public class SnakeGame extends GameApplication {

    private static final Logger LOGGER = Logger.get(SnakeGame.class);

    private MovementDirection currentMove = MovementDirection.RIGHT;
    private MovementDirection nextMove = MovementDirection.NONE;
    private LocalTimer nextMoveTimer;

    private SnakeBodyPart snakeHeadBodyPart;
    private Entity block;

    private final Runnable startNewGame = () -> {
        List<Entity> gameEntities = new ArrayList<>();
        SnakeBodyPart snakeBodyPart = snakeHeadBodyPart;
        do {
            gameEntities.add(snakeBodyPart.getBodyEntity());
            snakeBodyPart = snakeBodyPart.getNextBodyPart();
        } while (Objects.nonNull(snakeBodyPart));
        gameEntities.add(block);
        getGameWorld().removeEntities(gameEntities);
        createSnake();
        createNewBlock();
        currentMove = MovementDirection.RIGHT;
        nextMove = MovementDirection.NONE;
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL-Snake");
        settings.setVersion("0.1");
        settings.setWidth(GAME_WIDTH * BLOCK_SIZE);
        settings.setHeight(GAME_HEIGHT * BLOCK_SIZE);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new SnakeEntityFactory());

        createSnake();
        createNewBlock();

        nextMoveTimer = newLocalTimer();
        nextMoveTimer.capture();
    }

    private void createSnake() {
        SnakeBodyPart tail = new SnakeBodyPart(
                spawn(ENTITY_BLOCK, 4 * BLOCK_SIZE, 5 * BLOCK_SIZE),
                MovementDirection.RIGHT,
                null);
        SnakeBodyPart middle = new SnakeBodyPart(
                spawn(ENTITY_BLOCK, 5 * BLOCK_SIZE, 5 * BLOCK_SIZE),
                MovementDirection.RIGHT,
                tail);
        snakeHeadBodyPart = new SnakeBodyPart(
                spawn(ENTITY_BLOCK, 6 * BLOCK_SIZE, 5 * BLOCK_SIZE),
                MovementDirection.RIGHT,
                middle
        );
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put(VAR_HIGH_SCORE, 0);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.W, () -> nextMove = MovementDirection.UP);
        onKeyDown(KeyCode.UP, () -> nextMove = MovementDirection.UP);
        onKeyDown(KeyCode.A, () -> nextMove = MovementDirection.LEFT);
        onKeyDown(KeyCode.LEFT, () -> nextMove = MovementDirection.LEFT);
        onKeyDown(KeyCode.S, () -> nextMove = MovementDirection.DOWN);
        onKeyDown(KeyCode.DOWN, () -> nextMove = MovementDirection.DOWN);
        onKeyDown(KeyCode.D, () -> nextMove = MovementDirection.RIGHT);
        onKeyDown(KeyCode.RIGHT, () -> nextMove = MovementDirection.RIGHT);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (nextMoveTimer.elapsed(Duration.seconds(0.3))) {
            nextMoveTimer.capture();

            if (nextMove != MovementDirection.NONE) {
                currentMove = isMoveValid(currentMove, nextMove) ? nextMove : currentMove;
                LOGGER.info(LOG_MOVE_DIRECTION.formatted(currentMove));
                nextMove = MovementDirection.NONE;
            }

            Entity headBlock = snakeHeadBodyPart.getBodyEntity();

            if (doesEntityMoveOutOfScreen(headBlock, currentMove)) {
                finishGameWithMessage(LOG_EXCEPTION_SCREEN_END_COLLISION);
            }

            SnakeBodyPart snakeBodyPart = snakeHeadBodyPart.getNextBodyPart();
            do {
                if (doesEntitiesCollide(headBlock, snakeBodyPart.getBodyEntity(), currentMove)) {
                    finishGameWithMessage(LOG_EXCEPTION_SNAKE_BODY_COLLISION);
                }
                snakeBodyPart = snakeBodyPart.getNextBodyPart();
            } while (Objects.nonNull(snakeBodyPart));

            if (doesEntitiesCollide(headBlock, block, currentMove)) {
                LOGGER.info(LOG_NEW_BLOCK_ATTACHED);
                snakeHeadBodyPart = new SnakeBodyPart(block, currentMove, snakeHeadBodyPart);

                if (getSnakeLength() == GAME_HEIGHT * GAME_WIDTH) {
                    finishGameWithMessage(LOG_WIN);
                }

                createNewBlock();
            } else {
                moveSnakeBodyPart(snakeHeadBodyPart, currentMove);
            }
        }
    }

    private boolean isMoveValid(MovementDirection currentDirection, MovementDirection nextDirection) {
        return switch (currentDirection) {
            case UP -> nextDirection != MovementDirection.DOWN;
            case DOWN -> nextDirection != MovementDirection.UP;
            case LEFT -> nextDirection != MovementDirection.RIGHT;
            case RIGHT -> nextDirection != MovementDirection.LEFT;
            case NONE -> false;
        };
    }

    private boolean doesEntitiesCollide(Entity snakeHead, Entity block, MovementDirection direction) {
        return switch (direction) {
            case UP -> getEntityCorner(snakeHead, Corner.TOP_LEFT).equals(getEntityCorner(block, Corner.BOTTOM_LEFT))
                    && getEntityCorner(snakeHead, Corner.TOP_RIGHT).equals(getEntityCorner(block, Corner.BOTTOM_RIGHT));
            case DOWN -> getEntityCorner(snakeHead, Corner.BOTTOM_LEFT).equals(getEntityCorner(block, Corner.TOP_LEFT))
                    && getEntityCorner(snakeHead, Corner.BOTTOM_RIGHT).equals(getEntityCorner(block, Corner.TOP_RIGHT));
            case LEFT -> getEntityCorner(snakeHead, Corner.TOP_LEFT).equals(getEntityCorner(block, Corner.TOP_RIGHT))
                    && getEntityCorner(snakeHead, Corner.BOTTOM_LEFT).equals(getEntityCorner(block, Corner.BOTTOM_RIGHT));
            case RIGHT -> getEntityCorner(snakeHead, Corner.TOP_RIGHT).equals(getEntityCorner(block, Corner.TOP_LEFT))
                    && getEntityCorner(snakeHead, Corner.BOTTOM_RIGHT).equals(getEntityCorner(block, Corner.BOTTOM_LEFT));
            case NONE -> throw new RuntimeException(LOG_EXCEPTION_NOT_POSSIBLE);
        };
    }

    private boolean doesEntityMoveOutOfScreen(Entity snakeHead, MovementDirection direction) {
        return switch (direction) {
            case UP -> snakeHead.getY() == 0;
            case DOWN -> snakeHead.getBottomY() == GAME_HEIGHT * BLOCK_SIZE;
            case LEFT -> snakeHead.getX() == 0;
            case RIGHT -> snakeHead.getRightX() == GAME_WIDTH * BLOCK_SIZE;
            case NONE -> throw new RuntimeException(LOG_EXCEPTION_NOT_POSSIBLE);
        };
    }

    private Point2D getEntityCorner(Entity entity, Corner corner) {
        return switch (corner) {
            case TOP_LEFT -> new Point2D(entity.getX(), entity.getY());
            case TOP_RIGHT -> new Point2D(entity.getRightX(), entity.getY());
            case BOTTOM_LEFT -> new Point2D(entity.getX(), entity.getBottomY());
            case BOTTOM_RIGHT -> new Point2D(entity.getRightX(), entity.getBottomY());
        };
    }

    private void moveSnakeBodyPart(SnakeBodyPart snakeBodyPart, MovementDirection movementDirection) {
        snakeBodyPart.getBodyEntity().translateX(BLOCK_SIZE * movementDirection.getDx());
        snakeBodyPart.getBodyEntity().translateY(BLOCK_SIZE * movementDirection.getDy());
        if (snakeBodyPart.getNextBodyPart() != null) {
            moveSnakeBodyPart(snakeBodyPart.getNextBodyPart(), snakeBodyPart.getMovementDirection());
        }
        snakeBodyPart.setMovementDirection(movementDirection);
    }

    private void createNewBlock() {
        boolean collide;
        int x, y;
        do {
            x = random(0, GAME_WIDTH - 1);
            y = random(0, GAME_HEIGHT - 1);
            collide = doesCollideWithSnakeBodyPart(snakeHeadBodyPart, x * BLOCK_SIZE, y * BLOCK_SIZE);
            if (collide) {
                LOGGER.info(LOG_NEW_BLOCK_COLLISION.formatted(x, y));
            }
        } while (collide);
        LOGGER.info(LOG_NEW_BLOCK_CREATED.formatted(x, y));
        block = spawn(ENTITY_BLOCK, x * BLOCK_SIZE, y * BLOCK_SIZE);
    }

    private boolean doesCollideWithSnakeBodyPart(SnakeBodyPart snakeBodyPart, int x, int y) {
        Entity bodyPartEntity = snakeBodyPart.getBodyEntity();
        if (bodyPartEntity.getX() == x && bodyPartEntity.getY() == y) {
            return true;
        } else {
            return Objects.nonNull(snakeBodyPart.getNextBodyPart()) &&
                    doesCollideWithSnakeBodyPart(snakeBodyPart.getNextBodyPart(), x, y);
        }
    }

    private int getSnakeLength() {
        SnakeBodyPart snakeBodyPart = snakeHeadBodyPart.getNextBodyPart();
        int length = 2;
        do {
            length++;
            snakeBodyPart = snakeBodyPart.getNextBodyPart();
        } while (Objects.nonNull(snakeBodyPart.getNextBodyPart()));
        return length;
    }

    private boolean setHighScore(int snakeLength) {
        if (snakeLength > getWorldProperties().getInt(VAR_HIGH_SCORE)) {
            getWorldProperties().setValue(VAR_HIGH_SCORE, snakeLength);
            return true;
        } else {
            return false;
        }
    }

    private void finishGameWithMessage(final String message) {
        int snakeLength = getSnakeLength();
        String highScore = setHighScore(snakeLength) ? NEW_HIGH_SCORE : HIGH_SCORE;
        getDialogService().showMessageBox(
                message.formatted(snakeLength, highScore, getWorldProperties().getInt(VAR_HIGH_SCORE)),
                startNewGame);
    }
}
