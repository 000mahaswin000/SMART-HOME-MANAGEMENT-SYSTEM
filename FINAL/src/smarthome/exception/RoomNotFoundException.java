package smarthome.exception;

public class RoomNotFoundException extends Exception {

    public RoomNotFoundException(String roomId) {
        super("Room not found: " + roomId);
    }
}
