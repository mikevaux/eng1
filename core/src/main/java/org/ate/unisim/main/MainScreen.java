package org.ate.unisim.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import org.ate.unisim.UniSim;
import org.ate.unisim.menu.MenuScreen;

public class MainScreen implements Screen {
    final UniSim game;

    Grid grid;
    BuildingManager buildingManager;
    GameProgressionTracker tracker;

    public MainScreen(final UniSim game) {
        this.game = game;

        grid = new Grid();
        buildingManager = new BuildingManager();

        tracker = new GameProgressionTracker();
    }

    @Override
    public void show() {
        resume();
    }

    @Override
    public void render(float delta) {
        input(delta);
        logic(delta);
        draw();
    }

    private void input(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new MenuScreen(game, this));
        }
    }

    private void logic(float delta) {

    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();
        game.font.draw(game.batch, "UniSim!", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2 + 2);
        game.font.draw(game.batch, "Press Esc for Main Menu", game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2);
        game.font.draw(game.batch, "Remaining Time: " + tracker.displayRemainingTime(), game.viewport.getWorldWidth()/3, game.viewport.getWorldHeight()/2 - 2);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        tracker.pauseCountdown();
    }

    @Override
    public void resume() {
        tracker.resumeCountdown();
    }

    @Override
    public void hide() {
        pause();
    }

    @Override
    public void dispose() {}
}
