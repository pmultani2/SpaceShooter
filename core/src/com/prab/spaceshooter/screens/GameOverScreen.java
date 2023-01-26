package com.prab.spaceshooter.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.prab.spaceshooter.SpaceGame;

public class GameOverScreen implements Screen {
    private static final int BANNER_WIDTH = 350;
    private static final int BANNER_HEIGHT = 100;
    SpaceGame game;
    int score, highscore;
    int suddenDeathHighScore;
    Texture gameOverBanner;
    BitmapFont scoreFont;
    boolean suddenDeath;

    public GameOverScreen(SpaceGame game, int score, boolean suddenDeath) {
        this.game = game;
        this.score = score;
        this.suddenDeath = suddenDeath;
        // Get high score from prefs file
        Preferences prefs = Gdx.app.getPreferences("spaceshooter");
        this.suddenDeathHighScore = prefs.getInteger("suddendeathhighscore", 0);
        this.highscore = prefs.getInteger("highscore", 0);
        // Check if score beats high score
        if (score > highscore) {
            this.highscore = score;
            prefs.putInteger("highscore", score);
            prefs.flush(); // Saves file
        }
        if (this.suddenDeath) {
            if (score > suddenDeathHighScore) {
                this.suddenDeathHighScore = score;
                prefs.putInteger("suddendeathhighscore", score);
                prefs.flush(); // Saves file
            }
        }
        // Load textures and fonts
        gameOverBanner = new Texture("game_over.png");
        scoreFont = new BitmapFont(Gdx.files.internal("fonts/score.fnt.txt"));
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 0);
        game.batch.begin();
        game.batch.draw(gameOverBanner, Gdx.graphics.getWidth() / 2 - BANNER_WIDTH / 2,  Gdx.graphics.getHeight() - BANNER_HEIGHT - 15, BANNER_WIDTH, BANNER_HEIGHT);
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont, "Score: \n" + score, Color.WHITE, 0, Align.left, false);
        GlyphLayout highScoreLayout;
        if (suddenDeath)
            highScoreLayout = new GlyphLayout(scoreFont, "High Score: \n" + suddenDeathHighScore, Color.WHITE, 0, Align.left, false);
        else
            highScoreLayout = new GlyphLayout(scoreFont, "High Score: \n" + highscore, Color.WHITE, 0, Align.left, false);
        scoreFont.draw(game.batch, scoreLayout, Gdx.graphics.getWidth() / 2 - scoreLayout.width / 2, Gdx.graphics.getHeight() - BANNER_HEIGHT - 15 * 2);
        scoreFont.draw(game.batch, highScoreLayout, Gdx.graphics.getWidth() / 2 - highScoreLayout.width / 2, Gdx.graphics.getHeight() - BANNER_HEIGHT - scoreLayout.height - 15 * 4);
        GlyphLayout tryAgainLayout = new GlyphLayout(scoreFont, "Try Again");
        GlyphLayout mainMenuLayout = new GlyphLayout(scoreFont, "Main Menu");
        float tryAgainX = Gdx.graphics.getWidth() / 2 - tryAgainLayout.width / 2;
        float tryAgainY = tryAgainLayout.height + 100;
        float mainMenuX = Gdx.graphics.getWidth() / 2 - mainMenuLayout.width / 2;
        float mainMenuY = mainMenuLayout.height + 50;
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();
        if (touchX > tryAgainX && touchX < tryAgainX + tryAgainLayout.width && touchY > tryAgainY - tryAgainLayout.height && touchY < tryAgainY) {
          tryAgainLayout.setText(scoreFont, "Try Again", Color.YELLOW, 0, Align.left, false);
          if (Gdx.input.isTouched()) {
              this.dispose();
              game.batch.end();
              if (suddenDeath) {
                  game.setScreen(new SuddenDeathScreen(game));
              } else {
                  game.setScreen(new MainGameScreen(game));
              }
              return;
          }
        }
        if (touchX > mainMenuX && touchX < mainMenuX + mainMenuLayout.width && touchY > mainMenuY - mainMenuLayout.height && touchY < mainMenuY) {
            mainMenuLayout.setText(scoreFont, "Main Menu", Color.YELLOW, 0, Align.left, false);
            if (Gdx.input.isTouched()) {
                this.dispose();
                game.batch.end();
                game.setScreen(new MainMenuScreen(game));
                return;
            }
        }
        scoreFont.draw(game.batch, tryAgainLayout, tryAgainX, tryAgainY);
        scoreFont.draw(game.batch, mainMenuLayout, mainMenuX, mainMenuY);
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

    }

    @Override
    public void dispose() {
        scoreFont.dispose();
        gameOverBanner.dispose();
    }
}
