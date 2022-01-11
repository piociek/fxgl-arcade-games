package pong;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.logging.Logger;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.scene.input.KeyCode;
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
        vars.put(SCORE_1, 0);
        vars.put(SCORE_2, 0);
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
    }

    @Override
    protected void initUI() {
        Text textScoreTop = getUIFactoryService().newText("Score", Color.BLACK, 25);
        textScoreTop.setTranslateX(getAppWidth() / 2 - 25);
        textScoreTop.setTranslateY(100);

        Text textScoreBottom = getUIFactoryService().newText(":", Color.BLACK, 25);
        textScoreBottom.setTranslateX(getAppWidth() / 2);
        textScoreBottom.setTranslateY(130);

        Text textScore1 = getUIFactoryService().newText("", Color.BLACK, 25);
        textScore1.setTranslateX(getAppWidth() / 2 - 25);
        textScore1.setTranslateY(130);

        Text textScore2 = getUIFactoryService().newText("", Color.BLACK, 25);
        textScore2.setTranslateX(getAppWidth() / 2 + 25);
        textScore2.setTranslateY(130);

        textScore1.textProperty().bind(getWorldProperties().intProperty(SCORE_1).asString());
        textScore2.textProperty().bind(getWorldProperties().intProperty(SCORE_2).asString());

        getGameScene().addUINodes(textScoreTop, textScoreBottom, textScore1, textScore2);
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
                            .setVelocityY(ball.getComponent(PhysicsComponent.class)
                                    .getVelocityY() + paddle.getComponent(PhysicsComponent.class).getVelocityY());
                }
        );
        onCollisionBegin(BALL, WALL, (ball, wall) ->
                ball.getComponent(PhysicsComponent.class)
                        .setVelocityY(ball.getComponent(PhysicsComponent.class)
                                .getVelocityY() * -1));

        onCollision(BALL, VOID_LEFT, (ball, voidLeft) -> {
            getWorldProperties().increment(SCORE_2, +1);
            spawnNewBall(Direction.RIGHT);
        });

        onCollision(BALL, VOID_RIGHT, (ball, voidRight) -> {
            getWorldProperties().increment(SCORE_1, +1);
            spawnNewBall(Direction.LEFT);
        });

        onCollision(PADDLE, WALL, (paddle, wall) -> {
            paddle.getComponent(PhysicsComponent.class).setVelocityY(0);
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
