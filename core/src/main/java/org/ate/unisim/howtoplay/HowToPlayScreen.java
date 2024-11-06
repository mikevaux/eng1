package org.ate.unisim.howtoplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import org.ate.unisim.UniSim;
import org.ate.unisim.main.MainScreen;

/**
 * The How to Play screen.
 */
public class HowToPlayScreen implements Screen {
    private final UniSim game;

    /**
     * Creates a new instance of `HowToPlayScreen`.
     */
    public HowToPlayScreen() {
        game = UniSim.getInstance();
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
        game.font.draw(game.batch, "How to play...", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2 + 2);
        game.font.draw(game.batch, "Press space to start....", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2);
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(MainScreen.getInstance());
            dispose();
        }
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

    }
}
