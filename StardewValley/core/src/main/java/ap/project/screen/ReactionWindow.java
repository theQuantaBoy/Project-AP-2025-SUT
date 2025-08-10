package ap.project.screen;

import ap.project.model.App.App;
import ap.project.model.enums.ReactionEmoji;
import ap.project.model.App.GameAssetsManager;
import ap.project.visual.UIRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
        "Hi!", "Bye!", "OMG!", "GG", "HOT TO GO!!", "party 4 u", "so confusing", "SO JULIA!","AP Panic!!",
        "JUST A THEORY"
    );

    private final List<ReactionEmoji> defaultEmojis = new ArrayList<>();
    private final List<String> defaultTexts = new ArrayList<>();
    private int nextEmojiReplaceIndex = 0;
    private int nextTextReplaceIndex = 0;

    // Sizing & spacing
    private static final int EMOJI_SIZE = 48;          // 9×9 textures drawn as 48×48, like SLOT_SIZE in CookBookWindow
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 70;
    private static final float FONT_SCALE = 1.0f;

    // New tuneable gaps
    private static final int EMOJI_SPACE_DEFAULTS = 8; // space between default emojis
    private static final int SECTION_GAP = 24;         // vertical gap between title/sections
    private static final int MIDDLE_GAP = 24;          // space between text label and button in rows
    private static final int TEXT_ROW_HEIGHT = 56;        // row height for text+button rows
    private static final int TEXT_LIST_HEIGHT_MAIN = TEXT_ROW_HEIGHT * 3 + 50; // show all 3 rows fully

    private static final int GRID_COLS = 10;
    private static final int GRID_ROWS = 5;
    private static final int EMOJI_SPACE_MORE = 8;       // space between emoji cells in the grid
    private static final int COLUMN_GAP = 24;            // gap between emoji grid (left) and text pane (right)
    private static final int RIGHT_PANE_WIDTH = 500;     // width for the text list pane
    private static final int MORE_TEXT_LIST_HEIGHT = TEXT_ROW_HEIGHT * 5 + 30; // show ~5 rows

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
        window.setMovable(true);
        window.add(buildMainContent()).pad(15);
        window.pack();
        return window;
    }

    private Table buildMainContent() {
        Table content = new Table();
        content.defaults().pad(5).center();
        content.align(Align.center);

        // Emoji grid (1x5) - centered
        Table emojiTable = new Table();
        for (ReactionEmoji emoji : defaultEmojis)
        {
            Drawable emojiDrawable = new TextureRegionDrawable(emoji.getTexture());
            Image emojiImg = new Image(emojiDrawable);
            emojiImg.setSize(EMOJI_SIZE, EMOJI_SIZE);

            // Use Stack + Image instead of Button
            Stack emojiStack = new Stack();
            emojiStack.add(emojiImg);
            emojiStack.setSize(EMOJI_SIZE, EMOJI_SIZE);

            emojiStack.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    App.getCurrentGame().getCurrentPlayer().setReaction(emoji);
                    WorldScreen.getInstance().sendReactionMessage(emoji);
                    toggleVisibility();
                }
            });
            emojiTable.add(emojiStack).size(EMOJI_SIZE).pad(EMOJI_SPACE_DEFAULTS);
        }
        content.add(emojiTable).padTop(SECTION_GAP).row();

        // Text options - centered with proper spacing
        // Default text reactions (compact list with React button)
        Table defaultsList = new Table();
        defaultsList.defaults().pad(6).height(TEXT_ROW_HEIGHT).fillX();

        for (String text : defaultTexts) {
            Table row = new Table();
            Label lbl = new Label(text, skin);
            // keep normal font size; we’re increasing the container height instead of font size
            TextButton reactBtn = new TextButton("React", skin);
            reactBtn.getLabel().setFontScale(FONT_SCALE);
            reactBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    App.getCurrentGame().getCurrentPlayer().setReaction(text);
                    WorldScreen.getInstance().sendReactionMessage(text);
                    toggleVisibility();
                }
            });

            row.add(lbl).left().expandX().padRight(MIDDLE_GAP); // add breathing room between text and button
            row.add(reactBtn).width(140).height(BUTTON_HEIGHT);
            defaultsList.add(row).row();
        }

        ScrollPane defaultsScroll = new ScrollPane(defaultsList, skin);
        defaultsScroll.setFadeScrollBars(false);
        defaultsScroll.setScrollBarPositions(false, true);

        content.add(defaultsScroll)
            .padTop(SECTION_GAP)
            .height(TEXT_LIST_HEIGHT_MAIN)
            .width(420)     // narrow, just to avoid stretching; tweak freely
            .row();

        // Action buttons - centered in a single row
        Table buttonTable = new Table();
        buttonTable.defaults().pad(10).minWidth(BUTTON_WIDTH).height(BUTTON_HEIGHT);

        TextButton moreBtn = new TextButton("More", skin);
        moreBtn.getLabel().setFontScale(FONT_SCALE);
        moreBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                mainWindow.setVisible(false);
                moreWindow.setVisible(true);
            }
        });

        TextButton customBtn = new TextButton("Custom Text", skin);
        customBtn.getLabel().setFontScale(FONT_SCALE);
        customBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showCustomTextDialog();
            }
        });

        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.getLabel().setFontScale(FONT_SCALE);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleVisibility();
            }
        });

        buttonTable.add(moreBtn);
        buttonTable.add(customBtn);
        buttonTable.add(closeBtn);

        content.add(buttonTable).padTop(50).row();

        return content;
    }

    private Window createMoreWindow()
    {
        Window window = new Window("More Reactions", skin);
        window.setMovable(true);

        // Root content for "More"
        Table content = new Table();
        content.defaults().pad(8);
        window.add(content).pad(15);

        // ---------- LEFT: Emoji Grid (5 rows × 10 cols) ----------
        Table emojiGrid = new Table();
        emojiGrid.defaults().size(EMOJI_SIZE).pad(EMOJI_SPACE_MORE);

        // Build the grid exactly 10 columns per row
        int c = 0;
        for (ReactionEmoji re : ALL_EMOJIS) {
            // create the image like you already do (Image or ImageButton)
            Image img = new Image(new TextureRegionDrawable(re.getTexture()));
            // (optional crispness) ensure nearest:
            re.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            img.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    // your existing "choose emoji as default" rotation logic + feedback text
                    setDefaultEmojiWithRotation(re);
                    UIRenderer.showTextBox("Emoji set as default.");
                }
            });

            emojiGrid.add(img).size(EMOJI_SIZE);
            c++;
            if (c % GRID_COLS == 0) emojiGrid.row();
        }

        // ---------- RIGHT: Text list (Label + "Choose" button) ----------
        Table allTextsTable = new Table();
        allTextsTable.defaults().pad(6).height(TEXT_ROW_HEIGHT).fillX();

        for (String text : ALL_TEXTS) { // use your existing source for the full text options
            Table row = new Table();
            Label lbl = new Label(text, skin);

            TextButton chooseBtn = new TextButton("Choose", skin); // only one button, as requested
            chooseBtn.addListener(new ClickListener() {
                @Override public void clicked(InputEvent event, float x, float y) {
                    setDefaultTextWithRotation(text);       // your existing rotation logic
                    UIRenderer.showTextBox("Text set as default.");
                }
            });

            row.add(lbl).left().expandX().padRight(MIDDLE_GAP);
            row.add(chooseBtn).width(140).height(BUTTON_HEIGHT);
            allTextsTable.add(row).row();
        }

        ScrollPane textScroll = new ScrollPane(allTextsTable, skin);
        textScroll.setFadeScrollBars(false);
        textScroll.setScrollBarPositions(false, true);

        // ---------- Place left/right panes ----------
        content.add(emojiGrid).left().top();                                        // LEFT
        content.add(textScroll).right().top().width(RIGHT_PANE_WIDTH)
            .height(MORE_TEXT_LIST_HEIGHT).padLeft(COLUMN_GAP);                   // RIGHT
        content.row();

        // ---------- Bottom bar: Back ----------
        TextButton backBtn = new TextButton("Back", skin);
        backBtn.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                // hide More, show Defaults (use your existing toggling)
                moreWindow.setVisible(false);
                mainWindow.setVisible(true);
            }
        });

        // Place Back spanning both columns so it's always visible
        content.add(backBtn).colspan(2).center().padTop(SECTION_GAP);

        // Finalize like CookBookWindow
        window.pack();
        return window;
    }

    private void showCustomTextDialog()
    {
        Dialog dialog = new Dialog("Custom Reaction", skin);
        dialog.getTitleLabel().setFontScale(FONT_SCALE);
        dialog.getTitleLabel().setAlignment(Align.center);

        final TextField textField = new TextField("", skin);
        textField.setMessageText("Max 10 characters");
        textField.setAlignment(Align.center);
        textField.getStyle().font.getData().setScale(FONT_SCALE); // Larger font

        Table contentTable = new Table();
        contentTable.add(textField).width(400).height(60).pad(20); // Larger field
        dialog.getContentTable().add(contentTable);

        // Create buttons with result objects
        Table buttonTable = new Table();
        buttonTable.defaults().pad(10).minWidth(150).height(60);

        TextButton confirmButton = new TextButton("Confirm", skin);
        confirmButton.getLabel().setFontScale(FONT_SCALE);

        TextButton cancelButton = new TextButton("Cancel", skin);
        cancelButton.getLabel().setFontScale(FONT_SCALE);

        buttonTable.add(confirmButton).padRight(20);
        buttonTable.add(cancelButton);

        dialog.getButtonTable().add(buttonTable).padBottom(20);

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
                        WorldScreen.getInstance().sendReactionMessage(text);
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
        // Save current visibility and position
        boolean wasVisible = mainWindow.isVisible();
        float x = mainWindow.getX();
        float y = mainWindow.getY();

        // Remove old content
        mainWindow.clearChildren();

        // Rebuild content
        mainWindow.add(buildMainContent()).pad(15);
        mainWindow.pack();

        // Restore position and visibility
        mainWindow.setPosition(x, y);
        mainWindow.setVisible(wasVisible);
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

    private void setDefaultEmojiWithRotation(ReactionEmoji re) {
        if (defaultEmojis.size() < 5) {
            defaultEmojis.add(re);
        } else {
            defaultEmojis.set(nextEmojiReplaceIndex, re);
        }
        nextEmojiReplaceIndex = (nextEmojiReplaceIndex + 1) % 5;
        refreshMainWindow();
    }

    private void setDefaultTextWithRotation(String text) {
        if (defaultTexts.size() < 3) {
            defaultTexts.add(text);
        } else {
            defaultTexts.set(nextTextReplaceIndex, text);
        }
        nextTextReplaceIndex = (nextTextReplaceIndex + 1) % 3;
        refreshMainWindow();
    }
}
