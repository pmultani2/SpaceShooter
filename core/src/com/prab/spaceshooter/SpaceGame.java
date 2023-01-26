package com.prab.spaceshooter;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.prab.spaceshooter.screens.MainMenuScreen;
import com.prab.spaceshooter.tools.ScrollingBackground;

public class SpaceGame extends Game {
	public SpriteBatch batch; // Declare spritebatch. Can be used from any class with a SpaceGame instance.
	public ScrollingBackground scrollingBackground;
	public static final int WIDTH = 480; // Declare constant for width of game. Can be used in any class.
	public static final int HEIGHT = 720; // Declare constant for height of game. Can be used in any class.
	
	@Override
	public void create () {
		// Create spritebatch
		batch = new SpriteBatch();
		this.scrollingBackground = new ScrollingBackground();
		// Set the screen to be the main menu. Pass in a MainMenuScreen object with SpaceGame parameter.
		this.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render () {
		// Run render method from super class.
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		this.scrollingBackground.resize(width, height);
		super.resize(width, height);
	}

	@Override
	public void dispose () {
		// Dispose of spritebatch.
		batch.dispose();
	}
}
