package org.ate.unisim.main;

public class Map {
    /**
     * Holds initial cells
     */
    private final boolean[][] initiallyFilled;

    Map() {
        // Abbreviated to make the array declaration more readable!
        boolean t = true;
        boolean f = false;

        initiallyFilled = new boolean[][] {
            {t, f},
            {f, t}
        };
    }

    public boolean[][] getInitiallyFilled() {
        return initiallyFilled;
    }
}
