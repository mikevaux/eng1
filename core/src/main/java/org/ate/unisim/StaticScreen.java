package org.ate.unisim;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Base class for static screens, which loads common elements.
 */
abstract public class StaticScreen implements Screen {
    /**
     * The stage for which all actors are added to.
     */
    protected final Stage stage;
    /**
     * The common skin.
     */
    protected final Skin skin;
    /**
     * The root table, which fills the parent container.
     */
    protected final Table rootTable;
    /**
     * The table which holds the actual content.
     */
    protected final Table contentTable;
    /**
     * The game logo, used in most static screens.
     */
    protected final Image logo;


    /**
     * Abstract constructor, which loads common elements.
     */
    public StaticScreen() {
        stage = new Stage(new ScreenViewport());
        stage.getViewport().apply();
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        rootTable = new Table();
        rootTable.pad(32);
        rootTable.setFillParent(true);

        stage.addActor(rootTable);
        contentTable = new Table();
        rootTable.add(contentTable);

        logo = new Image(new Texture("logo.png"));
        preserveAspectRatio(logo);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.DARK_GRAY);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
        skin.dispose();
    }

    /**
     * Helper method for centralising aspect ratio preserving.
     *
     * @param img the image for which to preserve the aspect ratio
     */
    protected void preserveAspectRatio(Image img) {
        img.setScaling(Scaling.fit);
    }

    /**
     * Centralised method of obtaining an "image font", i.e. a graphic of a font.
     *
     * @param filename the filename of the graphic, including the extension
     * @return the UI Image
     */
    protected Image getImageFont(String filename) {
        return new Image(new Texture("image-font/" + filename));
    }
}
