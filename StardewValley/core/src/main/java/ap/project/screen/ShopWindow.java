package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.enums.shop_enums.*;
import ap.project.model.shops.ShopProduct;
import ap.project.model.game.Player;
import ap.project.model.shops.*;
import ap.project.model.App.GameAssetsManager;
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
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;
import java.util.List;

public class ShopWindow {
    private final Window window;
    private final Stage stage;
    private Shop shop;
    private final Table productsTable;
    private final ScrollPane scrollPane;
    private final SelectBox<String> filterSelectBox;
    private boolean isVisible = false;
    private final Skin skin;
    private final Player player;

    public ShopWindow(Stage stage, Shop shop) {
        this.stage = stage;
        this.shop = shop;
        this.player = App.getCurrentGame().getCurrentPlayer();
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        String windowTitle = (shop != null) ? shop.getType().getName() : "Shop";
        window = new Window(windowTitle, skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(5);

        // Create filter dropdown
        filterSelectBox = new SelectBox<>(skin);
        filterSelectBox.setItems("All Products", "Available Products");
        filterSelectBox.setSelected("All Products");
        filterSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                refreshProductsTable();
            }
        });

        Table headerTable = new Table(skin);
        headerTable.add(new Label("Filter:", skin)).padRight(10);
        headerTable.add(filterSelectBox).width(200);
        window.add(headerTable).row();

        // Create products table
        productsTable = new Table(skin);
        productsTable.defaults().pad(5);
        scrollPane = new ScrollPane(productsTable, skin);
        scrollPane.setFadeScrollBars(false);
        window.add(scrollPane).expand().fill().row();

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility();
            }
        });
        window.add(closeButton).right();

        window.pack();
        center();
        stage.addActor(window);
    }

    private List<ShopProduct> getShopProducts() {
        List<ShopProduct> products = new ArrayList<>();

        if (shop instanceof Blacksmith) {
            Blacksmith blacksmith = (Blacksmith) shop;
            for (BlacksmithStockItem item : BlacksmithStockItem.values()) {
                products.add(new ShopProduct(
                    item.getName(),
                    item.getPrice(),
                    -1, // Unlimited stock
                    item.getGameObjectType(),
                    item
                ));
            }
            for (BlacksmithUpgradeTools tool : BlacksmithUpgradeTools.values()) {
                products.add(new ShopProduct(
                    tool.getName(),
                    tool.getCost(),
                    tool.getDailyLimit(),
                    null, // Not a GameObject
                    tool
                ));
            }
        } else if (shop instanceof CarpentersShop) {
            CarpentersShop carpenters = (CarpentersShop) shop;
            for (CarpentersShopStock stock : CarpentersShopStock.values()) {
                products.add(new ShopProduct(
                    stock.getName(),
                    stock.getPrice(),
                    -1, // Unlimited stock
                    stock.getGameObjectType(),
                    stock
                ));
            }
            for (FarmBuildingType building : FarmBuildingType.values()) {
                products.add(new ShopProduct(
                    building.getName(),
                    building.getPrice(),
                    -1, // Unlimited stock
                    null, // Not a GameObject
                    building
                ));
            }
        } else if (shop instanceof FishShop) {
            FishShop fishShop = (FishShop) shop;
            for (FishShopStock stock : FishShopStock.values()) {
                products.add(new ShopProduct(
                    stock.getName(),
                    stock.getPrice(),
                    stock.getDailyLimit(),
                    stock.getGameObjectType(),
                    stock
                ));
            }
        } else if (shop instanceof JojaMart) {
            JojaMart jojaMart = (JojaMart) shop;
            for (JojaMartPermanentStock item : JojaMartPermanentStock.values()) {
                ShopProduct product = new ShopProduct(
                    item.getName(),
                    item.getPrice(),
                    item.getLimit(),
                    item.getGameObjectType(),
                    item
                );
                product.isSeasonal = false;
                products.add(product);
            }
            for (JojaMartSeasonalStock item : JojaMartSeasonalStock.values()) {
                ShopProduct product = new ShopProduct(
                    item.getName(),
                    item.getPrice(),
                    item.getLimit(),
                    item.getGameObjectType(),
                    item
                );
                product.isSeasonal = true;
                product.isAvailable = item.getSeason().equals(App.getCurrentGame().getCurrentTime().getSeason());
                products.add(product);
            }
        } else if (shop instanceof MarniesRanch) {
            MarniesRanch ranch = (MarniesRanch) shop;
            for (MarniesRanchShopInventory item : MarniesRanchShopInventory.values()) {
                products.add(new ShopProduct(
                    item.getName(),
                    item.getPrice(),
                    item.getLimit(),
                    item.getGameObjectType(),
                    item
                ));
            }
            for (FarmAnimalsType animal : FarmAnimalsType.values()) {
                products.add(new ShopProduct(
                    animal.getName(),
                    animal.getPurchaseCost(),
                    -1, // Unlimited stock
                    null, // Not a GameObject
                    animal
                ));
            }
        } else if (shop instanceof PierresGeneralStore) {
            PierresGeneralStore pierres = (PierresGeneralStore) shop;
            for (PierresGeneralStoreYearRoundStock stock : PierresGeneralStoreYearRoundStock.values()) {
                products.add(new ShopProduct(
                    stock.getDisplayName(),
                    stock.getPrice(),
                    stock.getDailyLimit(),
                    stock.getGameObjectType(),
                    stock
                ));
            }
            for (PierresGeneralStoreSeasonalStock stock : PierresGeneralStoreSeasonalStock.values()) {
                ShopProduct product = new ShopProduct(
                    stock.getName(),
                    stock.getBasePrice(),
                    stock.getDailyLimit(),
                    stock.getType(),
                    stock
                );
                product.isSeasonal = true;
                product.isAvailable = stock.getSeasons().contains(App.getCurrentGame().getCurrentTime().getSeason());
                products.add(product);
            }
            for (PierresGeneralStoreBackpacks backpack : PierresGeneralStoreBackpacks.values()) {
                products.add(new ShopProduct(
                    backpack.getName(),
                    backpack.getPrice(),
                    backpack.getDailyLimit(),
                    GameObjectType.BackPack,
                    backpack
                ));
            }
        } else if (shop instanceof TheStardropSaloon) {
            TheStardropSaloon saloon = (TheStardropSaloon) shop;
            for (TheStardropSaloonStock stock : TheStardropSaloonStock.values()) {
                products.add(new ShopProduct(
                    stock.getName(),
                    stock.getPrice(),
                    stock.getLimit(),
                    stock.getGameObjectType(),
                    stock
                ));
            }
        }

        return products;
    }

    private void refreshProductsTable() {
        productsTable.clear();
        boolean showAvailableOnly = "Available Products".equals(filterSelectBox.getSelected());

        List<ShopProduct> products = getShopProducts();
        for (ShopProduct product : products) {
            if (showAvailableOnly && !product.isAvailable) continue;

            Table productRow = new Table(skin);
            productRow.defaults().pad(5);

            // Product icon
            Drawable icon = getIconForProduct(product);
            ImageButton iconButton = new ImageButton(icon);
            iconButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    openPurchaseWindow(product);
                }
            });
            productRow.add(iconButton).size(50, 50);

            // Product info
            Table infoTable = new Table(skin);
            infoTable.defaults().pad(2);
            infoTable.add(new Label(product.getName(), skin)).left().row();

            // Price display
            String priceText = product.getPrice() + "g";
            if (product.isSeasonal) {
                priceText += " (Seasonal)";
            }
            infoTable.add(new Label(priceText, skin)).left().row();

            // Stock information
            if (product.getStock() == 0) {
                Label outOfStock = new Label("Out of Stock", skin);
                outOfStock.setColor(0.7f, 0.7f, 0.7f, 1f);
                infoTable.add(outOfStock).left();
            } else if (product.getStock() > 0) {
                infoTable.add(new Label("In Stock: " + product.getStock(), skin)).left();
            }

            productRow.add(infoTable).width(200).left().expandX();

            // Add to products table
            productsTable.add(productRow).fillX().expandX().row();

            // Dim unavailable products
            if (!product.isAvailable) {
                productRow.setColor(0.5f, 0.5f, 0.5f, 1f);
            }
        }
    }

    private Drawable getIconForProduct(ShopProduct product) {
        if (product.getGameObjectType() != null) {
            return getIconForGameObjectType(product.getGameObjectType());
        }

        // Handle special cases
        if (product.getOriginalItem() instanceof FarmBuildingType) {
            return new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("buildings/" + product.getName() + ".png"))
            ));
        }
        else if (product.getOriginalItem() instanceof FarmAnimalsType) {
            return new TextureRegionDrawable(new TextureRegion(
                new Texture(Gdx.files.internal("animals/" + product.getName() + ".png"))
            ));
        }

        // Default icon
        return new TextureRegionDrawable(new TextureRegion(
            new Texture(Gdx.files.internal("game_objects/unknown.png"))
        ));
    }

    private Drawable getIconForGameObjectType(GameObjectType type) {
        String path = type.getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    private void openPurchaseWindow(ShopProduct product) {
        if (!product.isAvailable) return;

        PurchaseWindow purchaseWindow = new PurchaseWindow(stage, shop, product);
        purchaseWindow.toggleVisibility();
    }

    private void center() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible) {
            refreshProductsTable();
            window.toFront();
        }
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        if (shop != null) {
            window.getTitleLabel().setText(shop.getType().getName());
        }
    }

}
