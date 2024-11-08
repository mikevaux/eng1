package org.ate.unisim.main.buildings;

public class Accommodation extends Building {
    /**
     * Creates a new instance of `Accommodation`.
     */
    public Accommodation() {
        super();

        type = Types.ACCOMMODATION;
        rows = 9;
        cols = 9;
        filename = "accommodation.png";
    }
}
