package com.piociek.fxgl.pong;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;
import static com.piociek.fxgl.pong.PongConstants.*;
import static com.piociek.fxgl.pong.PongEntityType.*;

public class PongEntityFactory implements EntityFactory {

    @Spawns("paddle")
    public Entity paddle(SpawnData spawnData) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.KINEMATIC);

        return entityBuilder(spawnData)
                .type(PADDLE)
                .bbox(new HitBox(BoundingShape.box(PADDLE_WIDTH, PADDLE_HEIGHT)))
                .viewWithBBox(new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT))
                .with(physicsComponent)
                .with(new PaddleMoveComponent())
                .collidable()
                .build();
    }

    @Spawns("ball")
    public Entity ball(SpawnData spawnData) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(spawnData)
                .type(BALL)
                .bbox(new HitBox(BoundingShape.box(BALL_SIZE, BALL_SIZE)))
                .viewWithBBox(new Rectangle(BALL_SIZE, BALL_SIZE))
                .with(physicsComponent)
                .collidable()
                .build();
    }

    @Spawns("wall")
    public Entity wall(SpawnData spawnData) {
        return entityBuilder()
                .type(WALL)
                .at(-BALL_SIZE, spawnData.getY())
                .bbox(new HitBox(BoundingShape.box(getAppWidth() + (2 * BALL_SIZE), 0)))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("void_left")
    public Entity voidLeft(SpawnData spawnData) {
        return entityBuilder()
                .type(VOID_LEFT)
                .at(-BALL_SIZE, 0)
                .bbox(new HitBox(BoundingShape.box(0, getAppHeight())))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("void_right")
    public Entity voidRight(SpawnData spawnData) {
        return entityBuilder()
                .type(VOID_RIGHT)
                .at(getAppWidth() + BALL_SIZE, 0)
                .bbox(new HitBox(BoundingShape.box(0, getAppHeight())))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }
}
