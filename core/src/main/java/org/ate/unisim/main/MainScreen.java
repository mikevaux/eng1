package org.ate.unisim.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ate.unisim.UniSim;
import org.ate.unisim.gameover.GameOverScreen;
import org.ate.unisim.main.buildings.Accommodation;
import org.ate.unisim.main.buildings.Cafe;
import org.ate.unisim.main.buildings.Gym;
import org.ate.unisim.main.buildings.LectureHall;
import org.ate.unisim.menu.MenuScreen;

/**
 * The main game screen for UniSim.
 */
public class MainScreen implements Screen {

    private static final int WORLD_WIDTH = 80;
    private static final int WORLD_HEIGHT = 60;
    public static final int TILE_WIDTH = 16;
    public static final int TILE_HEIGHT = 16;

    private static MainScreen INSTANCE;
    private final UniSim game;

    OrthographicCamera mapCamera;
    Viewport mapViewport;
    Viewport uiViewport;

    BitmapFont font;

    TiledMap map;
    TiledMapRenderer mapRenderer;

    int tilesX;
    int tilesY;

    BuildingManager buildingManager;
    GameProgressionTracker tracker;

    /**
     * Creates a new instance of MainScreen, and runs various bootstrap mechanisms.
     * Note that this class uses the Singleton pattern, so this is only used inside `getInstance()`.
     */
    private MainScreen() {
        this.game = UniSim.getInstance();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Set the resolution of the camera, and match this in the viewport
        mapCamera = new OrthographicCamera(32, 32 * (h/w));
        mapViewport = new ExtendViewport(mapCamera.viewportWidth, mapCamera.viewportHeight, mapCamera);

        map = new TmxMapLoader().load("map/map.tmx");
        // unitScale is here set to 1 unit per tile, which makes cell calculations much easier
        mapRenderer = new OrthogonalTiledMapRenderer(map, (float) 1/TILE_WIDTH);
        MapLayers layers = map.getLayers();
        TiledMapTileLayer bottomLayer = (TiledMapTileLayer) layers.get(0);
        tilesX = bottomLayer.getWidth();
        tilesY = bottomLayer.getHeight();

        uiViewport = new ScreenViewport();
        font = new BitmapFont();

        buildingManager = new BuildingManager(map);
        tracker = new GameProgressionTracker();
    }

    /**
     * Gets the single instance of MainScreen, creating if needed.
     *
     * @return the single instance of MainScreen
     */
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
        gameOverCheck();
        input(delta);
        logic();
        draw();
    }

    /**
     * Checks whether the game is over, and sets the respective screen if so, disposing of this one.
     */
    private void gameOverCheck() {
        if (tracker.isGameOver()) {
            game.setScreen(new GameOverScreen());
            dispose();
        }
    }

    /**
     * Handles user input and updates the map / camera accordingly.
     *
     * @param delta the time in seconds since the last render
     */
    private void input(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            game.setScreen(new MenuScreen(true));
            return;
        }

        if (buildingManager.inBuildMode() && Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            buildingManager.exitBuildMode();
        }

        if (buildingManager.inBuildMode()) {
            Vector2 vector = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            mapViewport.unproject(vector);
            int cellX = (int) Math.floor(vector.x);
            int cellY = (int) Math.floor(vector.y);

            buildingManager.setProposalParameters(cellX, cellY);

            if (buildingManager.proposalPossible()) {
                buildingManager.displayProposal();

                if (Gdx.input.justTouched()) {
                    buildingManager.build();
                }
            } else {
                buildingManager.displayImpossible();
            }
        } else {
            if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
                buildingManager.enterBuildMode(new Accommodation());
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
                buildingManager.enterBuildMode(new Cafe());
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
                buildingManager.enterBuildMode(new Gym());
            } else if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
                buildingManager.enterBuildMode(new LectureHall());
            }
        }

        float dist = 0.25f;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            mapCamera.translate(-dist, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            mapCamera.translate(dist, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            mapCamera.translate(0, dist);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            mapCamera.translate(0, -dist);
        }

        float effectiveViewportWidth = mapCamera.viewportWidth * mapCamera.zoom;
        float effectiveViewportHeight = mapCamera.viewportHeight * mapCamera.zoom;
        float xMin = effectiveViewportWidth / 2f;
        float xMax = tilesX - effectiveViewportWidth / 2f;
        float yMin = effectiveViewportHeight / 2f;
        float yMax = tilesY - effectiveViewportHeight / 2f;

        mapCamera.position.x = MathUtils.clamp(mapCamera.position.x, xMin, xMax);
        mapCamera.position.y = MathUtils.clamp(mapCamera.position.y, yMin, yMax);
        mapCamera.update();
    }

    /**
     * Handles logic, and updates accordingly
     */
    private void logic() {

    }

    /**
     * Draws the map and associated UI.
     */
    private void draw() {
        ScreenUtils.clear(Color.BLACK);


        mapViewport.apply();

        mapRenderer.setView(mapCamera);
        mapRenderer.render();
        game.batch.setProjectionMatrix(mapCamera.combined);
        game.batch.begin();
        game.batch.end();

        uiViewport.apply();
        game.batch.setProjectionMatrix(uiViewport.getCamera().combined);
        game.batch.begin();
        font.draw(game.batch, "Remaining Time:\n" + tracker.formatRemainingTime(), 5, 42);
        game.batch.end();

        if (buildingManager.inBuildMode()) {
            buildingManager.clearProposal();
        }
    }

    @Override
    public void resize(int width, int height) {
        mapViewport.update(width, height, true);
        mapCamera.position.set(mapCamera.viewportWidth/2,mapCamera.viewportHeight/2,0);
        mapCamera.update();
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
    public void dispose() {
        map.dispose();
        // Because this is a singleton, we need to manually clear it
        INSTANCE = null;
    }
}
