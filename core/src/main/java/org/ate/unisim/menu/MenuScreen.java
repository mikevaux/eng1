package org.ate.unisim.menu;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import org.ate.unisim.StaticScreen;
import org.ate.unisim.UniSim;
import org.ate.unisim.howtoplay.HowToPlayScreen;
import org.ate.unisim.main.MainScreen;

/**
 * Abstract menu screen, extended by different types of menus.
 */
public class MenuScreen extends StaticScreen {
    /**
     * Aspect ratio of the "Play" image font. This is to resolve an issue with nested tables containing a scaled image
     * always expanding the nested table to the full height of its parent widget.
     */
    private static final float IMAGE_FONT_PLAY_ASPECT_RATIO = 963/1745f;
    /**
     * Aspect ratio of the "Resume" image font. This is to resolve an issue with nested tables containing a scaled image
     * always expanding the nested table to the full height of its parent widget.
     */
    private static final float IMAGE_FONT_RESUME_ASPECT_RATIO = 354/1760f;
    /**
     * Whether the game is paused when this menu shows.
     */
    private boolean gamePaused = false;

    /**
     * Creates a new instance of `MenuScreen`
     */
    public MenuScreen() {
        super();
    }

    /**
     * Creates a new instance of `MenuScreen`, setting the paused state accordingly.
     *
     * @param gamePaused whether the game is currently paused (as opposed to not-yet started)
     */
    public MenuScreen(boolean gamePaused) {
        this();
        this.gamePaused = gamePaused;
    }

    @Override
    public void show() {
        String imageFontFilename = gamePaused ? "resume.png" : "play.png";
        // A possible bug with GDX means that even when the aspect ratio is preserved on all nested image widgets,
        // the height of the centred table was far in excess of the total height of the nested images, meaning that
        // the contents were not centring properly. This workaround fixes this.
        float imageFontFileAR = gamePaused ? IMAGE_FONT_RESUME_ASPECT_RATIO : IMAGE_FONT_PLAY_ASPECT_RATIO;

        contentTable.row().padBottom(24);
        contentTable.add(logo).size(200, 200);
        contentTable.row();
        contentTable.add(getImageFont(imageFontFilename)).size(100, 100 * imageFontFileAR);

        contentTable.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                UniSim.getInstance().setScreen(gamePaused ? MainScreen.getInstance() : new HowToPlayScreen());
                dispose();
            }
        });
    }
}
