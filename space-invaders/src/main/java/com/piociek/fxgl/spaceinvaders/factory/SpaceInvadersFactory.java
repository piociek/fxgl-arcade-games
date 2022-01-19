package com.piociek.fxgl.spaceinvaders.factory;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;

import java.util.List;
import java.util.Objects;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.piociek.fxgl.spaceinvaders.SpaceInvadersConstants.*;
import static com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersType.PLAYER;

public class SpaceInvadersFactory {

    public Entity spawnPlayer() {
        try {
            Entity player = getGameWorld().getSingleton(PLAYER);
            player.removeFromWorld();
        } catch (RuntimeException e) {
            // ignore
        }
        return spawn(ENTITY_PLAYER, getAppWidth() / 2.0, getAppHeight() - 1.5 * PLAYER_HEIGHT);
    }

    public Entity[][] spawnEnemies() {
        Entity[][] enemies = new Entity[ENEMY_COUNT_PER_ROW][ENEMY_ROW_COUNT];

        double startX =
                (getAppWidth() - ((ENEMY_COUNT_PER_ROW - 1) * (ENEMY_WIDTH + ENEMY_SPACING) + ENEMY_WIDTH)) / 2.0;
        double startY = 2 * UFO_HEIGHT;

        for (int x = 0; x < ENEMY_COUNT_PER_ROW; x++) {
            for (int y = 0; y < ENEMY_ROW_COUNT; y++) {
                SpawnData spawnData = new SpawnData(
                        startX + x * (ENEMY_WIDTH + ENEMY_SPACING),
                        startY + y * (ENEMY_HEIGHT + ENEMY_SPACING));
                spawnData.put(DATA_SCORE, ROW_TO_SCORE_MAP.get(y));

                enemies[x][y] = spawn(ENTITY_ENEMY, spawnData);
            }
        }
        return enemies;
    }

    public void spawnUfo() {
        spawn(ENTITY_UFO, getAppWidth(), UFO_HEIGHT / 2.0);
    }

    public void spawnBomb(Entity[][] enemies) {
        do {
            int x = random(0, ENEMY_COUNT_PER_ROW - 1);
            for (int y = ENEMY_ROW_COUNT - 1; y >= 0; y--) {
                Entity enemy = enemies[x][y];
                if (enemy.isActive()) {
                    spawn(ENTITY_BOMB, enemy.getCenter());
                    return;
                }
            }
        } while (true);
    }

    public void deleteAllEntities(SpaceInvadersType... types) {
        for (SpaceInvadersType type : types) {
            List<Entity> entities = getGameWorld().getEntities()
                    .stream()
                    .filter(Objects::nonNull)
                    .filter(e -> e.getType().equals(type))
                    .filter(Entity::isActive)
                    .toList();

            if (!entities.isEmpty()) {
                getGameWorld().removeEntities(entities);
            }
        }
    }
}
