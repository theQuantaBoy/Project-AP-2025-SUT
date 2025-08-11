package ap.project.model.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

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

    public RadioPlayer(String musicFolderPath) {
        loadPlaylist(musicFolderPath);
    }

    private void loadPlaylist(String folderPath) {
        // Use absolute path instead of internal
        FileHandle dir = Gdx.files.internal(folderPath);

        Gdx.app.log("RadioPlayer", "Loading from absolute path: " + dir.path());
        Gdx.app.log("RadioPlayer", "Directory exists: " + dir.exists());

        if (dir.exists() && dir.isDirectory()) {
            for (FileHandle file : dir.list()) {
                Gdx.app.log("RadioPlayer", "Found file: " + file.name());
                if (file.extension().equalsIgnoreCase("ogg")) {
                    playlist.add(file.nameWithoutExtension());
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

}
