package pong;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;
import static pong.EntityTypes.*;

public class PongGame extends GameApplication {

    private static final Logger LOGGER = Logger.get(PongGame.class);

    private static final int PADDLE_WIDTH = 30;
    private static final int PADDLE_HEIGHT = 100;
    private static final int BALL_SIZE = 20;
    private static final int PADDLE_SPEED = 300;
    private static final int BALL_SPEED = 300;

    private Entity paddle1;
    private Entity paddle2;
    private Entity ball;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Pong v1");
        settings.setCloseConfirmation(false);
        settings.setDeveloperMenuEnabled(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.W, () -> paddle1.getComponent(PhysicsComponent.class).setVelocityY(-PADDLE_SPEED));
        onKey(KeyCode.S, () -> paddle1.getComponent(PhysicsComponent.class).setVelocityY(PADDLE_SPEED));
        onKey(KeyCode.UP, () -> paddle2.getComponent(PhysicsComponent.class).setVelocityY(-PADDLE_SPEED));
        onKey(KeyCode.DOWN, () -> paddle2.getComponent(PhysicsComponent.class).setVelocityY(PADDLE_SPEED));
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score1", 0);
        vars.put("score2", 0);
    }

    @Override
    protected void initGame() {
        spawnWall(0);
        spawnWall(getAppHeight());
        spawnVoid_left();
        spawnVoid_right();

        paddle1 = spawnBat(0, (getAppHeight() / 2) - (PADDLE_HEIGHT / 2));
        paddle2 = spawnBat(getAppWidth() - PADDLE_WIDTH, (getAppHeight() / 2) - (PADDLE_HEIGHT / 2));
        ball = spawnBall((getAppWidth() / 2) - (BALL_SIZE / 2), (getAppHeight() / 2) - (BALL_SIZE / 2));

        ball.getComponent(PhysicsComponent.class).setVelocityX(BALL_SPEED);
    }

    private Entity spawnBat(double x, double y) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(PADDLE)
                .at(x, y)
                .bbox(new HitBox(BoundingShape.box(PADDLE_WIDTH, PADDLE_HEIGHT)))
                .viewWithBBox(new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT))
                .collidable()
                .with(physicsComponent)
                .buildAndAttach();
    }

    private Entity spawnBall(double x, double y) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return entityBuilder()
                .type(BALL)
                .at(x, y)
                .bbox(new HitBox(BoundingShape.box(BALL_SIZE, BALL_SIZE)))
                .viewWithBBox(new Rectangle(BALL_SIZE, BALL_SIZE))
                .collidable()
                .with(physicsComponent)
                .buildAndAttach();
    }

    private Entity spawnWall(double y) {
        return entityBuilder()
                .type(WALL)
                .at(-BALL_SIZE, y)
                .bbox(new HitBox(BoundingShape.box(getAppWidth() + (2 * BALL_SIZE), 0)))
                .collidable()
                .with(new PhysicsComponent())
                .buildAndAttach();
    }

    private Entity spawnVoid_left() {
        return entityBuilder()
                .type(VOID_LEFT)
                .at(-BALL_SIZE, 0)
                .bbox(new HitBox(BoundingShape.box(0, getAppHeight())))
                .collidable()
                .with(new PhysicsComponent())
                .buildAndAttach();
    }

    private Entity spawnVoid_right() {
        return entityBuilder()
                .type(VOID_RIGHT)
                .at(getAppWidth() + BALL_SIZE, 0)
                .bbox(new HitBox(BoundingShape.box(0, getAppHeight())))
                .collidable()
                .with(new PhysicsComponent())
                .buildAndAttach();
    }

    @Override
    protected void initUI() {
        Text textScore1 = getUIFactoryService().newText("", Color.BLACK, 22);
        Text textScore2 = getUIFactoryService().newText("", Color.BLACK, 22);

        textScore1.setTranslateX(10);
        textScore1.setTranslateY(50);

        textScore2.setTranslateX(getAppWidth() - 30);
        textScore2.setTranslateY(50);

        textScore1.textProperty().bind(getWorldProperties().intProperty("score1").asString());
        textScore2.textProperty().bind(getWorldProperties().intProperty("score2").asString());

        getGameScene().addUINodes(textScore1, textScore2);
    }

    @Override
    protected void onUpdate(double tpf) {

    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, PADDLE, (ball, paddle) -> {
                    ball.getComponent(PhysicsComponent.class)
                            .setVelocityX(ball.getComponent(PhysicsComponent.class).getVelocityX() * -1);
                    if (ball.getComponent(PhysicsComponent.class).getVelocityX() == 0) {
                        LOGGER.fatal("Ball velocityX == 0");
                    }
                    ball.getComponent(PhysicsComponent.class)
                            .setVelocityY(ball.getComponent(PhysicsComponent.class).getVelocityY() + paddle.getComponent(PhysicsComponent.class).getVelocityY());
                }
        );
        onCollisionBegin(BALL, WALL, (ball, wall) ->
                ball.getComponent(PhysicsComponent.class).setVelocityY(ball.getComponent(PhysicsComponent.class).getVelocityY() * -1));

        onCollision(BALL, VOID_LEFT, (ball, voidLeft) -> {
            getWorldProperties().increment("score2", +1);
            spawnNewBall(Direction.RIGHT);
        });

        onCollision(BALL, VOID_RIGHT, (ball, voidRight) -> {
            getWorldProperties().increment("score1", +1);
            spawnNewBall(Direction.LEFT);
        });
    }

    private void spawnNewBall(Direction direction) {
        ball.removeFromWorld();
        ball = spawnBall((getAppWidth() / 2) - (BALL_SIZE / 2), (getAppHeight() / 2) - (BALL_SIZE / 2));
        ball.getComponent(PhysicsComponent.class).setVelocityX(BALL_SPEED * direction.value);
    }
}

enum EntityTypes {
    BALL, PADDLE, WALL, VOID_LEFT, VOID_RIGHT
}

enum Direction {
    LEFT(-1), RIGHT(1);

    Direction(int value) {
        this.value = value;
    }

    int value;
}