package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.animal.Animal;
import ap.project.model.animal.AnimalBuilding;
import ap.project.model.enums.GameObjectType;
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

    public PurchaseWindow(Stage stage) {
        super("Purchase", GameAssetsManager.getGameAssetsManager().getSkin());
        this.stage = stage;

        setModal(true);
        setMovable(true);
        setResizable(true);
        setSize(400, 300);
        hide(); // Start hidden

        buyButton = new TextButton("Buy", getSkin());
        cancelButton = new TextButton("Cancel", getSkin());

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
    }

    public void setProduct(ShopProduct product) {
        this.product = product;
        this.quantity = 1;
        clearWindow();
        updateWindow();
        show(stage);
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

        currentTexture = new Texture(Gdx.files.internal(product.getGameObjectType().getPath()));
        icon = new Image(new TextureRegionDrawable(currentTexture));


        // Product info
        Table contentTable = new Table(getSkin());
        contentTable.defaults().pad(10);

        // Product icon using Texture
        Texture texture = new Texture(Gdx.files.internal(product.getGameObjectType().getPath()));
        icon = new Image(new TextureRegionDrawable(texture));
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

    private void updateQuantity() {
        quantityLabel.setText(String.valueOf(quantity));
        totalPriceLabel.setText("Total: " + (quantity * product.getPrice()) + "g");
    }

    private void purchase() {
        int totalCost = quantity * product.getPrice();
        Player player = App.getCurrentGame().getCurrentPlayer();

        if (player.getMoney() < totalCost) {
            UIRenderer.showTextBox("Not enough money!");
            return;
        }

        if (product.getOriginalItem() instanceof FarmBuildingType) {
            handleBuildingPurchase((FarmBuildingType) product.getOriginalItem());
        }
        else if (product.getOriginalItem() instanceof FarmAnimalsType) {
            handleAnimalPurchase((FarmAnimalsType) product.getOriginalItem());
        }
        else {
            GameObject purchasedItem = new GameObject(product.getGameObjectType(), quantity);
            player.addToInventory(purchasedItem);
        }

        player.increaseMoney(-totalCost);

        if (product.getStock() > 0) {
            product.setStock(product.getStock() - quantity);
        }

        UIRenderer.showTextBox("Purchased " + quantity + " " + product.getName());

        if(product.getOriginalItem() instanceof FarmBuildingType) {
            System.out.println("salam");
            handleBuildingPurchase((FarmBuildingType) product.getOriginalItem());
        }
    }

    private void handleBuildingPurchase(FarmBuildingType building) {
        WorldScreen worldScreen = WorldScreen.getInstance();
        worldScreen.startBuilding(building);
        hide();
    }

    private void handleAnimalPurchase(FarmAnimalsType animalType) {
        Player player = App.getCurrentGame().getCurrentPlayer();
        Farm farm = player.getFarm();

        for (int i = 0; i < quantity; i++) {
            Animal animal = new Animal("test", animalType);


            // Add animal to animal manager for rendering
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
}
