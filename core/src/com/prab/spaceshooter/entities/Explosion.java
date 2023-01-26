package com.prab.spaceshooter.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Explosion {
    public static float FRAME_LENGTH = 0.1f;
    public static final int OFFSET = 0;
    public static final int SIZE = 32;

    private static Animation<TextureRegion> anim = null;
    float x, y;
    float stateTime;

    public boolean remove = false;

    public Explosion(float x, float y) {
        this.x = x - OFFSET;
        this.y = y - OFFSET;
        stateTime = 0;
        if (anim == null)
            anim = new Animation<TextureRegion>(FRAME_LENGTH, TextureRegion.split(new Texture("explosion.png"), SIZE, SIZE)[0]);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        if (anim.isAnimationFinished(stateTime)) {
            remove = true;
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(anim.getKeyFrame(stateTime), x, y, 64, 64);
    }
}
