package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.ReactionEmoji;
import ap.project.model.App.GameAssetsManager;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReactionWindow
{
    private final Window mainWindow;
    private final Window moreWindow;
    private final Stage stage;
    private final Skin skin;
    private boolean isVisible = false;

    // Reaction data
    private static final List<ReactionEmoji> ALL_EMOJIS = Arrays.asList(ReactionEmoji.values());
    private static final List<String> ALL_TEXTS = Arrays.asList(
        "Hi!", "Bye!", "OMG!", "LOL", "GG", "BRB", "SLAYING", "SO JULIA!","Ap Panic!!", "JUST A THEORY"
    );

    private final List<ReactionEmoji> defaultEmojis = new ArrayList<>();
    private final List<String> defaultTexts = new ArrayList<>();
    private int nextEmojiReplaceIndex = 0;
    private int nextTextReplaceIndex = 0;

    public ReactionWindow(Stage stage)
    {
        this.stage = stage;
        this.skin = GameAssetsManager.getGameAssetsManager().getSkin();

        // Initialize defaults
        defaultEmojis.add(ReactionEmoji.SMILE_0);
        defaultEmojis.add(ReactionEmoji.CRY);
        defaultEmojis.add(ReactionEmoji.LIKE);
        defaultEmojis.add(ReactionEmoji.HEART_FACE);
        defaultEmojis.add(ReactionEmoji.SUNGLASSES);

        defaultTexts.addAll(ALL_TEXTS.subList(0, 3));

        mainWindow = createMainWindow();
        moreWindow = createMoreWindow();

        mainWindow.setVisible(false);
        moreWindow.setVisible(false);
        stage.addActor(mainWindow);
        stage.addActor(moreWindow);
    }

    private Window createMainWindow()
    {
        Window window = new Window("Reactions", skin);
        window.setSize(500, 400);
        window.setMovable(true);

        Table content = new Table();
        content.defaults().pad(10);

        // Emoji grid (1x5)
        Table emojiTable = new Table();
        for (ReactionEmoji emoji : defaultEmojis)
        {
            ImageButton emojiBtn = createEmojiButton(emoji);
            emojiBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    App.getCurrentGame().getCurrentPlayer().setReaction(emoji);
                    toggleVisibility();
                }
            });
            emojiTable.add(emojiBtn).size(48);
        }
        content.add(emojiTable).row();

        // Text options
        Table textTable = new Table();
        for (String text : defaultTexts)
        {
            TextButton textBtn = new TextButton(text, skin);
            textBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    App.getCurrentGame().getCurrentPlayer().setReaction(text);
                    toggleVisibility();
                }
            });
            textTable.add(textBtn).pad(5).width(100).row();
        }
        content.add(textTable).row();

        // Action buttons
        TextButton moreBtn = new TextButton("More", skin);
        moreBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainWindow.setVisible(false);
                moreWindow.setVisible(true);
            }
        });

        TextButton customBtn = new TextButton("Custom Text", skin);
        customBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCustomTextDialog();
            }
        });

        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility();
            }
        });

        content.add(moreBtn).padTop(20);
        content.add(customBtn);
        content.add(closeBtn);

        window.add(content);
        return window;
    }

    private Window createMoreWindow()
    {
        Window window = new Window("More Reactions", skin);
        window.setSize(600, 500);
        window.setMovable(true);

        Table content = new Table();
        content.defaults().pad(5);

        // Emoji grid (5x10)
        Table emojiTable = new Table();
        int col = 0;
        for (ReactionEmoji emoji : ALL_EMOJIS)
        {
            ImageButton emojiBtn = createEmojiButton(emoji);
            emojiBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    App.getCurrentGame().getCurrentPlayer().setReaction(emoji);
                    addToDefaultEmojis(emoji);
                    toggleVisibility();
                }
            });
            emojiTable.add(emojiBtn).size(48);
            if (++col % 10 == 0) emojiTable.row();
        }

        ScrollPane emojiScroll = new ScrollPane(emojiTable, skin);
        content.add(emojiScroll).height(300).row();

        // Text options
        Table textTable = new Table();
        for (String text : ALL_TEXTS)
        {
            Table row = new Table();
            Label textLabel = new Label(text, skin);
            TextButton setDefaultBtn = new TextButton("Set as Default", skin);
            setDefaultBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    addToDefaultTexts(text);
                }
            });

            TextButton chooseBtn = new TextButton("Choose", skin);
            chooseBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    App.getCurrentGame().getCurrentPlayer().setReaction(text);
                    toggleVisibility();
                }
            });

            row.add(textLabel).width(100);
            row.add(setDefaultBtn);
            row.add(chooseBtn);
            textTable.add(row).pad(5).row();
        }

        ScrollPane textScroll = new ScrollPane(textTable, skin);
        content.add(textScroll).height(150).row();

        // Back button
        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                moreWindow.setVisible(false);
                mainWindow.setVisible(true);
            }
        });
        content.add(backBtn).padTop(10);

        window.add(content);
        return window;
    }

    private ImageButton createEmojiButton(ReactionEmoji emoji)
    {
        return new ImageButton(new TextureRegionDrawable(emoji.getTexture()));
    }

    private void showCustomTextDialog()
    {
        Dialog dialog = new Dialog("Custom Reaction", skin);
        final TextField textField = new TextField("", skin);
        textField.setMessageText("Max 10 characters");

        dialog.getContentTable().add(textField).width(300).pad(10);

        // Create buttons with result objects
        TextButton confirmButton = new TextButton("Confirm", skin);
        TextButton cancelButton = new TextButton("Cancel", skin);

        dialog.getButtonTable().add(confirmButton).padRight(10);
        dialog.getButtonTable().add(cancelButton);

        // Handle button clicks
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String text = textField.getText().trim();
                if (!text.isEmpty()) {
                    if (text.length() > 10) {
                        UIRenderer.showTextBox("Text must be 10 characters or less");
                    } else {
                        App.getCurrentGame().getCurrentPlayer().setReaction(text);
                        addToDefaultTexts(text);
                        toggleVisibility();
                        dialog.hide();
                    }
                }
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });

        dialog.show(stage);
        dialog.setPosition(
            (stage.getWidth() - dialog.getWidth()) / 2,
            (stage.getHeight() - dialog.getHeight()) / 2
        );
    }

    public void addToDefaultEmojis(ReactionEmoji emoji) {
        if (defaultEmojis.size() < 5) {
            defaultEmojis.add(emoji);
        } else {
            defaultEmojis.set(nextEmojiReplaceIndex, emoji);
            nextEmojiReplaceIndex = (nextEmojiReplaceIndex + 1) % 5;
        }
        refreshMainWindow();
    }

    public void addToDefaultTexts(String text) {
        if (defaultTexts.size() < 3) {
            defaultTexts.add(text);
        } else {
            defaultTexts.set(nextTextReplaceIndex, text);
            nextTextReplaceIndex = (nextTextReplaceIndex + 1) % 3;
        }
        refreshMainWindow();
    }

    private void refreshMainWindow() {
        // Rebuild the main window content
        mainWindow.clear();
        Table newContent = ((Table) mainWindow.getChildren().get(0));
        mainWindow.add(createMainWindow().getChildren().first());
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        mainWindow.setVisible(isVisible);
        if (isVisible) center();
    }

    public boolean isVisible() {
        return isVisible;
    }

    private void center()
    {
        mainWindow.setPosition(
            (stage.getWidth() - mainWindow.getWidth()) / 2,
            (stage.getHeight() - mainWindow.getHeight()) / 2
        );
        moreWindow.setPosition(
            (stage.getWidth() - moreWindow.getWidth()) / 2,
            (stage.getHeight() - moreWindow.getHeight()) / 2
        );
    }

    public void dispose()
    {
        mainWindow.remove();
        moreWindow.remove();
    }
}
