package org.ate.unisim.howtoplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import org.ate.unisim.StaticScreen;
import org.ate.unisim.UniSim;
import org.ate.unisim.main.MainScreen;

/**
 * The How to Play screen.
 */
public class HowToPlayScreen extends StaticScreen {
    /**
     * Aspect ratio of the "How to Play" image font. This is to resolve an issue with nested tables containing a scaled
     * image always expanding the nested table to the full height of its parent widget.
     */
    private static final float IMAGE_FONT_HTP_ASPECT_RATIO = 333/1796f;

    /**
     * Creates a new instance of `HowToPlayScreen`.
     */
    public HowToPlayScreen() {
        super();
    }

    @Override
    public void show() {
        Image imageFont = getImageFont("how-to-play.png");
        preserveAspectRatio(imageFont);
        String instructions = """
            - Use the arrow keys (or wasd) to move the map.
            - Click the 'Store' in the top-left of the map to open the Store.
            - From here, click any building to build it.
            - The Store also includes a count of how many of each building has been built.
            - If you change your mind while placing a building, press Esc to cancel.
            - Press 'p' at any time to pause the game.
            - Enjoy!""";
        String startHint = "Press space to start the game...";
        Label instructionsLabel = new Label(instructions, skin);
        Label startHintLabel = new Label(startHint, skin, "window");

        contentTable.row().padBottom(48);
        contentTable.add(imageFont).size(320, 320 * IMAGE_FONT_HTP_ASPECT_RATIO).left();
        contentTable.add(logo).size(48, 48).top().right();
        contentTable.row();
        contentTable.add(instructionsLabel).colspan(2);
        contentTable.row().padTop(24);
        contentTable.add(startHintLabel).colspan(2).center();
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            UniSim.getInstance().setScreen(MainScreen.getInstance());
            dispose();
        }
    }
}
