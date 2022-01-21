package com.piociek.fxgl.spaceinvaders;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.piociek.fxgl.spaceinvaders.component.ScoreComponent;
import com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersEntityFactory;
import com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersFactory;
import com.piociek.fxgl.spaceinvaders.movement.EnemyMovement;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.piociek.fxgl.spaceinvaders.SpaceInvadersConstants.*;
import static com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersType.*;

public class SpaceInvadersGame extends GameApplication {

    private final SpaceInvadersFactory factory = new SpaceInvadersFactory();
    private EnemyMovement enemyMovement;
    private Entity player;
    private Entity bullet;
    private Entity[][] enemies;

    private final Runnable startNewGame = () -> {
        factory.deleteAllEntities(ENEMY, BULLET, BOMB, UFO, EXPLOSION);

        player = factory.spawnPlayer();
        enemies = factory.spawnEnemies();

        enemyMovement = new EnemyMovement(enemies);

        getWorldProperties().setValue(VAR_SCORE, 0);
        getWorldProperties().setValue(VAR_LIVES, VALUE_DEFAULT_LIVES);
    };

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("FXGL-Space-Invaders");
        settings.setVersion("0.1");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put(VAR_SCORE, 0);
        vars.put(VAR_LIVES, VALUE_DEFAULT_LIVES);
        vars.put(VAR_HIGH_SCORE, 0);
    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new SpaceInvadersEntityFactory());
        spawn(ENTITY_BACKGROUND);
        spawn(ENTITY_SCREEN_END, 0, getAppHeight());

        startNewGame.run();

        getGameTimer().runAtInterval(() -> factory.spawnBomb(enemies), Duration.seconds(BOMB_SPAWN_INTERVAL));
        getGameTimer().runAtInterval(factory::spawnUfo, Duration.seconds(UFO_SPAWN_INTERVAL));
    }

    @Override
    protected void initUI() {
        HBox score = createUiHBox(5, UI_SCORE, VAR_SCORE);
        HBox lives = createUiHBox(150, UI_LIVES, VAR_LIVES);
        HBox highScore = createUiHBox(getAppWidth() - 200, UI_HIGH_SCORE, VAR_HIGH_SCORE);
        getGameScene().addUINodes(score, lives, highScore);
    }

    private HBox createUiHBox(double x, String uiLabel, String propertyId) {
        HBox hBox = new HBox();
        hBox.setSpacing(5);
        hBox.setTranslateX(x);
        hBox.setTranslateY(5);

        Text text = getUIFactoryService().newText(uiLabel, Color.BLACK, 15);
        Text value = getUIFactoryService().newText("", Color.BLACK, 15);
        value.textProperty().bind(getWorldProperties().intProperty(propertyId).asString());
        hBox.getChildren().addAll(text, value);
        return hBox;
    }

    @Override
    protected void initInput() {
        onKey(KeyCode.LEFT, () -> {
            if (player.getX() > 5) {
                player.translateX(-PLAYER_SPEED);
            }
        });
        onKey(KeyCode.RIGHT, () -> {
            if (player.getRightX() < getAppWidth() - 5) {
                player.translateX(PLAYER_SPEED);
            }
        });
        onKeyDown(KeyCode.SPACE, () -> {
            if (bullet == null || !bullet.isActive()) {
                bullet = spawn(ENTITY_BULLET, player.getX(), player.getY());
            }
        });
    }

    @Override
    protected void initPhysics() {
        onCollisionBegin(BULLET, ENEMY, (bullet, enemy) -> {
            bullet.removeFromWorld();
            spawn(ENTITY_EXPLOSION, new SpawnData(enemy.getCenter()));
            enemy.removeFromWorld();
            getWorldProperties().increment(VAR_SCORE, enemy.getComponent(ScoreComponent.class).getScore());
            checkForGameOver();
            enemyMovement.checkMovementBounds();
        });

        onCollisionBegin(BULLET, UFO, (bullet, ufo) -> {
            bullet.removeFromWorld();
            spawn(ENTITY_EXPLOSION, new SpawnData(ufo.getCenter()));
            ufo.removeFromWorld();
            getWorldProperties().increment(VAR_SCORE, UFO_SCORE);
        });

        onCollisionBegin(BOMB, PLAYER, (bomb, player) -> {
            bomb.removeFromWorld();
            spawn(ENTITY_EXPLOSION, new SpawnData(this.player.getCenter()));
            getWorldProperties().increment(VAR_LIVES, -1);
            if (getWorldProperties().getInt(VAR_LIVES) == 0) {
                finishGame();
            }
            this.player = factory.spawnPlayer();
        });

        onCollisionBegin(PLAYER, ENEMY, (player, enemy) -> {
            spawn(ENTITY_EXPLOSION, new SpawnData(enemy.getCenter()));
            enemy.removeFromWorld();
            checkForGameOver();
            spawn(ENTITY_EXPLOSION, new SpawnData(this.player.getCenter()));
            getWorldProperties().increment(VAR_LIVES, -1);
            if (getWorldProperties().getInt(VAR_LIVES) == 0) {
                finishGame();
            }
            this.player = factory.spawnPlayer();
        });

        // TODO: BUG: Dialog is shown for every collision. Should be shown only once
        onCollision(ENEMY, SCREEN_END, (enemy, screenEnd) -> finishGame());
    }

    @Override
    protected void onUpdate(double tpf) {
        movePlayerToView();
        enemyMovement.move();
    }

    private void movePlayerToView() {
        if (player.getY() >= getAppHeight() - 1.5 * PLAYER_HEIGHT) {
            player.translateY(-PLAYER_SPEED);
        }
    }

    private void checkForGameOver() {
        List<Entity> enemyEntities = getGameWorld().getEntities().stream()
                .filter(Objects::nonNull)
                .filter(e -> e.getType().equals(ENEMY))
                .filter(Entity::isActive)
                .toList();
        if (enemyEntities.isEmpty()) {
            finishGame();
        }
    }

    private void finishGame() {
        int score = getWorldProperties().getInt(VAR_SCORE);
        String highScore = setHighScore(score) ?
                NEW_HIGH_SCORE : HIGH_SCORE.formatted(getWorldProperties().getInt(VAR_HIGH_SCORE));
        getDialogService().showMessageBox(
                LOG_MSG_GAME_END.formatted(score, highScore),
                startNewGame);
    }

    private boolean setHighScore(int score) {
        if (score > getWorldProperties().getInt(VAR_HIGH_SCORE)) {
            getWorldProperties().setValue(VAR_HIGH_SCORE, score);
            return true;
        } else {
            return false;
        }
    }
}
