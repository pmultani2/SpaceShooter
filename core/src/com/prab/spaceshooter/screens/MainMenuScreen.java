package com.prab.spaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.prab.spaceshooter.SpaceGame;

public class MainMenuScreen implements Screen {
    // Declare a bunch of constants for widths, heights and positions.
    private static final int EXIT_BUTTON_WIDTH = 300;
    private static final int EXIT_BUTTON_HEIGHT = 150;
    private static final int PLAY_BUTTON_WIDTH = 400;
    private static final int PLAY_BUTTON_HEIGHT = 150;
    private static final int PLAY_BUTTON_Y = Gdx.graphics.getHeight() / 2 - PLAY_BUTTON_HEIGHT / 2;
    private static final int EXIT_BUTTON_Y = 100;
    SpaceGame game; // Declare SpaceGame Object.
    // Declare textures for buttons.
    Texture exitButtonActive;
    Texture exitButtonInactive;
    Texture playButtonActive;
    Texture playButtonInactive;
    Music menuMusic;
    BitmapFont font;

    public MainMenuScreen(SpaceGame game) {
        font = new BitmapFont(Gdx.files.internal("fonts/score.fnt.txt"));
        // Set the game field to the game parameter that was just passed in.
        this.game = game;
        // Create textures
        playButtonActive = new Texture("play_button_active.png");
        playButtonInactive = new Texture("play_button_inactive.png");
        exitButtonActive = new Texture("exit_button_active.png");
        exitButtonInactive = new Texture("exit_button_inactive.png");
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        menuMusic.setLooping(true);
    }
    @Override
    public void show() {
        menuMusic.play();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.M))
            menuMusic.stop();

        // Clear the screen to a black color
        ScreenUtils.clear(0, 0, 0, 0);

        // Begin drawing textures from spritebatch in SpaceGame.
        game.batch.begin();
        int x = SpaceGame.WIDTH / 2 - PLAY_BUTTON_WIDTH / 2; // center position
        // If mouse is over the button, draw active texture, and vice versa
        if (Gdx.input.getX() < x + PLAY_BUTTON_WIDTH && Gdx.input.getX() > x && SpaceGame.HEIGHT - Gdx.input.getY() < PLAY_BUTTON_Y + PLAY_BUTTON_HEIGHT && SpaceGame.HEIGHT - Gdx.input.getY() > PLAY_BUTTON_Y) {
            game.batch.draw(playButtonActive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
            // If texture is clicked, set screen to MainGameScreen passing in game parameter.
            if (Gdx.input.isTouched()) {
                game.setScreen(new MainGameScreen(game));
            }
        } else {
            game.batch.draw(playButtonInactive, x, PLAY_BUTTON_Y, PLAY_BUTTON_WIDTH, PLAY_BUTTON_HEIGHT);
        }
        x = SpaceGame.WIDTH / 2 - EXIT_BUTTON_WIDTH / 2; // center position

        // Sudden Death Button
        GlyphLayout suddenDeathLayout = new GlyphLayout(font, "Sudden Death");
        float suddenDeathX = Gdx.graphics.getWidth() / 2 - suddenDeathLayout.width / 2;
        float suddenDeathY = PLAY_BUTTON_Y - EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT - 50;
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (touchX > suddenDeathX && touchX < suddenDeathX + suddenDeathLayout.width && touchY > suddenDeathY - suddenDeathLayout.height && touchY < suddenDeathY) {
            suddenDeathLayout.setText(font, "Sudden Death", Color.YELLOW, 0, Align.left, false);
            if (Gdx.input.isTouched()) {
                game.setScreen(new SuddenDeathScreen(game));
            }
        }
        font.draw(game.batch, suddenDeathLayout, Gdx.graphics.getWidth() / 2 - suddenDeathLayout.width / 2, PLAY_BUTTON_Y - EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT - 50);
        // If mouse is over the button, draw active texture, and vice versa
        if (Gdx.input.getX() < x + EXIT_BUTTON_WIDTH && Gdx.input.getX() > x && SpaceGame.HEIGHT - Gdx.input.getY() < EXIT_BUTTON_Y + EXIT_BUTTON_HEIGHT && SpaceGame.HEIGHT - Gdx.input.getY() > EXIT_BUTTON_Y) {
            game.batch.draw(exitButtonActive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
            // If texture is clicked, exit application.
            if (Gdx.input.isTouched()) {
                Gdx.app.exit();
            }
        } else {
            game.batch.draw(exitButtonInactive, x, EXIT_BUTTON_Y, EXIT_BUTTON_WIDTH, EXIT_BUTTON_HEIGHT);
        }
        GlyphLayout layout = new GlyphLayout(font, "Mute (M)");
        font.draw(game.batch, layout, Gdx.graphics.getWidth() / 2 - layout.width / 2, layout.height + 25);
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
        menuMusic.stop();
    }

    @Override
    public void dispose() {
        menuMusic.dispose();
        playButtonInactive.dispose();
        playButtonActive.dispose();
        exitButtonActive.dispose();
        exitButtonInactive.dispose();
        font.dispose();
    }
}
