package org.ate.unisim.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import org.ate.unisim.main.buildings.Building;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the placeable buildings, utilising 2 dynamically created layers for buildings built, and buildings proposed.
 * Specifically, `BuildingManager` creates one layer on which buildings are built, and one layer on which they are
 * rendered. It 'flattens' the custom tile property `buildable` across all initial map layers into a single data
 * structure which holds whether a given cell is buildable on or not, that is then updated accordingly as new buildings
 * are built.
 */
public class BuildingManager {

    // trackers for the number of each building placed
    private int accommodationsBuilt;
    private int cafesBuilt;
    private int gymsBuilt;
    private int lectureHallsBuilt;

    private final static String TILE_PROPERTY_BUILDABLE = "buildable";

    private static final int CONCURRENT_CONSTRUCTION_LIMIT = 2;
    private boolean buildMode = false;

    TiledMap map;
    TiledMapTileLayer assetsLayer;
    TiledMapTileLayer proposalsLayer;

    boolean[][] buildable;

    Building beingBuilt;
    int proposedCellX;
    int proposedCellY;

    //bools used for incrementing building counter
    boolean accommodation;
    boolean cafe;
    boolean gym;
    boolean lectureHall;

    Map<String, Integer> buildLimits;

    /**
     * Creates a new `BuildingManager`, running all bootstrap methods which this class needs.
     *
     * @param map the tiled map on which the game is played
     */
    BuildingManager(TiledMap map) {
        this.map = map;
        initLayers();
        compileBuildable();

        buildLimits = new HashMap<>();
    }

    /**
     * Creates and adds the assets layer and proposals layer.
     */
    private void initLayers() {
        int tileWidth = MainScreen.TILE_WIDTH;
        int tileHeight = MainScreen.TILE_HEIGHT;

        MapLayers layers = map.getLayers();
        TiledMapTileLayer bottomLayer = (TiledMapTileLayer) layers.get(0);
        assetsLayer = new TiledMapTileLayer(bottomLayer.getWidth(), bottomLayer.getHeight(), tileWidth, tileHeight);
        proposalsLayer = new TiledMapTileLayer(bottomLayer.getWidth(), bottomLayer.getHeight(), tileWidth, tileHeight);
        proposalsLayer.setVisible(false);
        layers.add(assetsLayer);
        layers.add(proposalsLayer);
    }

    /**
     * Compiles the data structure that holds the per-cell boolean value for whether that cell is buildable or not.
     */
    private void compileBuildable() {
        MapLayers layers = map.getLayers();
        TiledMapTileLayer bottomLayer = (TiledMapTileLayer) layers.get(0);
        int tilesX = bottomLayer.getWidth();
        int tilesY = bottomLayer.getHeight();

        buildable = new boolean[tilesX][tilesY];

        for (int x = 0; x < tilesX; x++) {
            for (int y = 0; y < tilesY; y++) {
                buildable[x][y] = flattenBuildable(layers, x, y);
            }
        }
    }

    /**
     * "Flattens" the `buildable` custom property across one or more layers for a given cell (designated by x, y).
     *
     * @param layers the map layers
     * @param x the x coordinate
     * @param y the y coordinate
     * @return true if this cell is buildable across all layers
     */
    private boolean flattenBuildable(MapLayers layers, int x, int y) {
        for (MapLayer layer : layers) {
            TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layer).getCell(x, y);
            // Check for not null here is because layers > 0 may not have all cells set (i.e. where there is nothing
            // present in that layer)
            if (cell != null && !cell.getTile().getProperties().get(TILE_PROPERTY_BUILDABLE, Boolean.class)) {
                return false;
            }
        }
        return true;
    }

    /** Returns true if this is in build mode. */
    public boolean inBuildMode() {
        return buildMode;
    }

    /**
     * Enters build mode, by setting the boolean flag, and updating some instance members.
     *
     * @param building the building being proposed.
     */
    public void enterBuildMode(Building building, String buildingName) {
        buildMode = true;
        beingBuilt = building;
        proposalsLayer.setVisible(true);
        //sets the bool for the building clicked on to true and all others to false
        switch (buildingName){
            case "accommodation":
                accommodation = true;
                cafe = false;
                gym = false;
                lectureHall = false;
                break;
            case "cafe":
                accommodation = false;
                cafe = true;
                gym = false;
                lectureHall = false;
                break;
            case "gym":
                accommodation = false;
                cafe = false;
                gym = true;
                lectureHall = false;
                break;
            case "lecture hall":
                accommodation = false;
                cafe = false;
                gym = false;
                lectureHall = true;
                break;
        }
    }

    /**
     * Exits build mode, by setting the boolean flag, and updating some instance members.
     */
    public void exitBuildMode() {
        clearProposal();
        proposalsLayer.setVisible(false);
        beingBuilt = null;
        buildMode = false;
    }

    /**
     * Sets the values for the new proposal, i.e. the proposed origin cell x and y.
     *
     * @param cellX proposed origin cell x
     * @param cellY proposed origin cell y
     */
    public void setProposalParameters(int cellX, int cellY) {
        proposedCellX = cellX;
        proposedCellY = cellY;
    }

    /**
     * Determines whether the proposed building is possible to build in the proposed region.
     *
     * @return true if the building proposal is possible
     */
    public boolean proposalPossible() {
        int x, y;

        for (int incX = 0; incX < beingBuilt.getRows(); incX++) {
            for (int incY = 0; incY < beingBuilt.getCols(); incY++) {
                x = proposedCellX+incX;
                y = proposedCellY+incY;

                // First, check whether this tile is actually on our map (i.e. not out of bounds of the layer)
                if (x < 0 || y < 0 || x >= buildable.length || y >= buildable[0].length) {
                    return false;
                }
                // Then check whether this tile is buildable
                if (!buildable[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Displays the current proposal, by dynamically creating a new cell and rendering on the proposals layer.
     */
    public void displayProposal() {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        TextureRegion region = new TextureRegion(new Texture(beingBuilt.getAssetPath()));
        cell.setTile(new StaticTiledMapTile(region));
        proposalsLayer.setCell(proposedCellX, proposedCellY, cell);
    }

    public void displayImpossible() {
        // TODO Implement this somehow
    }

    /**
     * Clears the current proposal.
     */
    public void clearProposal() {
        proposalsLayer.setCell(proposedCellX, proposedCellY, null);
    }

    /**
     * Builds the proposed building in the proposed region, and updates the `buildable` data structure accordingly.
     */
    public void build() {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        TextureRegion region = new TextureRegion(new Texture(beingBuilt.getAssetPath()));
        cell.setTile(new StaticTiledMapTile(region));
        assetsLayer.setCell(proposedCellX, proposedCellY, cell);

        for (int incX = 0; incX < beingBuilt.getRows(); incX++) {
            for (int incY = 0; incY < beingBuilt.getCols(); incY++) {
                buildable[proposedCellX+incX][proposedCellY+incY] = false;
            }
        }
        //updates the building counter in the store menu once the building has been placed
        if (accommodation){
            accommodation = false;
            accommodationsBuilt += 1;
        }else if (cafe){
            cafe = false;
            cafesBuilt += 1;
        }else if (gym){
            gym = false;
            gymsBuilt += 1;
        }else if (lectureHall){
            lectureHall = false;
            lectureHallsBuilt += 1;
        }

        exitBuildMode();
    }

    public int getAccommodationsBuilt(){
        return accommodationsBuilt;
    }
    public int getCafesBuilt(){
        return cafesBuilt;
    }
    public int getGymsBuilt(){
        return gymsBuilt;
    }
    public int getLectureHallsBuilt(){
        return lectureHallsBuilt;
    }
}
