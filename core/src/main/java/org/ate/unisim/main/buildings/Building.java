package org.ate.unisim.main.buildings;

/**
 * Represents a University building which can be built on the map.
 */
public abstract class Building {
    /**
     * Enumeration of all possible building types. Used as a means of identifying which building an instance is.
     */
    public enum Types {
        ACCOMMODATION,
        CAFE,
        GYM,
        LECTURE_HALL
    }

    /**
     * The 'type' of this building.
     */
    Types type;

    /**
     * The number of rows this `Building` occupies on the map. (Should be a multiple of `MainScreen.TILE_WIDTH`)
     */
    int rows;

    /**
     * The number of cols this `Building` occupies on the map. (Should be a multiple of `MainScreen.TILE_WIDTH`)
     */
    int cols;

    /**
     * The filename for the respective asset, relative to the `buildings/` folder, and including the extension,
     * e.g. "my-shiny-building.png"
     */
    String filename;

    public Types getType() {
        return type;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /**
     * Returns the path to this building's asset, which will be housed in the `buildings/` folder.
     *
     * @return the path to this building's asset
     */
    public String getAssetPath() {
        return "buildings/" + filename;
    }
}
