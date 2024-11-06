package org.ate.unisim;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import org.ate.unisim.menu.MenuScreen;

/**
 * The main 'UniSim' game; the entry point for our application.
 */
public class UniSim extends Game {
    private static UniSim INSTANCE;
    public SpriteBatch batch;
    public BitmapFont font;
    public FitViewport viewport;

    /**
     * Creates a new instance of UniSim.
     * Note that this class uses the Singleton pattern, so this is only used inside `getInstance()`.
     * Note also that as a `ApplicationListener`, the bulk of setup work is done in `create()`.
     */
    private UniSim() {
    }

    /**
     * Gets the single instance of UniSim, creating if needed.
     *
     * @return the single instance of UniSim
     */
    public static UniSim getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new UniSim();
        }
        return INSTANCE;
    }

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        viewport = new FitViewport(64, 48);

        font.setUseIntegerPositions(false);
        font.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

        this.setScreen(new MenuScreen());
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
