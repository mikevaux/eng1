package org.ate.unisim.main.buildings;

public class Accommodation extends Building {
    /**
     * Creates a new instance of `Accommodation`.
     */
    public Accommodation() {
        super();

        type = Types.ACCOMMODATION;
        rows = 12;
        cols = 12;
        filename = "accommodation.png";
    }
}
