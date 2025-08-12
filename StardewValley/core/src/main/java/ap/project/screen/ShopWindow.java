package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.enums.GameObjectType;
import ap.project.model.shops.Shop;
import ap.project.model.shops.ShopProduct;
import ap.project.visual.UIRenderer;
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

import javax.swing.*;
import java.util.List;

public class ShopWindow extends Window
{
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
        boolean showAvailableOnly = (filterSelectBox.getSelected()).equals("Available Products");

        if (shop == null) return;

        List<ShopProduct> products;
        if (showAvailableOnly) {
            products = shop.getAvailableProducts();
        } else {
            products = shop.getAllProducts();
        }

        for (ShopProduct product : products) {
            boolean isAvailable = isProductAvailable(product);

            // Create product row
            Table productRow = new Table(skin);
            productRow.defaults().pad(5);
            productRow.setBackground(skin.getDrawable("background"));

            // Product icon
            ImageButton iconButton = new ImageButton(getProductIcon(product));
            iconButton.setDisabled(!isAvailable); // Disable if not available

            if (isAvailable) {
                // Only add purchase listener for available products
                iconButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        purchaseWindow = new PurchaseWindow(stage, product);
                        purchaseWindow.toggleVisibility();
                        purchaseWindow = null;
                    }
                });
            } else {
                // Show error message when clicking unavailable product
                iconButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        String reason = getUnavailabilityReason(product);
                        UIRenderer.showTextBox("Cannot purchase: " + reason);
                    }
                });
            }

            productRow.add(iconButton).size(64, 64).padRight(10);

            // Product info
            Table infoTable = new Table(skin);
            infoTable.defaults().pad(2).left();

            // Name and price
            infoTable.add(new Label(product.getName(), skin)).left().row();
            infoTable.add(new Label(product.getPrice() + "g", skin)).left().row();

            // Stock status
            if (product.getStock() == 0) {
                Label outOfStock = new Label("Out of Stock", skin);
                outOfStock.setColor(0.7f, 0.7f, 0.7f, 1f);
                infoTable.add(outOfStock).left().row();
            } else if (product.getStock() > 0) {
                infoTable.add(new Label("In Stock: " + product.getStock(), skin)).left().row();
            } else {
                infoTable.add(new Label("Unlimited Stock", skin)).left().row();
            }

            // Availability status indicators
            if (!product.isAvailable()) {
                Label seasonal = new Label("Not in Season", skin);
                infoTable.add(seasonal).left().row();
            }

            if (!shop.isOpen(App.getCurrentGame().getCurrentTime())) {
                Label closed = new Label("Shop Closed", skin);
                infoTable.add(closed).left().row();
            }

            productRow.add(infoTable).width(300).left().expandX();

            // Add "Not Available" label for unavailable products
            if (!isAvailable) {
                Label notAvailableLabel = new Label("Not Available", skin);
                notAvailableLabel.setAlignment(Align.right);
                productRow.add(notAvailableLabel).padLeft(20).right().expandX();
            }

            // Apply visual styling for unavailable products
            if (!isAvailable) {
                // Darker background with more transparency
                productRow.setColor(0.5f, 0.5f, 0.5f, 0.6f);
                iconButton.setColor(0.5f, 0.5f, 0.5f, 0.6f);
            }

            productsTable.add(productRow).fillX().expandX().pad(5).row();
            Table separator = new Table();
            separator.setBackground(skin.getDrawable("background"));
            productsTable.add(separator).height(2).fillX().padTop(5).padBottom(5).growX().row();
        }
    }

    private boolean isProductAvailable(ShopProduct product) {
        // Check if shop is open
        if (!shop.isOpen(App.getCurrentGame().getCurrentTime())) {
            return false;
        }

        // Check if product is in season
        if (!product.isAvailable()) {
            return false;
        }

        // Check stock
        if (product.getStock() == 0) {
            return false;
        }

        return true;
    }

    // Get reason for product unavailability
    private String getUnavailabilityReason(ShopProduct product) {
        if (!shop.isOpen(App.getCurrentGame().getCurrentTime())) {
            return "Shop is currently closed";
        }

        if (!product.isAvailable()) {
            return "Product is out of season";
        }

        if (product.getStock() == 0) {
            return "Product is out of stock";
        }

        return "Product is unavailable";
    }

    private Drawable getProductIcon(ShopProduct product) {
        if (product.getGameObjectType() != null) {
            return getIconForGameObject(product.getGameObjectType());
        }
        // Default icon if no specific type
        return new TextureRegionDrawable(new TextureRegion(
            new Texture(Gdx.files.internal("game_objects/Rice_Shoot.png"))
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
            toFront();
            refreshProductsTable();
        } else {
            this.shop = null;
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

    @Override
    public boolean remove() {
        // Clean up purchase window when shop window is removed
        if (purchaseWindow != null) {
            purchaseWindow.dispose();
        }
        return super.remove();
    }


    public PurchaseWindow getPurchaseWindow() {
        return purchaseWindow;
    }

    public boolean isPurchaseWindowVisible()
    {
        if (purchaseWindow != null)
        {
            return purchaseWindow.isVisible();
        }
        return false;
    }
}
