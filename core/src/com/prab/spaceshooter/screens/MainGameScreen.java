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
import com.prab.spaceshooter.entities.Bullet;
import com.prab.spaceshooter.entities.Explosion;
import com.prab.spaceshooter.tools.CollisionRect;

import java.util.ArrayList;
import java.util.Random;

public class MainGameScreen implements Screen {
    // Declaring different constants.
    public static final float SPEED = 900;
    public static final int SHIP_WIDTH_PIXEL = 17;
    public static final int SHIP_HEIGHT_PIXEL = 32;
    public static final int SHIP_WIDTH = SHIP_WIDTH_PIXEL * 3;
    public static final int SHIP_HEIGHT = SHIP_HEIGHT_PIXEL * 3;
    public static final float SHIP_ANIMATION_SPEED = 0.5f;
    public static final float ROLL_TIMER_SWITCH_TIME = 0.15f;
    public static final float SHOOT_WAIT_TIME = 0.2f;
    public static final float MIN_ASTEROID_SPAWN_TIME = 0.3f;
    public static final float MAX_ASTEROID_SPAWN_TIME = 0.6f;
    Animation<TextureRegion>[] rolls;
    float x;
    float y;
    float rollTimer;
    int roll;
    float stateTime;
    Random random;
    SpaceGame game;
    BitmapFont scoreFont;
    ArrayList<Bullet> bullets;
    ArrayList<Asteroid> asteroids;
    ArrayList<Explosion> explosions;
    float shootTimer;
    float asteroidSpawnTimer;
    float health;
    int score;
    Texture blank;
    CollisionRect playerRect;
    Sound shootSound;
    Sound invaderDeathSound;
    Music gameMusic;
    boolean mute;

    public MainGameScreen(SpaceGame game) {
        this.game = game; // Set game field to game parameter.
        mute = false;
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
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
        Asteroid.SPEED = 250;
        asteroidSpawnTimer = random.nextFloat() * (MAX_ASTEROID_SPAWN_TIME - MIN_ASTEROID_SPAWN_TIME) + MIN_ASTEROID_SPAWN_TIME;
        asteroids = new ArrayList<Asteroid>(); // Create arraylist of asteroids.
        bullets = new ArrayList<Bullet>(); // Create arraylist of bullets.
        explosions = new ArrayList<Explosion>(); // Create arraylist of explosions.
        shootTimer = 0; // Set shoot timer to 0
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
                health -= 0.05f;
            }
        }

        // Shooting code
        shootTimer += delta;
        // If mouse is pressed, add 2 new bullets on either side of ship.
        if ((Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE))&& shootTimer >= SHOOT_WAIT_TIME) {
            shootTimer = 0;
            int offset = 4;
            if (roll == 1 || roll == 3) // Slightly tilted
                offset = 8;
            if (roll == 0 || roll == 4) // Fully tilted
                offset = 16;
            bullets.add(new Bullet(x + offset));
            bullets.add(new Bullet(x + SHIP_WIDTH - offset));
            if (!mute)
                shootSound.play();
        }
        ArrayList<Bullet> bulletsToRemove = new ArrayList<Bullet>();
        // Iterate through array of bullets and run update method
        for (Bullet bullet : bullets) {
            bullet.update(delta);
            // If remove field is true, remove the bullet.
            if (bullet.remove)
                bulletsToRemove.add(bullet);
        }

        // Explosion code
        ArrayList<Explosion> explosionsToRemove = new ArrayList<Explosion>();
        for (Explosion explosion : explosions) {
            explosion.update(delta);
            if (explosion.remove)
                explosionsToRemove.add(explosion);
        }
        explosions.removeAll(explosionsToRemove);

        // Collision Code
        for (Bullet bullet : bullets) {
            for (Asteroid asteroid : asteroids) {
                if (bullet.getCollisionRect().collidesWith(asteroid.getCollisionRect())) {
                    bulletsToRemove.add(bullet);
                    asteroid.health -= 50;
                    if (asteroid.health <= 0) {
                        asteroidsToRemove.add(asteroid);
                        if (!mute)
                            invaderDeathSound.play();
                        explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
                    }
                    score += 100;
                }
            }
        }
        bullets.removeAll(bulletsToRemove);

        for (Asteroid asteroid: asteroids) {
            if (asteroid.getCollisionRect().collidesWith(playerRect)) {
                asteroidsToRemove.add(asteroid);
                health -= 0.1;
                explosions.add(new Explosion(asteroid.getX(), asteroid.getY()));
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
        if (health <= 0) {
            this.dispose();
            game.setScreen(new GameOverScreen(game, score, false));
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
        // Iterate through array of bullets and run render method for each.
        for (Bullet bullet : bullets) {
            bullet.render(game.batch);
        }
        for (Asteroid asteroid : asteroids) {
            asteroid.render(game.batch);
        }
        for (Explosion explosion : explosions) {
            explosion.render(game.batch);
        }
        if (health > 0.75f) {
            game.batch.setColor(Color.GREEN);
        } else if (health > 0.25f) {
            game.batch.setColor(Color.ORANGE);
        } else {
            game.batch.setColor(Color.RED);
        }
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "" + score);
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
        shootSound.dispose();
    }
}
