package smarthome.exception;

public class RoomNotFoundException extends Exception {

//    private static final long serialVersionUID = 1L;
    public RoomNotFoundException(String roomId) {
        super("Room not found: " + roomId);
    }
}
