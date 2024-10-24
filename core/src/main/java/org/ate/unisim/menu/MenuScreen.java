package org.ate.unisim.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import org.ate.unisim.UniSim;

public class MenuScreen implements Screen {
    final UniSim game;
    final Screen mainScreen;

    public MenuScreen(final UniSim game, final Screen mainScreen) {
        this.game = game;
        this.mainScreen = mainScreen;
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
        game.font.draw(game.batch, "Press enter to start....", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2);
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            game.setScreen(mainScreen);
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
