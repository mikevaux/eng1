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

public class BuildingManager {
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

    Map<String, Integer> buildLimits;


    BuildingManager(TiledMap map) {
        this.map = map;
        initLayers();
        compileBuildable();

        buildLimits = new HashMap<>();
    }

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

    public boolean inBuildMode() {
        return buildMode;
    }

    public void enterBuildMode(Building building) {
        buildMode = true;
        beingBuilt = building;
        proposalsLayer.setVisible(true);
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
                buildable[proposedCellX+incX][proposedCellY+incY] = false;
            }
        }

        exitBuildMode();
    }
}
