package com.piociek.fxgl.spaceinvaders.factory;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.particle.ParticleComponent;
import com.almasb.fxgl.particle.ParticleEmitter;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.piociek.fxgl.spaceinvaders.component.ScoreComponent;
import javafx.geometry.Point2D;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.piociek.fxgl.spaceinvaders.SpaceInvadersConstants.*;
import static com.piociek.fxgl.spaceinvaders.factory.SpaceInvadersType.*;

public class SpaceInvadersEntityFactory implements EntityFactory {

    @Spawns("background")
    public Entity background(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .view(new Rectangle(getAppWidth(), getAppHeight(), Color.LIGHTGRAY))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .build();
    }

    @Spawns("screenEnd")
    public Entity screenEnd(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(SCREEN_END)
                .bbox(new HitBox(BoundingShape.box(getAppWidth(), 1)))
                .with(new IrremovableComponent())
                .collidable()
                .build();
    }

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
                .bbox(new HitBox(BoundingShape.box(ENEMY_WIDTH, ENEMY_HEIGHT)))
                .view(new Rectangle(ENEMY_WIDTH, ENEMY_HEIGHT))
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

    @Spawns("explosion")
    public Entity explosion(SpawnData spawnData) {
        ParticleEmitter emitter = new ParticleEmitter();
        emitter.setNumParticles(20);
        emitter.setEmissionRate(0.5);
        emitter.setMaxEmissions(2);
        emitter.setSize(1, 10);
        emitter.setSpawnPointFunction(i -> new Point2D(0, 0));
        emitter.setVelocityFunction(i -> new Point2D(Math.cos(i), Math.sin(i)).multiply(60));
        emitter.setScaleFunction(i -> new Point2D(FXGLMath.randomDouble() * -0.2, FXGLMath.randomDouble() * -0.2));
        emitter.setExpireFunction(i -> Duration.seconds(0.3));
        emitter.setStartColor(Color.YELLOW);
        emitter.setEndColor(Color.RED);
        emitter.setBlendMode(BlendMode.SRC_OVER);
        emitter.setInterpolator(Interpolators.EXPONENTIAL.EASE_OUT());

        return entityBuilder(spawnData)
                .type(EXPLOSION)
                .with(new ParticleComponent(emitter))
                .build();
    }
}
