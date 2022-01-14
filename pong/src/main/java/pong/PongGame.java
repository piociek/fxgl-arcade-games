package pong;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.Map;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.*;
import static pong.PongConstants.*;
import static pong.PongEntityType.*;

public class PongGame extends GameApplication {

    private static final Logger LOGGER = Logger.get(PongGame.class);
    private final PongEntityFactory pongEntityFactory = new PongEntityFactory();
    private Entity paddle1;
    private Entity paddle2;
    private Entity ball;

    private int uiPositionY;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL-Pong");
        settings.setCloseConfirmation(false);
        settings.setDeveloperMenuEnabled(true);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initInput() {
        onKeyDown(KeyCode.W, () -> paddle1.getComponent(PaddleMoveComponent.class).moveUp());
        onKeyDown(KeyCode.S, () -> paddle1.getComponent(PaddleMoveComponent.class).moveDown());

        onKeyDown(KeyCode.UP, () -> paddle2.getComponent(PaddleMoveComponent.class).moveUp());
        onKeyDown(KeyCode.DOWN, () -> paddle2.getComponent(PaddleMoveComponent.class).moveDown());
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put(VAR_SCORE_1, 0);
        vars.put(VAR_SCORE_2, 0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(pongEntityFactory);

        spawn(ENTITY_WALL, 0, 0);
        spawn(ENTITY_WALL, 0, getAppHeight());
        spawn(ENTITY_VOID_LEFT);
        spawn(ENTITY_VOID_RIGHT);

        paddle1 = spawn(ENTITY_PADDLE,
                0,
                getAppHeight() / 2 - PADDLE_HEIGHT / 2);
        paddle2 = spawn(ENTITY_PADDLE,
                getAppWidth() - PADDLE_WIDTH,
                getAppHeight() / 2 - PADDLE_HEIGHT / 2);
        spawnNewBall(Direction.RIGHT);

        ball.getComponent(PhysicsComponent.class).setVelocityX(BALL_SPEED);

        uiPositionY = getAppHeight() - PADDLE_WIDTH;
    }

    @Override
    protected void initUI() {
        HBox scorePlayer1 = new HBox();
        scorePlayer1.setSpacing(5);
        scorePlayer1.setTranslateX(PADDLE_WIDTH * 2);
        scorePlayer1.setTranslateY(uiPositionY);

        Text textScore1 = getUIFactoryService().newText(UI_SCORE, Color.BLACK, 20);
        Text textScoreValue1 = getUIFactoryService().newText("", Color.BLACK, 20);
        textScoreValue1.textProperty().bind(getWorldProperties().intProperty(VAR_SCORE_1).asString());
        scorePlayer1.getChildren().addAll(textScore1, textScoreValue1);

        HBox scorePlayer2 = new HBox();
        scorePlayer2.setSpacing(5);
        scorePlayer2.setTranslateX(getAppWidth() - PADDLE_WIDTH * 5);
        scorePlayer2.setTranslateY(uiPositionY);

        Text textScore2 = getUIFactoryService().newText(UI_SCORE, Color.BLACK, 20);
        Text textScoreValue2 = getUIFactoryService().newText("", Color.BLACK, 20);
        textScoreValue2.textProperty().bind(getWorldProperties().intProperty(VAR_SCORE_2).asString());
        scorePlayer2.getChildren().addAll(textScore2, textScoreValue2);

        getGameScene().addUINodes(scorePlayer1, scorePlayer2);
    }

    @Override
    protected void onUpdate(double tpf) {
        if (ball.getComponent(PhysicsComponent.class).getVelocityX() == 0) {
            LOGGER.fatal("Ball velocityX == 0");
            spawnNewBall(Direction.RIGHT);
        }
    }

    @Override
    protected void initPhysics() {
        getPhysicsWorld().setGravity(0, 0);

        onCollisionBegin(BALL, PADDLE, (ball, paddle) -> {
                    ball.getComponent(PhysicsComponent.class)
                            .setVelocityX(ball.getComponent(PhysicsComponent.class).getVelocityX() * -1);
                    ball.getComponent(PhysicsComponent.class)
                            .setVelocityY(ball.getComponent(PhysicsComponent.class)
                                    .getVelocityY() + paddle.getComponent(PhysicsComponent.class).getVelocityY());
                }
        );

        onCollisionBegin(BALL, WALL, (ball, wall) ->
                ball.getComponent(PhysicsComponent.class)
                        .setVelocityY(ball.getComponent(PhysicsComponent.class)
                                .getVelocityY() * -1));

        onCollision(BALL, VOID_LEFT, (ball, voidLeft) -> {
            getWorldProperties().increment(VAR_SCORE_2, +1);
            spawnNewBall(Direction.RIGHT);
        });

        onCollision(BALL, VOID_RIGHT, (ball, voidRight) -> {
            getWorldProperties().increment(VAR_SCORE_1, +1);
            spawnNewBall(Direction.LEFT);
        });

        getPhysicsWorld().addCollisionHandler(new CollisionHandler(PADDLE, WALL) {
            @Override
            protected void onCollisionBegin(Entity paddle, Entity wall) {
                paddle.getComponent(PaddleMoveComponent.class).blockMovement();
                paddle.getComponent(PaddleMoveComponent.class).reverseMovement();
            }

            @Override
            protected void onCollisionEnd(Entity paddle, Entity wall) {
                paddle.getComponent(PaddleMoveComponent.class).unBlockMovement();
            }
        });
    }

    private void spawnNewBall(Direction direction) {
        if (Objects.nonNull(ball)) {
            ball.removeFromWorld();
        }
        ball = spawn(ENTITY_BALL,
                getAppWidth() / 2 - BALL_SIZE / 2,
                getAppHeight() / 2 - BALL_SIZE / 2);
        ball.getComponent(PhysicsComponent.class).setVelocityX(BALL_SPEED * direction.value);
    }
}
