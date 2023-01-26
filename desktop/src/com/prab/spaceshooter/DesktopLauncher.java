package com.prab.spaceshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import sun.jvm.hotspot.gc.shared.Space;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setWindowedMode(SpaceGame.WIDTH, SpaceGame.HEIGHT);
		config.setTitle("Space Shooter");
		config.setResizable(false);
		new Lwjgl3Application(new SpaceGame(), config);
	}
}
