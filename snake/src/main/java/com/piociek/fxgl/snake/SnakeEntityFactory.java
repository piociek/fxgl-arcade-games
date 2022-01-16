package com.piociek.fxgl.snake;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;
import static com.piociek.fxgl.snake.SnakeConstants.BLOCK_SIZE;

public class SnakeEntityFactory implements EntityFactory {

    @Spawns("block")
    public Entity block(SpawnData spawnData) {
        return entityBuilder(spawnData)
                .viewWithBBox(new Rectangle(BLOCK_SIZE, BLOCK_SIZE))
                .build();
    }
}
