package org.ate.unisim.main;

public class Grid {
    private GridCell[][] gridCells;

    /**
     * Instantiates a new Grid, setting the initial configuration of `gridCells` to that of the specified map.
     */
    Grid() {
        boolean[][] initiallyFilled = (new Map()).getInitiallyFilled();
        // Setting the columns array to be the length of the first row's column length
        // here assumes that the map has rows of all the same length. This should
        // probably be tested when it comes to that.
        gridCells = new GridCell[initiallyFilled.length][initiallyFilled[0].length];

        for (int row = 0; row < initiallyFilled.length; row ++) {
            for (int col = 0; col < initiallyFilled[row].length; col ++) {
                gridCells[row][col] = new GridCell(initiallyFilled[row][col]);
            }
        }
    }
}
