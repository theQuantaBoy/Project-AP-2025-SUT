package ap.project.screen.input;

import ap.project.control.CharacterController;
import ap.project.model.game.Farm;
import ap.project.model.game.PlayerCharacter;
import ap.project.model.game.Point;
import ap.project.model.game.Tile;
import ap.project.screen.WorldScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class WorldScreenInputProcessor implements InputProcessor
{
    private final Farm farm;
    private final PlayerCharacter player;
    private final CharacterController characterController;
    private final OrthographicCamera cam;
    private final WorldScreen worldScreen;

    public WorldScreenInputProcessor(Farm farm, PlayerCharacter player, CharacterController controller,
                                     OrthographicCamera cam, WorldScreen worldScreen)
    {
        this.farm = farm;
        this.player = player;
        this.characterController = controller;
        this.cam = cam;
        this.worldScreen = worldScreen;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
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
            Point clicked = farm.screenToTile(Gdx.input.getX(), Gdx.input.getY(), cam);

            if (clicked != null)
            {
                Vector2 playerPos = player.getPosition();
                Point playerTile = farm.worldToTile(playerPos.x, playerPos.y);

                ArrayList<Point> path = farm.findShortestPath(playerTile, clicked);

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
        if (!worldScreen.isDialogVisible() && Gdx.input.isKeyJustPressed(Input.Keys.T))
        {
            worldScreen.showTestDialog();
            return true;
        }
        return false;
    }

    @Override public boolean keyUp(int keycode)    { return false; }
    @Override public boolean keyTyped(char character) { return false; }
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

