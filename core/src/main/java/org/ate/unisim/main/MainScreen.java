package org.ate.unisim.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ate.unisim.UniSim;
import org.ate.unisim.menu.MenuScreen;

public class MainScreen implements Screen {

    private static final int WORLD_WIDTH = 32;
    private static final int WORLD_HEIGHT = 32;

    private static MainScreen INSTANCE;
    private final UniSim game;

    OrthographicCamera mapCamera;
    Viewport mapViewport;
    Viewport uiViewport;

    BitmapFont font;
    Sprite mapSprite;

    Grid grid;
    BuildingManager buildingManager;
    GameProgressionTracker tracker;

    private MainScreen() {
        this.game = UniSim.getInstance();

        mapSprite = new Sprite(new Texture("map.png"));
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(WORLD_WIDTH, WORLD_HEIGHT);

        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        mapCamera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT * (h/w));
        mapViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, mapCamera);

        uiViewport = new ScreenViewport();
        font = new BitmapFont();

        grid = new Grid();
        buildingManager = new BuildingManager();

        tracker = new GameProgressionTracker();
    }

    public static MainScreen getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MainScreen();
        }
        return INSTANCE;
    };

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
            game.setScreen(new MenuScreen(true));
        }

        mapCamera.update();
    }


    private void logic(float delta) {

    }

    private void draw() {
        ScreenUtils.clear(Color.OLIVE);

        mapViewport.apply();
        game.batch.setProjectionMatrix(mapCamera.combined);
        game.batch.begin();
        mapSprite.draw(game.batch);
        game.batch.end();

        uiViewport.apply();
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.begin();
        font.draw(game.batch, "Remaining Time:\n" + tracker.displayRemainingTime(), 5, 42);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        mapViewport.update(width, height, true);
        mapCamera.position.set(mapCamera.viewportWidth/2,mapCamera.viewportHeight/2,0);
        uiViewport.update(width, height, true);
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
