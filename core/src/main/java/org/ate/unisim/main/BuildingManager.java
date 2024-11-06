package org.ate.unisim.main;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import org.ate.unisim.main.buildings.Building;

import java.util.HashMap;
import java.util.Map;

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
    TiledMapTileLayer baseLayer;
    TiledMapTileLayer assetsLayer;
    TiledMapTileLayer proposalsLayer;

    boolean[][] builtOn;

    Building beingBuilt;
    int proposedCellX;
    int proposedCellY;

    //bools used for incrementing building counter
    boolean accommodation;
    boolean cafe;
    boolean gym;
    boolean lectureHall;

    Map<String, Integer> buildLimits;


    BuildingManager(TiledMap map) {
        this.map = map;
        initLayers();

        buildLimits = new HashMap<>();
    }

    private void initLayers() {
        int tileWidth = MainScreen.TILE_WIDTH;
        int tileHeight = MainScreen.TILE_HEIGHT;

        MapLayers layers = map.getLayers();
        baseLayer = (TiledMapTileLayer) layers.get(0);
        assetsLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(), tileWidth, tileHeight);
        proposalsLayer = new TiledMapTileLayer(baseLayer.getWidth(), baseLayer.getHeight(), tileWidth, tileHeight);
        proposalsLayer.setVisible(false);
        layers.add(assetsLayer);
        layers.add(proposalsLayer);

        builtOn = new boolean[assetsLayer.getWidth()][assetsLayer.getHeight()];
    }

    public boolean inBuildMode() {
        return buildMode;
    }

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

    public void exitBuildMode() {
        clearProposal();
        proposalsLayer.setVisible(false);
        beingBuilt = null;
        buildMode = false;
    }

    public void setProposalParameters(int cellX, int cellY) {
        proposedCellX = cellX;
        proposedCellY = cellY;
    }

    public boolean proposalPossible() {
        int x, y;

        for (int incX = 0; incX < beingBuilt.getRows(); incX++) {
            for (int incY = 0; incY < beingBuilt.getCols(); incY++) {
                x = proposedCellX+incX;
                y = proposedCellY+incY;

                // First, check whether this cell exists at all. Then check the property of the respective tile.
                TiledMapTileLayer.Cell cell = baseLayer.getCell(x, y);
                if (cell == null || !cell.getTile().getProperties().get(TILE_PROPERTY_BUILDABLE, Boolean.class)) {
                    return false;
                }
                // Now check if we have already built on this cell
                if (builtOn[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    public void displayProposal() {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        TextureRegion region = new TextureRegion(new Texture(beingBuilt.getAssetPath()));
        cell.setTile(new StaticTiledMapTile(region));
        proposalsLayer.setCell(proposedCellX, proposedCellY, cell);
    }

    public void displayImpossible() {
        // TODO Implement this somehow
    }

    public void clearProposal() {
        proposalsLayer.setCell(proposedCellX, proposedCellY, null);
    }

    public void build() {
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        TextureRegion region = new TextureRegion(new Texture(beingBuilt.getAssetPath()));
        cell.setTile(new StaticTiledMapTile(region));
        assetsLayer.setCell(proposedCellX, proposedCellY, cell);

        for (int incX = 0; incX < beingBuilt.getRows(); incX++) {
            for (int incY = 0; incY < beingBuilt.getCols(); incY++) {
                builtOn[proposedCellX+incX][proposedCellY+incY] = true;
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
