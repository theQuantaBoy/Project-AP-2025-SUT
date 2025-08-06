package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.enums.GameObjectType;
import ap.project.model.shops.Shop;
import ap.project.model.shops.ShopMap;
import ap.project.model.shops.ShopProduct;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.io.PushbackInputStream;
import java.util.List;

import static org.lwjgl.opengl.Display.setTitle;

public class ShopWindow extends Window {
    private final Stage stage;
    private Shop shop;
    private Table productsTable;
    private final ScrollPane scrollPane;
    private final SelectBox<String> filterSelectBox;
    private boolean isVisible = false;
    private final Skin skin;
    private PurchaseWindow purchaseWindow;

    public ShopWindow(Stage stage, Shop shop) {
        super("", GameAssetsManager.getGameAssetsManager().getSkin());
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();
        this.purchaseWindow = new PurchaseWindow(stage);

        // Window setup
        setSize(800, 600);
        setMovable(true);
        setResizable(true);
        setModal(true);

        // Filter dropdown
        filterSelectBox = new SelectBox<>(skin);
        filterSelectBox.setItems("All Products", "Available Products");
        filterSelectBox.setSelected("All Products");
        filterSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshProductsTable();
            }
        });

        // Header with filter
        Table headerTable = new Table(skin);
        headerTable.add(new Label("Filter:", skin)).padRight(10);
        headerTable.add(filterSelectBox).width(200).padBottom(15);
        add(headerTable).colspan(2).row();

        // Products table with scroll
        productsTable = new Table(skin);
        productsTable.defaults().pad(5);
        scrollPane = new ScrollPane(productsTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollbarsVisible(true);
        add(scrollPane).expand().fill().colspan(2).row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility();
            }
        });
        add(closeButton).right().padTop(10);

        setShop(shop);
        centerWindow();
        stage.addActor(this);
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        refreshProductsTable();
    }

    private void refreshProductsTable() {
        productsTable.clear();
        boolean showAvailableOnly = "Available Products".equals(filterSelectBox.getSelected());

        if (shop == null) return;

        List<ShopProduct> products = shop.getProducts();
        for (ShopProduct product : products) {
            if (showAvailableOnly && !product.isAvailable()) continue;

            // Create product row
            Table productRow = new Table(skin);
            productRow.defaults().pad(5);

            // Product icon
            ImageButton iconButton = new ImageButton(getProductIcon(product));
            iconButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    purchaseWindow.setProduct(product); // Update the existing window
                    purchaseWindow.show(stage); // Show the existing window
                    purchaseWindow.centerWindow();
                }
            });
            productRow.add(iconButton).size(64, 64);

            // Product info
            Table infoTable = new Table(skin);
            infoTable.defaults().pad(2);

            // Name and price
            infoTable.add(new Label(product.getName(), skin)).left().row();
            infoTable.add(new Label(product.getPrice() + "g", skin)).left().row();

            // Stock status
            if (product.getStock() == 0) {
                Label outOfStock = new Label("Out of Stock", skin);
                outOfStock.setColor(0.7f, 0.7f, 0.7f, 1f);
                infoTable.add(outOfStock).left();
            } else if (product.getStock() > 0) {
                infoTable.add(new Label("In Stock: " + product.getStock(), skin)).left();
            }

            productRow.add(infoTable).width(250).left().expandX();

            // Dim if unavailable
            if (!product.isAvailable() || product.getStock() == 0) {
                productRow.setColor(0.5f, 0.5f, 0.5f, 1f);
            }

            productsTable.add(productRow).fillX().expandX().row();
        }
    }

    private Drawable getProductIcon(ShopProduct product) {
        if (product.getGameObjectType() != null) {
            return getIconForGameObject(product.getGameObjectType());
        }
        // Default icon if no specific type
        return new TextureRegionDrawable(new TextureRegion(
            new Texture(Gdx.files.internal("game_objects/unknown.png"))
        ));
    }

    private Drawable getIconForGameObject(GameObjectType type) {
        String path = type.getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        setVisible(isVisible);
        if (isVisible) {
            System.out.println("yes");
            toFront();
            refreshProductsTable();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    private void centerWindow() {
        float x = (stage.getViewport().getWorldWidth() - getWidth()) / 2;
        float y = (stage.getViewport().getWorldHeight() - getHeight()) / 2;
        setPosition(x, y);
    }
}
