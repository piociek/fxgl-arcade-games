package com.piociek.fxgl.spaceinvaders;

import java.util.HashMap;
import java.util.Map;

public abstract class SpaceInvadersConstants {

    public static final String VAR_SCORE = "score";
    public static final String VAR_LIVES = "lives";
    public static final String VAR_HIGH_SCORE = "highScore";

    public static final String DATA_SCORE = "score";

    public static final Integer VALUE_DEFAULT_LIVES = 3;

    public static final String UI_SCORE = "Score: ";
    public static final String UI_LIVES = "Lives: ";
    public static final String UI_HIGH_SCORE = "HighScore: ";

    public static final String ENTITY_BACKGROUND = "background";
    public static final String ENTITY_SCREEN_END = "screenEnd";
    public static final String ENTITY_PLAYER = "player";
    public static final String ENTITY_ENEMY = "enemy";
    public static final String ENTITY_UFO = "ufo";
    public static final String ENTITY_BULLET = "bullet";
    public static final String ENTITY_BOMB = "bomb";
    public static final String ENTITY_EXPLOSION = "explosion";

    public static final Double PLAYER_WIDTH = 32.0;
    public static final Double PLAYER_HEIGHT = 32.0;
    public static final Double PLAYER_SPEED = 4.0;

    public static final Double BULLET_WIDTH = 15.0;
    public static final Double BULLET_HEIGHT = 2.0;
    public static final Double BULLET_SPEED = 450.0;

    public static final Double ENEMY_WIDTH = 32.0;
    public static final Double ENEMY_HEIGHT = 32.0;
    public static final Integer ENEMY_ROW_COUNT = 5;
    public static final Integer ENEMY_COUNT_PER_ROW = 12;
    public static final Integer ENEMY_SPACING = 16;

    public static final Double ENEMY_SPEED = 0.5;
    public static final Double ENEMY_SPEED_INCREASE = 0.1;

    public static final Map<Integer, Integer> ROW_TO_SCORE_MAP = new HashMap<>();

    static {
        int maxScore = ENEMY_ROW_COUNT * 10;
        for (int i = 0; i < ENEMY_ROW_COUNT; i++) {
            ROW_TO_SCORE_MAP.put(i, maxScore - i * 10);
        }
    }

    public static final Double BOMB_WIDTH = 15.0;
    public static final Double BOMB_HEIGHT = 2.0;
    public static final Double BOMB_SPEED = 350.0;
    public static final Double BOMB_SPAWN_INTERVAL = 1.8;

    public static final Double UFO_WIDTH = 64.0;
    public static final Double UFO_HEIGHT = 32.0;
    public static final Double UFO_SPEED = 75.0;
    public static final Integer UFO_SCORE = 150;
    public static final Double UFO_SPAWN_INTERVAL = 25.0;

    public static final String LOG_EXCEPTION_NOT_POSSIBLE = "Should not happen";
    public static final String LOG_MSG_GAME_END =
            "Game over\nScore: %d\n%s";

    public static final String NEW_HIGH_SCORE = "New high score!";
    public static final String HIGH_SCORE = "High score: %d";
}
