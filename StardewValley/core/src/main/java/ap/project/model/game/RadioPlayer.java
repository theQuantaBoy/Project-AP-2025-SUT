package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.network.client.GameClient;
import ap.project.network.shared.messages.RadioPlayMessage;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class RadioPlayer {

    private final ArrayList<String> playlist = new ArrayList<>();
    private int currentIndex = 0;
    private Music currentMusic;

    // Modes
    private boolean shuffleMode = false;
    private boolean repeatOne = false;
    private final Random random = new Random();
    private GameClient client = GameClient.getInstance();

    public RadioPlayer(String musicFolderPath) {
        loadPlaylist(musicFolderPath);
    }

    private void loadPlaylist(String folderPath) {
        File dir = new File(folderPath);

        Gdx.app.log("RadioPlayer", "Loading from path: " + dir.getAbsolutePath());
        Gdx.app.log("RadioPlayer", "Directory exists: " + dir.exists());

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {  // Important: listFiles() can return null
                for (File file : files) {
                    if (file.isFile()) {  // Ensure it's a file, not subdirectory
                        String fileName = file.getName();
                        Gdx.app.log("RadioPlayer", "Found file: " + fileName);

                        // Extract extension manually
                        int dotIndex = fileName.lastIndexOf('.');
                        if (dotIndex > 0) {  // Ensure dot exists and isn't first char
                            String extension = fileName.substring(dotIndex + 1);
                            if ("ogg".equalsIgnoreCase(extension)) {
                                String baseName = fileName.substring(0, dotIndex);
                                playlist.add(baseName);
                            }
                        }
                    }
                }
            }
        }

        if (playlist.isEmpty()) {
            Gdx.app.log("RadioPlayer", "No .ogg files found in: " + folderPath);
        }
    }

    public void playTrack(int index) {
        if (playlist.isEmpty()) return;

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        FileHandle file = Gdx.files.internal("music/" + playlist.get(index) + ".ogg");
        currentMusic = Gdx.audio.newMusic(file);
        currentMusic.setLooping(false);

        currentMusic.setOnCompletionListener(music -> playNext());

        currentMusic.play();
        currentIndex = index;

        Gdx.app.log("RadioPlayer", "Playing: " + playlist.get(index));
    }

    public void playNext() {
        if (playlist.isEmpty()) return;

        if (repeatOne) {
            playTrack(currentIndex);
            return;
        }

        if (shuffleMode) {
            int nextIndex;
            do {
                nextIndex = random.nextInt(playlist.size());
            } while (nextIndex == currentIndex && playlist.size() > 1);
            playTrack(nextIndex);
        } else {
            int nextIndex = (currentIndex + 1) % playlist.size();
            playTrack(nextIndex);
        }
    }

    public void playPrevious() {
        if (playlist.isEmpty()) return;

        if (shuffleMode) {
            int prevIndex;
            do {
                prevIndex = random.nextInt(playlist.size());
            } while (prevIndex == currentIndex && playlist.size() > 1);
            playTrack(prevIndex);
        } else {
            int prevIndex = (currentIndex - 1 + playlist.size()) % playlist.size();
            playTrack(prevIndex);
        }
    }

    public void toggleShuffle() {
        shuffleMode = !shuffleMode;
        Gdx.app.log("RadioPlayer", "Shuffle: " + shuffleMode);
    }

    public void toggleRepeatOne() {
        repeatOne = !repeatOne;
        Gdx.app.log("RadioPlayer", "Repeat One: " + repeatOne);
    }

    public void stop() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
        }
    }

//    public String getCurrentTrackName() {
//        if (playlist.isEmpty()) return null;
//        return playlist.get(currentIndex);
//    }

    public ArrayList<String> getPlaylist() {
        return playlist;
    }

    // Add these to RadioPlayer.java
    public boolean isPlaying() {
        return currentMusic != null && currentMusic.isPlaying();
    }

    public boolean isShuffleOn() {
        return shuffleMode;
    }

    public boolean isRepeatOn() {
        return repeatOne;
    }

    public void pause() {
        if (currentMusic != null) {
            currentMusic.pause();
        }
    }

    public void resume() {
        if (currentMusic != null) {
            currentMusic.play();
            client.send(new RadioPlayMessage(App.getCurrentGame().getCurrentPlayer().getUser().getHashId(), true));
        }
    }

    public String getCurrentTrackName() {
        if (currentIndex >= 0 && currentIndex < playlist.size()) {
            return playlist.get(currentIndex);
        }
        return null;
    }

    public void playTrackByName(String trackName) {
        if (playlist.isEmpty()) return;

        int index = playlist.indexOf(trackName);
        if (index != -1) {
            playTrack(index);
        } else {
            Gdx.app.log("RadioPlayer", "Track not found: " + trackName);
        }
    }

    public void playTrackFrom(String trackName, float startSeconds) {
        if (playlist.isEmpty()) return;

        int index = playlist.indexOf(trackName);
        if (index == -1) {
            Gdx.app.log("RadioPlayer", "Track not found: " + trackName);
            return;
        }

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        FileHandle file = Gdx.files.internal("music/" + playlist.get(index) + ".ogg");
        currentMusic = Gdx.audio.newMusic(file);
        currentMusic.setLooping(false);
        currentMusic.setOnCompletionListener(music -> playNext());

        currentMusic.play();
        currentMusic.setPosition(startSeconds); // jump to the timestamp
        currentIndex = index;

        Gdx.app.log("RadioPlayer", "Playing from " + startSeconds + "s: " + trackName);
    }

    public float getCurrentTime() {
        if (currentMusic != null) {
            return currentMusic.getPosition(); // returns seconds into the track
        }
        return 0f;
    }



}
