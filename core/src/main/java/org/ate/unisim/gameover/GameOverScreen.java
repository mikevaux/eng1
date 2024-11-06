package org.ate.unisim.gameover;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import org.ate.unisim.StaticScreen;

/**
 * The Game Over screen.
 */
public class GameOverScreen extends StaticScreen {
    /**
     * Creates a new instance of `GameOverScreen`.
     */
    public GameOverScreen() {
        super();
    }

    @Override
    public void show() {
        Image imageFont = getImageFont("game-over.png");
        preserveAspectRatio(imageFont);
        contentTable.add(imageFont).width(280);
    }
}
