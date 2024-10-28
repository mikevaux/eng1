package org.ate.unisim.main.buildings;

public abstract class Building {
    int rows;
    int cols;
    String filename;

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public String getAssetPath() {
        return "buildings/" + filename;
    }
}
