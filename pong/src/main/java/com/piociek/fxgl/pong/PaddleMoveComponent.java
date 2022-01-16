package com.piociek.fxgl.pong;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;

import static com.piociek.fxgl.pong.PongConstants.PADDLE_SPEED;

public class PaddleMoveComponent extends Component {

    private PhysicsComponent physicsComponent;
    private boolean canMove = true;

    public void blockMovement() {
        this.canMove = false;
    }

    public void unBlockMovement() {
        this.canMove = true;
    }

    public void moveUp() {
        if (this.canMove) {
            physicsComponent.setVelocityY(-PADDLE_SPEED);
        }
    }

    public void moveDown() {
        if (this.canMove) {
            physicsComponent.setVelocityY(PADDLE_SPEED);
        }
    }

    public void reverseMovement() {
        physicsComponent.setVelocityY(physicsComponent.getVelocityY() * -1);
    }
}
