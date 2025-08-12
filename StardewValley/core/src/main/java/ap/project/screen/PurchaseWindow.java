package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.animal.Animal;
import ap.project.model.enums.animal_enums.FarmAnimalsType;
import ap.project.model.enums.animal_enums.FarmBuildingType;
import ap.project.model.game.Farm;
import ap.project.model.game.GameObject;
import ap.project.model.game.Player;
import ap.project.model.shops.ShopProduct;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PurchaseWindow extends Dialog {
    private final Stage stage;
    private ShopProduct product;
    private int quantity = 1;
    private Label quantityLabel;
    private Label totalPriceLabel;
    private Image icon;
    private Texture currentTexture;
    private TextButton buyButton;
    private TextButton cancelButton;
    private AnimalManager animalManager;
    private TextButton minusButton;
    private TextButton plusButton;
    private boolean isVisible = false;

    public PurchaseWindow(Stage stage) {
        super("Purchase", GameAssetsManager.getGameAssetsManager().getSkin());
        this.stage = stage;
        stage.addActor(this);

        setModal(false);
        setMovable(true);
        setResizable(true);
        setSize(400, 300);
        setVisible(false); // Start hidden

        // Create buttons once
        buyButton = new TextButton("Buy", getSkin());
        cancelButton = new TextButton("Cancel", getSkin());
        minusButton = new TextButton("-", getSkin());
        plusButton = new TextButton("+", getSkin());
        quantityLabel = new Label("1", getSkin());
        totalPriceLabel = new Label("Total: 0g", getSkin());

        // Setup listeners once
        setupListeners();

        createQuantityControls();
    }

    private void setupListeners() {
        // Buy button listener
        buyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                purchase();
                hide();
            }
        });

        // Cancel button listener
        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        // Minus button listener
        minusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (quantity > 1) {
                    quantity--;
                    updateQuantity();
                }
            }
        });

        // Plus button listener
        plusButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (product == null || product.getStock() == -1 || quantity < product.getStock()) {
                    quantity++;
                    updateQuantity();
                }
            }
        });
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
        this.quantity = 1;
        clearWindow();
        updateWindow();
        toggleVisibility();
        centerWindow();
    }

    private void clearWindow() {
        if (currentTexture != null) {
            currentTexture.dispose();
            currentTexture = null;
        }

        getContentTable().clear();
        getButtonTable().clear();
        clearListeners();
    }

    private void updateWindow() {
        getContentTable().clear();
        getButtonTable().clear();

        if (product == null) return;

        currentTexture = new Texture(Gdx.files.internal(product.getGameObjectType().getPath()));
        icon = new Image(new TextureRegionDrawable(currentTexture));

        // Product info
        Table contentTable = new Table(getSkin());
        contentTable.defaults().pad(10);

        contentTable.add(icon).size(80, 80).padRight(20);

        Table detailsTable = new Table(getSkin());
        detailsTable.add(new Label(product.getName(), getSkin())).left().row();
        detailsTable.add(new Label("Price: " + product.getPrice() + "g", getSkin())).left().row();

        // Quantity controls
        Table quantityTable = new Table(getSkin());
        quantityTable.add(minusButton);
        quantityTable.add(quantityLabel).pad(0, 15, 0, 15);
        quantityTable.add(plusButton);

        detailsTable.add(quantityTable).padTop(15).row();

        // Update labels
        quantityLabel.setText("1");
        totalPriceLabel.setText("Total: " + (quantity * product.getPrice()) + "g");
        detailsTable.add(totalPriceLabel).padTop(10);

        contentTable.add(detailsTable);
        getContentTable().add(contentTable).pad(20);

        // Buttons
        button(buyButton);
        button(cancelButton);
        pack();
    }

    private void updateQuantity() {
        if (product == null) return;
        quantityLabel.setText(String.valueOf(quantity));
        totalPriceLabel.setText("Total: " + (quantity * product.getPrice()) + "g");
    }

    private void purchase() {
        if (product == null || buyButton.isDisabled()) return;

        buyButton.setDisabled(true);

        try {
            int totalCost = quantity * product.getPrice();
            Player player = App.getCurrentGame().getCurrentPlayer();

            if (player.getMoney() < totalCost) {
                UIRenderer.showTextBox("Not enough money!");
                return;
            }

            if (product.getOriginalItem() instanceof FarmBuildingType) {
                handleBuildingPurchase((FarmBuildingType) product.getOriginalItem());
            } else if (product.getOriginalItem() instanceof FarmAnimalsType) {
                handleAnimalPurchase((FarmAnimalsType) product.getOriginalItem());
            } else {
                GameObject purchasedItem = new GameObject(product.getGameObjectType(), quantity);
                player.addToInventory(purchasedItem);
            }

            player.increaseMoney(-totalCost);

            if (product.getStock() > 0) {
                product.setStock(product.getStock() - quantity);
            }

            UIRenderer.showTextBox("Purchased " + quantity + " " + product.getName());
        } finally {
            buyButton.setDisabled(false);
        }
    }

    private void handleBuildingPurchase(FarmBuildingType building) {
        WorldScreen worldScreen = WorldScreen.getInstance();
        hide();
    }

    private void handleAnimalPurchase(FarmAnimalsType animalType) {
        Player player = App.getCurrentGame().getCurrentPlayer();

        for (int i = 0; i < quantity; i++) {
            Animal animal = new Animal("test", animalType);

            if (animalManager != null) {
                animalManager.addAnimal(animal);
            }
        }
    }

    public void centerWindow() {
        setPosition(
            (stage.getViewport().getWorldWidth() - getWidth()) / 2,
            (stage.getViewport().getWorldHeight() - getHeight()) / 2
        );
    }

    public void dispose() {
        if (currentTexture != null) {
            currentTexture.dispose();
            currentTexture = null;
        }
    }

    public void setAnimalManager(AnimalManager animalManager) {
        this.animalManager = animalManager;
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        setVisible(isVisible);
        if (isVisible) {
            toFront();
        }
    }

    @Override
    public void hide() {
        super.hide();
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }

    private void createQuantityControls() {
        // Already created in constructor
    }

    public void clearProduct() {
        this.product = null;
        this.quantity = 1;
    }
}
