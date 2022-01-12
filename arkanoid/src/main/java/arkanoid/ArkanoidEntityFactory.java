package arkanoid;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import javafx.scene.shape.Rectangle;

import static arkanoid.ArkanoidConstants.*;
import static arkanoid.ArkanoidEntityType.*;
import static com.almasb.fxgl.dsl.FXGL.*;

public class ArkanoidEntityFactory implements EntityFactory {

    @Spawns("ball")
    public Entity ball(SpawnData spawnData) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.DYNAMIC);

        return entityBuilder(spawnData)
                .type(BALL)
                .bbox(BoundingShape.box(BALL_SIZE, BALL_SIZE))
                .viewWithBBox(new Rectangle(BALL_SIZE, BALL_SIZE))
                .with(physicsComponent)
                .collidable()
                .build();
    }

    @Spawns("paddle")
    public Entity paddle(SpawnData spawnData) {
        PhysicsComponent physicsComponent = new PhysicsComponent();
        physicsComponent.setBodyType(BodyType.KINEMATIC);

        return entityBuilder(spawnData)
                .type(PADDLE)
                .bbox(BoundingShape.box(PADDLE_WIDTH, PADDLE_HEIGHT))
                .viewWithBBox(new Rectangle(PADDLE_WIDTH, PADDLE_HEIGHT))
                .with(physicsComponent)
                .collidable()
                .build();
    }

    @Spawns("brick")
    public Entity brick(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(BRICK)
                .bbox(BoundingShape.box(BRICK_WIDTH, BRICK_HEIGHT))
                .viewWithBBox(new Rectangle(BRICK_WIDTH, BRICK_HEIGHT))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("wall")
    public Entity wall(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(WALL)
                .bbox(BoundingShape.box(WALL_ROOF_SIZE, getAppHeight()))
                .viewWithBBox(new Rectangle(WALL_ROOF_SIZE, getAppHeight()))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("roof")
    public Entity roof(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(ROOF)
                .bbox(BoundingShape.box(getAppWidth(), WALL_ROOF_SIZE))
                .viewWithBBox(new Rectangle(getAppWidth(), WALL_ROOF_SIZE))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("void")
    public Entity voidEntity(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .type(VOID)
                .bbox(BoundingShape.box(getAppWidth(), 0))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }
}
