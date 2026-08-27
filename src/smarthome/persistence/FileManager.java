package smarthome.persistence;

import smarthome.model.Home;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles saving and loading the entire Home object graph using
 * Java's built-in Serializable mechanism - a standard-library-only
 * persistence approach with no external dependencies.
 *
 * Data is stored at data/home.dat relative to the working directory.
 * Missing or corrupt files are handled safely: loadHome() returns
 * null in that case, and the caller (Main) is responsible for
 * creating fresh sample data.
 */
public class FileManager {

    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE_NAME = "home.dat";

    private final Path dataFilePath;

    public FileManager() {
        this.dataFilePath = Path.of(DATA_DIRECTORY, DATA_FILE_NAME);
    }

    /**
     * Save the given Home object graph to disk.
     *
     * @param home the application state to persist
     * @throws IOException if the file cannot be written
     */
    public void saveHome(Home home) throws IOException {
        Path parentDir = dataFilePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(dataFilePath)))) {
            oos.writeObject(home);
        }
    }

    /**
     * Load a previously saved Home object graph from disk.
     *
     * @return the loaded Home, or null if no valid save file exists
     *         (missing file, corrupt file, or version mismatch are
     *         all handled safely by returning null rather than
     *         throwing, so the caller can fall back to sample data).
     */
    public Home loadHome() {
        if (!Files.exists(dataFilePath)) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(dataFilePath)))) {
            Object obj = ois.readObject();
            if (obj instanceof Home home) {
                return home;
            }
            return null;
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            // Corrupt or incompatible save file - fail safe, let caller create fresh data.
            System.err.println("Warning: could not load saved data (" + e.getMessage()
                    + "). Starting with fresh sample data.");
            return null;
        }
    }

    /** @return true if a save file currently exists on disk. */
    public boolean saveFileExists() {
        return Files.exists(dataFilePath);
    }
}
