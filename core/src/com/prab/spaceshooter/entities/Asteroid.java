package com.prab.spaceshooter.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prab.spaceshooter.tools.CollisionRect;

public class Asteroid {
    public static int SPEED = 250;
    public static final int WIDTH = 48;
    public static final int HEIGHT = 48;
    private static Texture texture;
    float x, y;
    public int health;
    CollisionRect rect;

    public boolean remove = false;

    public Asteroid (float x) {
        this.health = 100;
        this.x = x;
        this.y = Gdx.graphics.getHeight();
        this.rect = new CollisionRect(x, y, WIDTH, HEIGHT);
        if (texture  == null)
            texture = new Texture("asteroid.png");
    }

    public void update (float deltaTime) {
        y -= SPEED * deltaTime;
        if (y < -HEIGHT)
            remove = true;
        rect.move(x, y);
    }

    public void render (SpriteBatch batch) {
        batch.draw(texture, x, y, WIDTH, HEIGHT);
    }

    public CollisionRect getCollisionRect() {
        return rect;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
