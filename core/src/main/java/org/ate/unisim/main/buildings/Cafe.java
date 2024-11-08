package org.ate.unisim.main.buildings;

public class Cafe extends Building {
    /**
     * Creates a new instance of `Cafe`.
     */
    public Cafe() {
        super();

        type = Types.CAFE;
        rows = 9;
        cols = 11;
        filename = "cafe.png";
    }
}
