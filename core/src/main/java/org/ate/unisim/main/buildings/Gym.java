package org.ate.unisim.main.buildings;

public class Gym extends Building {
    /**
     * Creates a new instance of `Gym`.
     */
    public Gym() {
        super();

        type = Types.GYM;
        rows = 10;
        cols = 6;
        filename = "gym.png";
    }
}
