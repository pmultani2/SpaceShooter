package com.prab.spaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ScreenUtils;
import com.prab.spaceshooter.SpaceGame;
import com.prab.spaceshooter.entities.Asteroid;
import com.prab.spaceshooter.tools.CollisionRect;

import java.util.ArrayList;
import java.util.Random;

public class SuddenDeathScreen implements Screen {
    // Declaring different constants.
    public static final float SPEED = 450;
    public static final int SHIP_WIDTH_PIXEL = 17;
    public static final int SHIP_HEIGHT_PIXEL = 32;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;
    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final float ROLL_TIMER_SWITCH_TIME = 0.15f;
    public static final float MIN_ASTEROID_SPAWN_TIME = 0.3f;
    public static final float MAX_ASTEROID_SPAWN_TIME = 0.5f;
    Animation<TextureRegion>[] rolls;
    float x;
    float y;
    float rollTimer;
    int roll;
    float stateTime;
    Random random;
    SpaceGame game;
    BitmapFont scoreFont;
    ArrayList<Asteroid> asteroids;
    float asteroidSpawnTimer;
    float health;
    float score;
    Texture blank;
    CollisionRect playerRect;
    Sound invaderDeathSound;
    Music gameMusic;
    boolean mute;

    public SuddenDeathScreen(SpaceGame game) {
        this.game = game; // Set game field to game parameter.
        Asteroid.SPEED = 500;
        mute = false;
        invaderDeathSound = Gdx.audio.newSound(Gdx.files.internal("invaderkilled.wav"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("gamemusic.mp3"));
        gameMusic.setLooping(true);
        scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt.txt"));
        score = 0;
        health = 1;
        blank = new Texture("blank.png");
        playerRect = new CollisionRect(0, 0, SHIP_WIDTH, SHIP_HEIGHT);
        y = 15; // Set y field to 15.
        x = SpaceGame.WIDTH / 2 - SHIP_WIDTH / 2; // Set x field to center.
        random = new Random();
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
        asteroids = new ArrayList<Asteroid>(); // Create arraylist of asteroids.
        roll = 2; // Set roll to 2.
        rollTimer = 0; // Set roll timer to 0.
        rolls = new Animation[5]; // Create animation array of length 5 for rolls.
        // Create 2D Array, splitting the sprite sheet.
        TextureRegion[][] rollSpriteSheet = TextureRegion.split(new Texture("ship.png"), 17, 32);
        // Set each element in animation array to an animation object containing duration of frame and image.
        rolls[0] = new Animation<TextureRegion>(SHIP_ANIMATION_SPEED, rollSpriteSheet[2]); // All left
        rolls[1] = new Animation<TextureRegion>(SHIP_ANIMATION_SPEED, rollSpriteSheet[1]);
        rolls[2] = new Animation<TextureRegion>(SHIP_ANIMATION_SPEED, rollSpriteSheet[0]); // Center
        rolls[3] = new Animation<TextureRegion>(SHIP_ANIMATION_SPEED, rollSpriteSheet[3]);
        rolls[4] = new Animation<TextureRegion>(SHIP_ANIMATION_SPEED, rollSpriteSheet[4]); // All right

        game.scrollingBackground.setSpeedFixed(false);
    }

    @Override
    public void show() {
        gameMusic.play();
        gameMusic.setVolume(0.25f);
    }

    @Override
    public void render(float delta) {
        // Asteroid Code
        asteroidSpawnTimer -= delta;
        if (asteroidSpawnTimer <= 0) {
            asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
            asteroids.add(new Asteroid(Asteroid.WIDTH + random.nextInt(Gdx.graphics.getWidth() - Asteroid.WIDTH*2)));
        }
        ArrayList<Asteroid> asteroidsToRemove = new ArrayList<Asteroid>();
        for (Asteroid asteroid : asteroids) {
            asteroid.update(delta);
            if (asteroid.remove) {
                asteroidsToRemove.add(asteroid);
            }
        }

        for (Asteroid asteroid: asteroids) {
            if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                asteroidsToRemove.add(asteroid);
                health -= 1.0f;
                if (!mute)
                    invaderDeathSound.play();
            }
        }
        asteroids.removeAll(asteroidsToRemove);


        // Movement code
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            x -= SPEED * Gdx.graphics.getDeltaTime();
            if (x < 0) x = 0;
            if ((Gdx.input.isKeyJustPressed(Input.Keys.LEFT)  || Gdx.input.isKeyJustPressed(Input.Keys.A)) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.D) && roll > 0) {
                rollTimer = 0;
                roll --;
            }
            rollTimer -= Gdx.graphics.getDeltaTime();
            if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
                rollTimer -= ROLL_TIMER_SWITCH_TIME;
                roll --;
                if (roll < 0) roll = 0;
            }
        } else {
            if(roll < 2) {
                rollTimer += Gdx.graphics.getDeltaTime();
                if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
                    rollTimer -= ROLL_TIMER_SWITCH_TIME;
                    roll++;
                    if (roll > 4) roll = 4;
                }
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            x += SPEED * Gdx.graphics.getDeltaTime();
            if (x + SHIP_WIDTH > game.WIDTH) x = game.WIDTH - SHIP_WIDTH;
            if ((Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.A) && roll < 4) {
                rollTimer = 0;
                roll ++;
            }
            rollTimer += Gdx.graphics.getDeltaTime();
            if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
                rollTimer -= ROLL_TIMER_SWITCH_TIME;
                roll ++;
                if (roll > 4) roll = 4;
            }
        } else {
            if (roll > 2) {
                rollTimer -= Gdx.graphics.getDeltaTime();
                if (Math.abs(rollTimer) > ROLL_TIMER_SWITCH_TIME) {
                    rollTimer = 0;
                    roll --;
                    if (roll < 0) roll = 0;
                }
            }
        }
        playerRect.move(x, y);

        // Clear screen to black color.
        ScreenUtils.clear(0, 0, 0, 0);

        // Dead
        score += delta;
        if (health <= 0) {
            this.dispose();
            game.setScreen(new GameOverScreen(game, (int)score, true));
            return;
        }

        // Mute Sounds
        if (Gdx.input.isKeyPressed(Input.Keys.M)) {
            mute = true;
            gameMusic.stop();
        }

        stateTime += delta;
        // Begin drawing textures
        game.batch.begin();
        game.scrollingBackground.updateAndRender(delta, game.batch);
        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }
        if (health > 0.75f) {
            game.batch.setColor(Color.GREEN);
        } else if (health > 0.25f) {
            game.batch.setColor(Color.ORANGE);
        } else {
            game.batch.setColor(Color.RED);
        }
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + Math.round(score));
        scoreFont.draw(game.batch, scoreLayout, Gdx.graphics.getWidth() / 2 - scoreLayout.width / 2, Gdx.graphics.getHeight() - scoreLayout.height - 10);
        game.batch.draw(blank, 0, 0, Gdx.graphics.getWidth() * health, 5);
        game.batch.setColor(Color.WHITE);
        // Draw a frame of animation.
        game.batch.draw(rolls[roll].getKeyFrame(stateTime, true), x, y, SHIP_WIDTH, SHIP_HEIGHT);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        gameMusic.stop();
    }

    @Override
    public void dispose() {
        scoreFont.dispose();
        gameMusic.dispose();
        invaderDeathSound.dispose();
    }
}
