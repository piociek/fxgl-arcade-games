package com.piociek.fxgl.spaceinvaders.factory;

import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.piociek.fxgl.spaceinvaders.component.ScoreComponent;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;
import static com.piociek.fxgl.spaceinvaders.SpaceInvadersConstants.*;
import static com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersType.*;

public class SpaceInvadersEntityFactory implements EntityFactory {

    @Spawns("player")
    public Entity player(SpawnData spawnData) {
        double[] points = new double[]{
                (PLAYER_WIDTH / 2), 0.0,
                0.0, PLAYER_HEIGHT,
                PLAYER_WIDTH, PLAYER_HEIGHT};

        return entityBuilder(spawnData)
                .type(PLAYER)
                .bbox(new HitBox(BoundingShape.polygon(points)))
                .view(new Polygon(points))
                .collidable()
                .build();
    }

    @Spawns("bullet")
    public Entity bullet(SpawnData spawnData) {
        Point2D direction = new Point2D(0, -10);
        return entityBuilder(spawnData)
                .type(BULLET)
                .viewWithBBox(new Rectangle(BULLET_WIDTH, BULLET_HEIGHT))
                .with(new ProjectileComponent(direction, BULLET_SPEED))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity enemy(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(ENEMY)
                .viewWithBBox(new Rectangle(ENEMY_WIDTH, ENEMY_HEIGHT))
                .with(new ScoreComponent(spawnData.get(DATA_SCORE)))
                .collidable()
                .build();
    }

    @Spawns("bomb")
    public Entity bomb(SpawnData spawnData) {
        Point2D direction = new Point2D(0, 10);
        return entityBuilder(spawnData)
                .type(BOMB)
                .viewWithBBox(new Rectangle(BOMB_WIDTH, BOMB_HEIGHT))
                .with(new ProjectileComponent(direction, BOMB_SPEED))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    @Spawns("ufo")
    public Entity ufo(SpawnData spawnData) {
        Point2D direction = new Point2D(-10, 0);
        return entityBuilder(spawnData)
                .type(UFO)
                .viewWithBBox(new Rectangle(UFO_WIDTH, UFO_HEIGHT))
                .with(new ProjectileComponent(direction, UFO_SPEED))
                .with(new OffscreenCleanComponent())
                .collidable()
                .build();
    }

    @Spawns("screenEnd")
    public Entity screenEnd(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(SCREEN_END)
                .bbox(new HitBox(BoundingShape.box(getAppWidth(), 1)))
                .collidable()
                .build();
    }
}
