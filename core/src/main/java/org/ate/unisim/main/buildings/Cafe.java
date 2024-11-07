package org.ate.unisim.main.buildings;

public class Cafe extends Building {
    /**
     * Creates a new instance of `Cafe`.
     */
    public Cafe() {
        super();

        type = Types.CAFE;
        rows = 12;
        cols = 14;
        filename = "cafe.png";
    }
}
