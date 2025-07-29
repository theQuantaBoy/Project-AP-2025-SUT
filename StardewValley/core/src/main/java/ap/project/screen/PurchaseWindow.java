package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.animal.AnimalBuilding;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.enums.shop_enums.BlacksmithUpgradeTools;
import ap.project.model.enums.shop_enums.PierresGeneralStoreBackpacks;
import ap.project.model.game.Farm;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.model.game.Point;
import ap.project.model.shops.Blacksmith;
import ap.project.model.shops.Shop;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.shops.ShopProduct;
import ap.project.model.tools.Axe;
import ap.project.model.tools.Hoe;
import ap.project.model.tools.Pickaxe;
import ap.project.model.tools.Tool;
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

import static ap.project.model.enums.GameObjectType.AXE;
import static ap.project.model.enums.GameObjectType.PICKAXE;

public class PurchaseWindow {
    private final Dialog dialog;
    private final Stage stage;
    private final Shop shop;
    private final ShopProduct product;
    private int quantity = 1;
    private Label quantityLabel;
    private Label totalPriceLabel;

    public PurchaseWindow(Stage stage, Shop shop, ShopProduct product) {
        this.stage = stage;
        this.shop = shop;
        this.product = product;
        Skin skin = GameAssetsManager.getGameAssetsManager().getSkin();

        dialog = new Dialog("Purchase " + product.getName(), skin);
        dialog.setModal(true);
        dialog.setMovable(true);

        // Product info
        Table contentTable = new Table(skin);
        contentTable.defaults().pad(10);

        // Product icon
        Image icon = new Image(getIconForGameObject(product.getGameObjectType()));
        contentTable.add(icon).size(64, 64).padRight(15);

        // Product details
        Table detailsTable = new Table(skin);
        detailsTable.defaults().pad(5);
        detailsTable.add(new Label(product.getName(), skin)).left().row();
        detailsTable.add(new Label("Price: " + product.getPrice() + "g", skin)).left().row();

        // Quantity controls
        Table quantityTable = new Table(skin);
        TextButton minusButton = new TextButton("-", skin);
        TextButton plusButton = new TextButton("+", skin);
        quantityLabel = new Label(String.valueOf(quantity), skin);

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
        quantityTable.add(quantityLabel).pad(0, 10, 0, 10);
        quantityTable.add(plusButton);

        detailsTable.add(quantityTable).padTop(10).row();

        // Total price
        totalPriceLabel = new Label("Total: " + (quantity * product.getPrice()) + "g", skin);
        detailsTable.add(totalPriceLabel).padTop(5);

        contentTable.add(detailsTable);
        dialog.getContentTable().add(contentTable).pad(15);

        // Buttons
        TextButton buyButton = new TextButton("Buy", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                purchase();
                dialog.hide();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.button(buyButton);
        dialog.button(cancelButton);
        dialog.pack();
    }

    private void updateQuantity() {
        quantityLabel.setText(String.valueOf(quantity));
        totalPriceLabel.setText("Total: " + (quantity * product.getPrice()) + "g");
        dialog.pack();
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
        if (product.getOriginalItem() instanceof BlacksmithUpgradeTools) {
            handleToolUpgrade((BlacksmithUpgradeTools) product.getOriginalItem());
        }
        else if (product.getOriginalItem() instanceof FarmBuildingType) {
            handleBuildingPurchase((FarmBuildingType) product.getOriginalItem());
        }
        else if (product.getOriginalItem() instanceof FarmAnimalsType) {
            handleAnimalPurchase((FarmAnimalsType) product.getOriginalItem());
        }
        else if (product.getOriginalItem() instanceof PierresGeneralStoreBackpacks) {
            handleBackpackUpgrade((PierresGeneralStoreBackpacks) product.getOriginalItem());
        }
        else {
            // Default GameObject purchase
            GameObject purchasedItem = new GameObject(product.getGameObjectType(), quantity);
            shop.purchase(purchasedItem);
        }

        // Deduct money
        player.increaseMoney(-totalCost);

        // Update stock
        if (product.getStock() > 0) {
            product.setStock(Math.max(0, product.getStock() - quantity));
        }

        UIRenderer.showTextBox("Purchased " + quantity + " " + product.getName());
    }

    private void handleToolUpgrade(BlacksmithUpgradeTools upgrade) {
        Player player = App.getCurrentGame().getCurrentPlayer();
        Tool currentTool = player.getCurrentTool();

        if (currentTool == null) {
            UIRenderer.showTextBox("No tool selected for upgrade!");
            return;
        }

        // Check if upgrade is applicable to current tool
        switch (currentTool.getToolType()) {
            case Axe:
                if (upgrade.name().contains("AXE")) {
                    ((Blacksmith) shop).upgrade(currentTool);
                }
                break;
            case Pickaxe:
                if (upgrade.name().contains("PICKAXE")) {
                    ((Blacksmith) shop).upgrade(currentTool);
                }
                break;
            case Hoe:
                if (upgrade.name().contains("HOE")) {
                    ((Blacksmith) shop).upgrade(currentTool);
                }
                break;
            case WateringCan:
                if (upgrade.name().contains("WATERINGCAN")) {
                    ((Blacksmith) shop).upgrade(currentTool);
                }
                break;
        }
    }

    private void handleBuildingPurchase(FarmBuildingType building) {
        Player player = App.getCurrentGame().getCurrentPlayer();
        Farm farm = player.getFarm();

        // Find suitable location for building
        Point location = farm.findSuitableLocation(building.getSize());

        if (location == null) {
            UIRenderer.showTextBox("No suitable location for this building!");
            return;
        }

        // Construct building
        farm.constructBuilding(building, location);
        UIRenderer.showTextBox(building.getName() + " constructed at (" +
            location.getX() + ", " + location.getY() + ")");
    }

    private void handleAnimalPurchase(FarmAnimalsType animalType) {
        Player player = App.getCurrentGame().getCurrentPlayer();

        // Check for available barn/coop
        AnimalBuilding building = player.findAvailableBuilding(animalType);

        if (building == null) {
            UIRenderer.showTextBox("No available " + animalType.getBuilding() + " for this animal!");
            return;
        }

        // Add animal to building
        building.addAnimal(animalType);
        UIRenderer.showTextBox(animalType.getName() + " added to your " + building.getFarmBuildingType());
    }

    private void handleBackpackUpgrade(PierresGeneralStoreBackpacks backpack) {
        Player player = App.getCurrentGame().getCurrentPlayer();
        player.getCurrentBackPack().setLevel(backpack.getLevel());
        UIRenderer.showTextBox("Backpack upgraded to " + backpack.getName());
    }

    private Drawable getIconForGameObject(GameObjectType type) {
        String path = type.getPath();
        return new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(path))));
    }

    public void toggleVisibility() {
        if (!dialog.isVisible()) {
            dialog.show(stage);
            center();
        }
    }

    private void center() {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        dialog.setPosition((w - dialog.getWidth()) / 2f, (h - dialog.getHeight()) / 2f);
    }
}
