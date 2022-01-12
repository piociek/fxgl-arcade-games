package arkanoid;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.input.KeyCode;

import java.util.Objects;

import static arkanoid.ArkanoidConstants.*;
import static arkanoid.ArkanoidEntityType.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public class ArkanoidGame extends GameApplication {

    private final ArkanoidEntityFactory arkanoidEntityFactory = new ArkanoidEntityFactory();

    private Entity paddle;
    private Entity ball;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL-Arkanoid");
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(arkanoidEntityFactory);

        spawn(ENTITY_WALL, 0, 0);
        spawn(ENTITY_WALL, getAppWidth() - WALL_ROOF_SIZE, 0);
        spawn(ENTITY_ROOF, 0, 0);
        spawn(ENTITY_VOID, 0, getAppHeight() + BALL_SIZE);

        resetBall();

        paddle = spawn(ENTITY_PADDLE, getAppWidth() / 2 - PADDLE_WIDTH / 2, getAppHeight() - PADDLE_HEIGHT);

        int brickXCount = 3;
        int brickYCount = 3;
        int brickPositionX = (getAppWidth() - 2 * WALL_ROOF_SIZE) / brickXCount;
        int brickPositionY = ((getAppHeight() / 2) / brickYCount) - WALL_ROOF_SIZE;
        for (int x = 0; x < brickXCount; x++) {
            for (int y = 1; y <= brickYCount; y++) {
                spawn(ENTITY_BRICK,
                        WALL_ROOF_SIZE + brickPositionX / 2 + (x * brickPositionX - BRICK_WIDTH / 2),
                        y * brickPositionY - BRICK_HEIGHT / 2);
            }
        }
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.LEFT, () -> paddle.getComponent(PhysicsComponent.class).setVelocityX(-PADDLE_SPEED));
        onKeyDown(KeyCode.RIGHT, () -> paddle.getComponent(PhysicsComponent.class).setVelocityX(PADDLE_SPEED));
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);
        onCollisionBegin(WALL, BALL, (wall, ball) -> reverseVelocityX(ball));
        onCollisionBegin(ROOF, BALL, (roof, ball) -> reverseVelocityY(ball));
        onCollisionBegin(PADDLE, BALL, (paddle, ball) -> {
            if (FXGLMath.abs(ball.getComponent(PhysicsComponent.class).getVelocityY()) < BALL_SPEED) {
                ball.getComponent(PhysicsComponent.class).setVelocityY(-BALL_SPEED);
            } else {
                reverseVelocityY(ball);
            }
            ball.getComponent(PhysicsComponent.class).setVelocityX(
                    ball.getComponent(PhysicsComponent.class).getVelocityX() + paddle.getComponent(PhysicsComponent.class).getVelocityX());
        });
        onCollisionBegin(BALL, BRICK, (ball, brick) -> {
            brick.removeFromWorld();
            reverseVelocityY(ball);
        });

        onCollisionBegin(PADDLE, WALL, (paddle, wall) -> reverseVelocityX(paddle));

        onCollision(VOID, BALL, (voidEntity, ball) -> resetBall());
    }

    private void resetBall() {
        if (Objects.nonNull(ball)) {
            ball.removeFromWorld();
        }
        ball = spawn(ENTITY_BALL, getAppWidth() / 2 - BALL_SIZE / 2, getAppHeight() - PADDLE_HEIGHT - BALL_SIZE - 10);
        ball.getComponent(PhysicsComponent.class).setVelocityY(-BALL_SPEED);
    }

    private void reverseVelocityX(Entity e) {
        e.getComponent(PhysicsComponent.class).setVelocityX(
                e.getComponent(PhysicsComponent.class).getVelocityX() * -1);
    }

    private void reverseVelocityY(Entity e) {
        e.getComponent(PhysicsComponent.class).setVelocityY(
                e.getComponent(PhysicsComponent.class).getVelocityY() * -1);
    }
}
