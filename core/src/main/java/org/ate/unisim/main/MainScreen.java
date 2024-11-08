package org.ate.unisim.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import org.ate.unisim.UniSim;
import org.ate.unisim.gameover.GameOverScreen;
import org.ate.unisim.main.buildings.*;
import org.ate.unisim.menu.MenuScreen;

/**
 * The main game screen for UniSim.
 */
public class MainScreen implements Screen {
    /**
     * The width of a tile, in pixels.
     */
    public static final int TILE_WIDTH = 16;
    /**
     * The height of a tile, in pixels.
     */
    public static final int TILE_HEIGHT = 16;

    private static MainScreen INSTANCE;
    private final UniSim game;

    OrthographicCamera mapCamera;
    Viewport mapViewport;
    Viewport uiViewport;

    TiledMap map;
    TiledMapRenderer mapRenderer;

    int tilesX;
    int tilesY;

    BuildingManager buildingManager;
    GameProgressionTracker tracker;

    Stage ui;
    TextButton toggleStoreButton;
    Label remainingTimeLabel;
    Label accommodationNumber;
    Label cafeNumber;
    Label gymNumber;
    Label lectureHallNumber;

    /**
     * Creates a new instance of MainScreen, and runs various bootstrap mechanisms.
     * Note that this class uses the Singleton pattern, so this is only used inside `getInstance()`.
     */
    private MainScreen() {
        this.game = UniSim.getInstance();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();

        // Set the resolution of the camera, and match this in the viewport
        mapCamera = new OrthographicCamera(48, 48 * (h / w));
        mapViewport = new ExtendViewport(mapCamera.viewportWidth, mapCamera.viewportHeight, mapCamera);

        map = new TmxMapLoader().load("map/map.tmx");
        // unitScale is here set to 1 unit per tile, which makes cell calculations much easier
        mapRenderer = new OrthogonalTiledMapRenderer(map, (float) 1 / TILE_WIDTH);
        MapLayers layers = map.getLayers();
        TiledMapTileLayer bottomLayer = (TiledMapTileLayer) layers.get(0);
        tilesX = bottomLayer.getWidth();
        tilesY = bottomLayer.getHeight();

        uiViewport = new ScreenViewport();

        buildingManager = new BuildingManager(map);
        tracker = new GameProgressionTracker();

        initUi();
    }

    /**
     * creates stages and ui elements to display the store menu and button.
     */
    private void initUi() {
        ui = new Stage(uiViewport);

        // creates the skin used for buttons and labels
        Skin skin = new Skin(Gdx.files.internal("skin/clean-crispy-ui.json"));
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = skin.getDrawable("button-c");
        style.font = new BitmapFont();
        style.fontColor = Color.FIREBRICK;

        // the button that opens and closes the store
        toggleStoreButton = new TextButton("Toggle Store", style);
        remainingTimeLabel = new Label("", new Skin(new FileHandle("ui/uiskin.json")), "window");

        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.LIGHT_GRAY);
        bgPixmap.fill();
        TextureRegionDrawable bg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));

        // labels for the different building types
        Label accommodationLabel = new Label("Accommodation", skin);
        Label cafeLabel = new Label("Cafe", skin);
        Label gymLabel = new Label("Gym", skin);
        Label lectureHallLabel = new Label("Lecture Hall", skin);

        // labels stating the number of each building type placed
        accommodationNumber = new Label("0 built", skin);
        cafeNumber = new Label("0 built", skin);
        gymNumber = new Label("0 built", skin);
        lectureHallNumber = new Label("0 built", skin);

        ImageButton accommodationButton = makeImageButton((new Accommodation()).getAssetPath());
        ImageButton cafeButton = makeImageButton((new Cafe()).getAssetPath());
        ImageButton gymButton = makeImageButton((new Gym()).getAssetPath());
        ImageButton lectureHallButton = makeImageButton((new LectureHall()).getAssetPath());

        Table root = new Table();
        root.setFillParent(true);

        // table storing the store ui
        Table store = new Table().pad(24);
        store.setBackground(bg);
        store.add(accommodationLabel);
        store.add(cafeLabel);
        store.add(gymLabel);
        store.add(lectureHallLabel);
        store.row();
        store.add(accommodationButton).pad(12);
        store.add(cafeButton).pad(12);
        store.add(gymButton).pad(12);
        store.add(lectureHallButton).pad(12);
        store.row();
        store.add(accommodationNumber);
        store.add(cafeNumber);
        store.add(gymNumber);
        store.add(lectureHallNumber);
        store.setVisible(false);

        root.add(store);

        ui.addActor(root);
        ui.addActor(toggleStoreButton);
        ui.addActor(remainingTimeLabel);

        // click listeners for all buttons
        toggleStoreButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                store.setVisible(!store.isVisible());
            }
        });

        accommodationButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                    buildingManager.enterBuildMode(new Accommodation());
                    store.setVisible(false);

            }
        });
        cafeButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                    buildingManager.enterBuildMode(new Cafe());
                    store.setVisible(false);
            }
        });
        gymButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                    buildingManager.enterBuildMode(new Gym());
                    store.setVisible(false);
            }
        });
        lectureHallButton.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                    buildingManager.enterBuildMode(new LectureHall());
                    store.setVisible(false);
            }
        });
    }

    /**
     * Centralised mechanism for the repetitive job of making a "build X" button for the store.
     *
     * @param assetPath the path to the graphic for this building
     * @return the ImageButton for the respective building
     */
    private ImageButton makeImageButton(String assetPath) {
        return new ImageButton(new TextureRegionDrawable(new Texture(assetPath)));
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
    }

    @Override
    public void show() {
        resume();
        Gdx.input.setInputProcessor(ui);
    }

    /**
     * updates the labels to display the current number of each building.
     */
    private void updateLabels() {
        accommodationNumber.setText(buildingManager.getBuildCount(Building.Types.ACCOMMODATION) + " built");
        cafeNumber.setText(buildingManager.getBuildCount(Building.Types.CAFE) + " built");
        gymNumber.setText(buildingManager.getBuildCount(Building.Types.GYM) + " built");
        lectureHallNumber.setText(buildingManager.getBuildCount(Building.Types.LECTURE_HALL) + " built");
    }

    @Override
    public void render(float delta) {
        gameOverCheck();
        input(delta);
        logic();
        draw(delta);
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
                    // updates the labels to display current building counts
                    updateLabels();
                }
            } else {
                buildingManager.displayImpossible();
            }
        }

        float dist = 32f * delta;
        // movement keys
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            mapCamera.translate(-dist, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            mapCamera.translate(dist, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            mapCamera.translate(0, dist);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
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
        remainingTimeLabel.setText("Remaining time: " + tracker.formatRemainingTime());
    }

    /**
     * Draws the map and associated UI.
     */
    private void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);

        mapViewport.apply();
        mapRenderer.setView(mapCamera);
        mapRenderer.render();

        uiViewport.apply();

        if (buildingManager.inBuildMode()) {
            buildingManager.clearProposal();
        }
        //draws the stages
        ui.draw();
    }

    @Override
    public void resize(int width, int height) {
        mapViewport.update(width, height, true);
        mapCamera.position.set(mapCamera.viewportWidth / 2, mapCamera.viewportHeight / 2, 0);
        mapCamera.update();
        uiViewport.update(width, height, true);

        // Reposition absolutely positioned UI elements
        toggleStoreButton.setPosition(5, Gdx.graphics.getHeight() - 50f);
        remainingTimeLabel.setPosition(12, 26);
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
