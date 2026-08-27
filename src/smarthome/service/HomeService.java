package smarthome.service;

import smarthome.exception.RoomNotFoundException;
import smarthome.model.Home;
import smarthome.model.Room;
import smarthome.model.SystemLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for room management. Keeps GUI panels free of
 * direct data-manipulation logic (separation of concerns).
 */
public class HomeService {

    private final Home home;

    public HomeService(Home home) {
        this.home = home;
    }

    public Home getHome() {
        return home;
    }

    /** Add a new room with the given name. */
    public Room addRoom(String roomName) {
        Room room = new Room(roomName);
        home.getRooms().put(room.getRoomId(), room);
        home.addLog(SystemLog.EventType.SYSTEM, "Room added: " + room.getRoomName());
        return room;
    }

    /**
     * Remove a room by ID. Devices/sensors that belonged to the room
     * are NOT deleted, but are left with a dangling roomId reference
     * cleared, so they show as unassigned rather than being lost.
     */
    public void removeRoom(String roomId) throws RoomNotFoundException {
        Room room = getRoomOrThrow(roomId);
        home.getRooms().remove(roomId);
        // Clear roomId on any devices/sensors that referenced this room
        home.getDevices().values().stream()
                .filter(d -> roomId.equals(d.getRoomId()))
                .forEach(d -> d.setRoomId(null));
        home.getSensors().values().stream()
                .filter(s -> roomId.equals(s.getRoomId()))
                .forEach(s -> s.setRoomId(null));
        home.addLog(SystemLog.EventType.SYSTEM, "Room removed: " + room.getRoomName());
    }

    public void renameRoom(String roomId, String newName) throws RoomNotFoundException {
        Room room = getRoomOrThrow(roomId);
        String oldName = room.getRoomName();
        room.setRoomName(newName);
        home.addLog(SystemLog.EventType.SYSTEM, "Room renamed: " + oldName + " -> " + room.getRoomName());
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(home.getRooms().values());
    }

    public Room getRoom(String roomId) throws RoomNotFoundException {
        return getRoomOrThrow(roomId);
    }

    private Room getRoomOrThrow(String roomId) throws RoomNotFoundException {
        Room room = home.getRooms().get(roomId);
        if (room == null) {
            throw new RoomNotFoundException(roomId);
        }
        return room;
    }
}
