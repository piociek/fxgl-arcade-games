package com.piociek.fxgl.snake;

public abstract class SnakeConstants {

    public static final String ENTITY_BLOCK = "block";

    public static final String VAR_HIGH_SCORE = "highScore";

    public static final int GAME_WIDTH = 20;
    public static final int GAME_HEIGHT = 15;
    public static final int BLOCK_SIZE = 30;

    public static final String LOG_MOVE_DIRECTION = "Current move direction set to %s";
    public static final String LOG_NEW_BLOCK_ATTACHED = "New block attached to snake";
    public static final String LOG_NEW_BLOCK_CREATED = "New block created at x:%d y:%d";
    public static final String LOG_NEW_BLOCK_COLLISION = "New block collision at x:%d y:%d";

    public static final String LOG_EXCEPTION_NOT_POSSIBLE = "Should not happen";
    public static final String LOG_EXCEPTION_SNAKE_BODY_COLLISION =
            "Game end - snake collision with body part\nTotal length: %d\n%sigh score: %d";
    public static final String LOG_EXCEPTION_SCREEN_END_COLLISION =
            "Game end - snake collision with screen\nTotal length: %d\n%sigh score: %d";
    public static final String NEW_HIGH_SCORE = "New h";
    public static final String HIGH_SCORE = "H";
    public static final String LOG_WIN = "You've WON!\nTotal length: %d";
}
