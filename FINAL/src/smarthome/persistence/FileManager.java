package smarthome.persistence;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import smarthome.model.Home;

/**
 * Saves and loads the Home object in data/home.dat.
 */
public class FileManager {

    private static final Path DATA_FOLDER = Path.of("data");
    private static final Path DATA_FILE = DATA_FOLDER.resolve("home.dat");

    /**
     * Saves the Home object to the data folder.
     */
    public void saveHome(Home home) throws IOException {
        Files.createDirectories(DATA_FOLDER);

        try (ObjectOutputStream output = new ObjectOutputStream(
                Files.newOutputStream(DATA_FILE))) {
            output.writeObject(home);
        }
    }

    /**
     * Loads the Home object from the data folder.
     *
     * @return the saved Home, or null when no valid file exists
     */
    public Home loadHome() {
        if (!Files.exists(DATA_FILE)) {
            return null;
        }

        try (ObjectInputStream input = new ObjectInputStream(
                Files.newInputStream(DATA_FILE))) {
            Object object = input.readObject();

            if (object instanceof Home) {
                return (Home) object;
            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Could not load saved home: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Checks whether a saved Home file exists.
     */
    public boolean saveFileExists() {
        return Files.exists(DATA_FILE);
    }
}
