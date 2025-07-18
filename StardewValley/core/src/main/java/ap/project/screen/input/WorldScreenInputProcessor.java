package ap.project.screen.input;

import ap.project.control.CharacterController;
import ap.project.model.App.App;
import ap.project.model.game.*;
import ap.project.screen.WorldScreen;
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

    public WorldScreenInputProcessor(Map map, PlayerCharacter player, CharacterController controller,
                                     OrthographicCamera cam, WorldScreen worldScreen)
    {
        this.map = map;
        this.player = player;
        this.characterController = controller;
        this.cam = cam;
        this.worldScreen = worldScreen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if (worldScreen.isDialogVisible() || worldScreen.isInventoryVisible())
        {
            return false;
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT))
        {
            Tile tile = worldScreen.cursorToTile();

            if (tile != null)
            {
                System.out.println("Clicked Tile (x: " + tile.getX() + ", y: " + tile.getY() + ") - " + tile.getTexture());
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
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))
        {
            Point clicked = map.screenToTile(Gdx.input.getX(), Gdx.input.getY(), cam);

            if (clicked != null)
            {
                Vector2 playerPos = player.getPosition();
                Point playerTile = map.worldToTile(playerPos.x, playerPos.y);

                ArrayList<Point> path = map.findShortestPath(playerTile, clicked);

                if (path != null)
                {
                    characterController.moveToPath(path);
                }
            }
        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (worldScreen.isDialogVisible() || worldScreen.isInventoryVisible())
        {
            return false;
        }

        if (keycode == Input.Keys.E || keycode == Input.Keys.ESCAPE)
        {
            worldScreen.toggleInventoryWindow();
            return true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C))
        {
            App.getCurrentGame().getCurrentPlayer().goToFarm();
            return true;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
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
}

