package ap.project.model.shops;

import ap.project.model.App.App;
import ap.project.model.enums.MapKind;
import ap.project.model.enums.ShopType;
import ap.project.model.game.Map;
import ap.project.model.game.Point;
import ap.project.util.MapAssetLoader;
import ap.project.visual.MapVisual;
import com.badlogic.gdx.Gdx;

import java.util.ArrayList;
import java.util.List;

public abstract class ShopMap extends Map {
    private final ShopType shopType;
    private Point doorPosition;
    protected List<ShopProduct> products = new ArrayList<>();
    private int startWork;
    private int endWork;

    public ShopMap(ShopType shopType) {
        super(shopType.getMapType());
        this.shopType = shopType;
        this.startWork = 0;
        this.endWork = 1;
        loadMap();
    }

    private void loadMap() {
        MapAssetLoader.LoadedMap loaded = MapAssetLoader.loadFromTmx(
            shopType.getMapType().getName(),
            App.getCurrentGame().getCurrentTime().getSeason(),
            MapKind.SHOP
        );

        if (loaded.tiledMap == null) {
            Gdx.app.error("ShopMap", "Failed to load TMX for: " + shopType);
            return;
        }

        this.setVisual(new MapVisual(this, loaded.tiledMap));
        this.WIDTH = loaded.width;
        this.HEIGHT = loaded.height;
        this.startingPoint = loaded.startingPoint;
        this.doorPosition = loaded.exitPoint;
        this.tiles = loaded.tiles;

        initializeProducts();
    }

    public Point getDoorPosition() {
        return doorPosition;
    }

    public ShopType getShopType() {
        return shopType;
    }

    public List<ShopProduct> getProducts() {
        return products;
    }

    protected abstract void initializeProducts();

    public int getStartWork() {
        return startWork;
    }

    public int getEndWork() {
        return endWork;
    }

    public boolean isOpen() {
        return endWork > startWork;
    }
}
