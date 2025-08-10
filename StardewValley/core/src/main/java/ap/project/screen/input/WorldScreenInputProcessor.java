package ap.project.screen.input;

import ap.project.control.CharacterController;
import ap.project.control.WorldController;
import ap.project.model.App.App;
import ap.project.model.enums.MapKind;
import ap.project.model.game.*;
import ap.project.model.shops.Shop;
import ap.project.model.shops.ShopMap;
import ap.project.screen.*;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class WorldScreenInputProcessor implements InputProcessor
{
    private final Map map;
    private final PlayerCharacter player;
    private final CharacterController characterController;
    private final OrthographicCamera cam;
    private final WorldScreen worldScreen;
    private final InventoryWindow inventoryWindow;
    private final CommunicationWindow communicationWindow;
    private final ShopWindow shopWindow;

    public WorldScreenInputProcessor(Map map, PlayerCharacter player, CharacterController controller,
                                     OrthographicCamera cam, WorldScreen worldScreen, InventoryWindow inventoryWindow,
                                     CommunicationWindow communicationWindow,
                                     ShopWindow shopWindow)
    {
        this.map = map;
        this.player = player;
        this.characterController = controller;
        this.cam = cam;
        this.worldScreen = worldScreen;
        this.inventoryWindow = inventoryWindow;
        this.communicationWindow = communicationWindow;
        this.shopWindow = shopWindow;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if (worldScreen.isDialogVisible() || worldScreen.isInventoryVisible() ||
            worldScreen.isCookBookVisible() || worldScreen.isRefrigeratorVisible() ||
            worldScreen.isCraftingWindowVisible() || worldScreen.isChatVisible() || worldScreen.isShopWindowVisible())
        {
            return false;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
        {
            Tile tile = worldScreen.cursorToTile();

            if (tile != null)
            {
                System.out.println("Clicked Tile (x: " + tile.getX() + ", y: " + tile.getY() + ") - " + tile.getTexture());
                if (tile.getObject() != null)
                {
                    System.out.println("Object: " + tile.getObject().getObjectType());
                }
//                for (int i = 0; i < farm.getDepth(); i++)
//                {
//                    Tile layerTile = farm.getLayerTiles()[i][tile.getY()][tile.getX()];
//                    if (layerTile != null)
//                    {
//                        System.out.println("Layer " + i + ": " + layerTile.getTypeName());
//                    } else
//                    {
//                        System.out.println("Layer " + i + ": null");
//                    }
//                }
            }

            WorldController.processClickLeft(worldScreen, tile);
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            Point clicked = map.screenToTile(Gdx.input.getX(), Gdx.input.getY(), cam);
            Tile tile = worldScreen.cursorToTile();

            if (tile != null)
            {
                WorldController.processClickRight(worldScreen, tile);
            }

//            if (clicked != null)
//            {
//                Vector2 playerPos = player.getPosition();
//                Point playerTile = map.worldToTile(playerPos.x, playerPos.y);
//
//                ArrayList<Point> path = map.findShortestPath(playerTile, clicked);
//
//                if (path != null)
//                {
//                    characterController.moveToPath(path);
//                }
//            }
        }

//        if (map.getMapType().getMapKind() == MapKind.TOWN) {
//            handleShopDoorClick(screenX, screenY);
//        }

        return true;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (worldScreen.isDialogVisible() || worldScreen.isInventoryVisible() ||
            worldScreen.isCookBookVisible() || worldScreen.isRefrigeratorVisible() ||
        worldScreen.isCraftingWindowVisible() || worldScreen.isChatVisible() || worldScreen.isShopWindowVisible() ||
        worldScreen.isPurchaseWindowVisible())
        {
            return false;
        }

        if (keycode == Input.Keys.E ||  keycode == Input.Keys.ESCAPE) {
            boolean nowVisible = !inventoryWindow.isVisible();
            inventoryWindow.toggleVisibility();

            if (nowVisible)
                inventoryWindow.getToolsTab().setChecked(false);

            return true;
        }
        else if (keycode == Input.Keys.TAB) {
            boolean nowVisible = !inventoryWindow.isVisible();
            inventoryWindow.toggleVisibility();

            if (nowVisible) {
                if (inventoryWindow.getLastTabOpenedByTabKey() == InventoryWindow.TabType.TOOLS) {
                    inventoryWindow.getToolsTab().toggle();
                } else {
                    // Default to Inventory
                    inventoryWindow.getToolsTab().setChecked(false);
                }
            }

            return true;
        }
        else if (keycode == Input.Keys.M) {
            inventoryWindow.toggleVisibility();
            inventoryWindow.getMapTab().setChecked(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        // Don't process character input if chat is visible
        if (worldScreen.isChatVisible()) {
            return false;
        }

        if (!worldScreen.isDialogVisible() && (character=='t' || character=='T'))
        {
            worldScreen.toggleTerminalDialog();
            return true;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode)    { return false; }
    @Override public boolean touchUp(int x, int y, int p, int b) { return false; }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override public boolean touchDragged(int x, int y, int p)   { return false; }
    @Override public boolean mouseMoved(int x, int y) { return false; }
    @Override public boolean scrolled(float x, float y) { return false; }

    private Player findNearbyPlayer(Point center, int radius) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int tx = center.getX() + dx;
                int ty = center.getY() + dy;

                Player found = App.getCurrentGame().getPlayerByLocation(new Point(tx, ty));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }




}
