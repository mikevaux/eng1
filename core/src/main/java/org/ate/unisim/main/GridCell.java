package org.ate.unisim.main;

public class GridCell {
    private boolean filled;

    GridCell(boolean filled) {
        this.filled = filled;
    }

    boolean isFilled() {
        return filled;
    }

    void setFilled(boolean filled) {
        this.filled = filled;
    }
}
