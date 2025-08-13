package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.App.GameAssetsManager;
import ap.project.model.animal.Animal;
import ap.project.model.enums.GameAnimationType;
import ap.project.model.enums.GameObjectType;
import ap.project.model.game.GameObject;
import ap.project.model.game.NPC;
import ap.project.model.game.Player;
import ap.project.model.player_data.FriendshipWithNpcData;
import ap.project.visual.MapVisual;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.sun.source.doctree.UnknownInlineTagTree;

public class AnimalWindow
{
    private final Window window;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;
    private Animal currentAnimal;

    public AnimalWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        window = new Window("Animal Manager", skin);
        window.setVisible(false);
        window.setMovable(true);
        window.defaults().pad(10);

        stage.addActor(window);
    }

    public void setAnimal(Animal animal)
    {
        this.currentAnimal = animal;
        refresh();
    }

    public void toggleVisibility()
    {
        isVisible = !isVisible;
        window.setVisible(isVisible);
        if (isVisible)
        {
            refresh();
            center();
        }
    }

    private void refresh()
    {
        window.clear();
        if (currentAnimal == null) return;

        Table content = new Table();
        content.add(new Label("pet: " + currentAnimal.getName(), skin)).padBottom(20).row();

        Table buttonTable = new Table();
        buttonTable.defaults().minWidth(120).pad(10);

        TextButton informationButton = new TextButton("Information", skin);
        informationButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showInformationDialog();
            }
        });

        TextButton productsButton = new TextButton("Products", skin);
        productsButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                showProductsDialog();
            }
        });

        TextButton sellButton = new TextButton("Sell", skin);
        sellButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                int price = currentAnimal.getPrice();
                Player player = App.getCurrentGame().getCurrentPlayer();
                player.setMoney(player.getMoney() + price);
                player.removeAnimal(currentAnimal);
                UIRenderer.showTextBox("you just earned " + price + " you cruel");
                toggleVisibility();
            }
        });

        // Close Button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                toggleVisibility();
            }
        });

        buttonTable.add(informationButton).padRight(20);
        buttonTable.add(productsButton).padRight(20);
        buttonTable.add(sellButton).padRight(20);
        buttonTable.row();
        buttonTable.add(closeButton).colspan(3).padTop(20);

        content.add(buttonTable);
        window.add(content);
        window.pack();
    }

    private void showInformationDialog()
    {
        Dialog informationDialog = new Dialog("Pet Information", skin);
        informationDialog.setModal(true);
        informationDialog.setMovable(true);
        informationDialog.setResizable(true);

        String friendText = currentAnimal.getInfo();

        ScrollPane scrollPane = new ScrollPane(new Label(friendText, skin));
        scrollPane.setFadeScrollBars(false);
        informationDialog.getContentTable().add(scrollPane).width(500).height(350).pad(20);

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y)
            {
                informationDialog.hide();
            }
        });
        informationDialog.getButtonTable().add(closeButton);

        informationDialog.show(stage);
        informationDialog.setSize(800, 550);
        informationDialog.setPosition(
                (stage.getWidth() - informationDialog.getWidth()) / 2,
                (stage.getHeight() - informationDialog.getHeight()) / 2
        );
    }

    private void showProductsDialog()
    {
        Dialog productsDialog = new Dialog("Products", skin);
        productsDialog.setModal(true);
        productsDialog.setMovable(true);
        productsDialog.setResizable(true);

        Player player = App.getCurrentGame().getCurrentPlayer();

        // Create scrollable content
        Table contentTable = new Table();
        contentTable.pad(10);
        contentTable.defaults().pad(5).left();

        // Title row
        contentTable.add(new Label(currentAnimal.getName() + "'s Products:", skin))
                .colspan(2).padBottom(15).row();
        productsDialog.add(contentTable).expand().pad(10);

        // Create quest row
        Table questRow = new Table();

        if (currentAnimal.hasProduct())
        {
            questRow.defaults().pad(5);

            GameObject product = currentAnimal.getProduct();

            // Quest description
            String description = product.getObjectType().toString() + " x" + product.getNumber();
            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setWrap(true);
            questRow.add(descriptionLabel).width(600).left();

            // Complete button
            TextButton getButton = new TextButton("Receive", skin);
            getButton.addListener(new ClickListener()
            {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    if (player.inventoryHasCapacity())
                    {
                        player.addToInventory(product);
                        currentAnimal.setHasProduct(false);

                        UIRenderer.showTextBox(product.getObjectType().toString() + " was added to your inventory!");

                        productsDialog.hide();
                        toggleVisibility();
                    } else
                    {
                        UIRenderer.showTextBox("you have no free space in your inventory!");
                    }
                }
            });
            questRow.add(getButton).padLeft(20);
        } else
        {
            questRow.defaults().pad(5);

            // Quest description
            String description = "currently no products!";
            Label descriptionLabel = new Label(description, skin);
            descriptionLabel.setWrap(true);
            questRow.add(descriptionLabel).width(600).left();
        }

        contentTable.add(questRow).colspan(2).padBottom(15).row();

        contentTable.setSize(1000, 800);

        // Close button
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                productsDialog.hide();
            }
        });
        productsDialog.getButtonTable().add(closeButton).padTop(15);

        productsDialog.show(stage);
        productsDialog.pack(); // Auto-size the dialog

        // Center dialog
        productsDialog.setPosition(
                (stage.getWidth() - productsDialog.getWidth()) / 2,
                (stage.getHeight() - productsDialog.getHeight()) / 2
        );
    }

    private void center()
    {
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        window.setPosition((w - window.getWidth()) / 2f, (h - window.getHeight()) / 2f);
    }

    public boolean isVisible()
    {
        return isVisible;
    }

    public Window getWindow()
    {
        return window;
    }
}
