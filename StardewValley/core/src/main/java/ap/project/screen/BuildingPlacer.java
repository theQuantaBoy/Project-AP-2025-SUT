package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.game.*;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class BuildingPlacer {
    private boolean isActive = false;
    private FarmBuildingType buildingType;
    private TextureRegion ghostTexture;
    private Point ghostPosition;
    private WorldScreen worldScreen;
    private boolean isValidPosition = false;

    public BuildingPlacer(WorldScreen worldScreen) {
        this.worldScreen = worldScreen;
    }

    public void startPlacement(FarmBuildingType buildingType) {
        this.buildingType = buildingType;
        this.isActive = true;
        this.ghostTexture = new TextureRegion(buildingType.texture);
        this.ghostPosition = new Point(10, 10); // Default position

        // Switch to farm view
        worldScreen.getCurrentPlayer().setCurrentMap(worldScreen.getCurrentPlayer().getFarm());
        worldScreen.updateGameInfo();
    }

    public void cancelPlacement() {
        isActive = false;
        ghostTexture = null;
        worldScreen.getCurrentPlayer().returnToPreviousMap();
        worldScreen.updateGameInfo();
    }

    public void updateCursorPosition(Point tilePosition) {
        if (isActive) {
            ghostPosition = tilePosition;
            // Validate new position
            Farm farm = worldScreen.getCurrentPlayer().getFarm();
        }
    }

    public void render(Batch batch) {
        if (!isActive) return;
        Vector2 worldPos = worldScreen.getMap().tileToWorld(new Tile(ghostPosition));
        batch.setColor(1, 1, 1, isValidPosition ? 0.7f : 0.4f); // Semi-transparent
        batch.draw(ghostTexture,
            worldPos.x,
            worldPos.y,
            buildingType.getWidth() * WorldScreen.TILE_SIZE,
            buildingType.getHeight() * WorldScreen.TILE_SIZE);
        batch.setColor(1, 1, 1, 1); // Reset color
    }

    public boolean tryPlaceBuilding(Point position) {
        if (!isActive || !isValidPosition) return false;

        Player player = worldScreen.getCurrentPlayer();
        Farm farm = player.getFarm();

        // Validate resources
        for (GameObject requirement : buildingType.getRequirements()) {
            if (!player.getInventory().contains(requirement)) {
                UIRenderer.showTextBox("Not enough " + requirement.getObjectType().name());
                return false;
            }
        }

        // Deduct resources
        player.increaseMoney(-buildingType.getPrice());
        for (GameObject requirement : buildingType.getRequirements()) {
            player.removeFromInventory(requirement);
        }

        // Create building


        // Exit placement mode
        isActive = false;
        ghostTexture = null;
        UIRenderer.showTextBox(buildingType.getName() + " built!");
        return true;
    }

    public boolean isActive() {
        return isActive;
    }
}
