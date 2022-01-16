package com.piociek.fxgl.snake;

import com.almasb.fxgl.entity.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnakeBodyPart {
    private Entity bodyEntity;
    private MovementDirection movementDirection;
    private SnakeBodyPart nextBodyPart;
}
