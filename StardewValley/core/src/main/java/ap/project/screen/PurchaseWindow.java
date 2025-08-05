package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.model.shops.ShopMap;
import ap.project.model.shops.ShopProduct;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PurchaseWindow extends Dialog {
    private final Stage stage;
    private final ShopMap shopMap;
    private final ShopProduct product;
    private int quantity = 1;
    private Label quantityLabel;
    private Label totalPriceLabel;

    public PurchaseWindow(Stage stage, ShopMap shopMap, ShopProduct product) {
        super("Purchase " + product.getName(),
            GameAssetsManager.getGameAssetsManager().getSkin());
        this.stage = stage;
        this.shopMap = shopMap;
        this.product = product;

        setModal(true);
        setMovable(true);
        setResizable(true);
        setSize(400, 300);

        // Product info
        Table contentTable = new Table(getSkin());
        contentTable.defaults().pad(10);

        // Product icon and details
        Image icon = new Image(getIconForGameObject(product.getGameObjectType()));
        contentTable.add(icon).size(80, 80).padRight(20);

        Table detailsTable = new Table(getSkin());
        detailsTable.add(new Label(product.getName(), getSkin())).left().row();
        detailsTable.add(new Label("Price: " + product.getPrice() + "g", getSkin())).left().row();

        // Quantity controls
        Table quantityTable = new Table(getSkin());
        TextButton minusButton = new TextButton("-", getSkin());
        TextButton plusButton = new TextButton("+", getSkin());
        quantityLabel = new Label(String.valueOf(quantity), getSkin());

        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (quantity > 1) {
                    quantity--;
                    updateQuantity();
                }
            }
        });

        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (product.getStock() == -1 || quantity < product.getStock()) {
                    quantity++;
                    updateQuantity();
                }
            }
        });

        quantityTable.add(minusButton);
        quantityTable.add(quantityLabel).pad(0, 15, 0, 15);
        quantityTable.add(plusButton);

        detailsTable.add(quantityTable).padTop(15).row();

        // Total price
        totalPriceLabel = new Label("Total: " + (quantity * product.getPrice()) + "g", getSkin());
        detailsTable.add(totalPriceLabel).padTop(10);

        contentTable.add(detailsTable);
        getContentTable().add(contentTable).pad(20);

        // Buttons
        TextButton buyButton = new TextButton("Buy", getSkin());
        TextButton cancelButton = new TextButton("Cancel", getSkin());

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                purchase();
                hide();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        button(buyButton);
        button(cancelButton);
        pack();
    }

    private Drawable getIconForGameObject(GameObjectType type) {
        String path = type.getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    private void updateQuantity() {
        quantityLabel.setText(String.valueOf(quantity));
        totalPriceLabel.setText("Total: " + (quantity * product.getPrice()) + "g");
    }

    private void purchase() {
        int totalCost = quantity * product.getPrice();
        Player player = App.getCurrentGame().getCurrentPlayer();

        // Check if player has enough money
        if (player.getMoney() < totalCost) {
            UIRenderer.showTextBox("Not enough money!");
            return;
        }

        // Handle different product types
        if (product.getOriginalItem() instanceof FarmBuildingType) {
            handleBuildingPurchase((FarmBuildingType) product.getOriginalItem());
        }
        else if (product.getOriginalItem() instanceof FarmAnimalsType) {
            handleAnimalPurchase((FarmAnimalsType) product.getOriginalItem());
        }
        else {
            // Default GameObject purchase
            GameObject purchasedItem = new GameObject(product.getGameObjectType(), quantity);
            player.addToInventory(purchasedItem);
        }

        // Deduct money
        player.increaseMoney(-totalCost);

        // Update stock if limited
        if (product.getStock() > 0) {
            product.setStock(product.getStock() - quantity);
        }

        UIRenderer.showTextBox("Purchased " + quantity + " " + product.getName());
    }

    private void handleBuildingPurchase(FarmBuildingType building) {
        // Implementation for building purchases
    }

    private void handleAnimalPurchase(FarmAnimalsType animalType) {
        // Implementation for animal purchases
    }

    public void toggleVisibility() {
        if (isVisible()) {
            hide();
        } else {
            show(stage);
            centerWindow();
        }
    }

    private void centerWindow() {
        setPosition(
            (stage.getViewport().getWorldWidth() - getWidth()) / 2,
            (stage.getViewport().getWorldHeight() - getHeight()) / 2
        );
    }
}
