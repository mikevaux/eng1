package org.ate.unisim.gameover;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ate.unisim.UniSim;

/**
 * The Game Over screen.
 */
public class GameOverScreen implements Screen {
    private final UniSim game;
    Viewport viewport;

    /**
     * Creates a new instance of `GameOverScreen`.
     */
    public GameOverScreen() {
        game = UniSim.getInstance();
        viewport = new ScreenViewport();
        viewport.apply();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        game.font.draw(game.batch, "GAME OVER", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2 + 2);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
