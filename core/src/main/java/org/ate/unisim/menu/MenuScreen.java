package org.ate.unisim.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import org.ate.unisim.UniSim;
import org.ate.unisim.howtoplay.HowToPlayScreen;
import org.ate.unisim.main.MainScreen;

/**
 * The menu screen, for pre-game and paused states.
 */
public class MenuScreen implements Screen {
    private final UniSim game;
    private boolean gamePaused = false;

    /**
     * Creates a new instance of `MenuScreen`
     */
    public MenuScreen() {
        this.game = UniSim.getInstance();
    }

    /**
     * Creates a new instance of `MenuScreen`, setting the paused state accordingly.
     *
     * @param gamePaused whether the game is currently paused (as opposed to not-yet started)
     */
    public MenuScreen(boolean gamePaused) {
        this();
        this.gamePaused = gamePaused;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "Main Menu", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2 + 2);
        String placeholderCopy = String.format("Press enter to %s....", gamePaused ? "resume" : "start");
        game.font.draw(game.batch, placeholderCopy, game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2);
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            game.setScreen(gamePaused ? MainScreen.getInstance() : new HowToPlayScreen());
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
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

    }
}
