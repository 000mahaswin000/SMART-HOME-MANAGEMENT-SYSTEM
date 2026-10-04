package smarthome.persistence;

import smarthome.model.Home;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Saves and loads the complete Home object graph using Java serialization.
 * Data is stored in data/home.dat relative to the working directory.
 */
public class FileManager {

    private static final String DATA_DIRECTORY = "data";
    private static final String DATA_FILE_NAME = "home.dat";

    private final Path dataFilePath;

    public FileManager() {
        this.dataFilePath = Path.of(DATA_DIRECTORY, DATA_FILE_NAME);
    }

    /** Saves the complete Home object graph to data/home.dat. */
    public void saveHome(Home home) throws IOException {
        Path parentDir = dataFilePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        Path temporaryFile = dataFilePath.resolveSibling(DATA_FILE_NAME + ".tmp");
        try (ObjectOutputStream output = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(temporaryFile)))) {
            output.writeObject(home);
        }

        try {
            Files.move(temporaryFile, dataFilePath,
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temporaryFile, dataFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /**
     * Loads the saved Home object graph from data/home.dat.
     *
     * @return the loaded Home, or null if no valid save file exists
     */
    public Home loadHome() {
        if (!Files.exists(dataFilePath)) return null;

        try (ObjectInputStream input = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(dataFilePath)))) {
            Object object = input.readObject();
            return object instanceof Home home ? home : null;
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            System.err.println("Warning: could not load saved data (" + ex.getMessage()
                    + "). Starting with fresh sample data.");
            return null;
        }
    }

    /** @return true when data/home.dat exists. */
    public boolean saveFileExists() {
        return Files.exists(dataFilePath);
    }
}
