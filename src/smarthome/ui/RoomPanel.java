package smarthome.ui;

import smarthome.exception.RoomNotFoundException;
import smarthome.model.Device;
import smarthome.model.Room;
import smarthome.model.Sensor;
import smarthome.ui.components.*;
import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Rooms tab: add, rename, and remove rooms, and view what devices
 * and sensors currently belong to the selected room, presented as a
 * grid of room cards rather than a plain table, so the whole layout
 * of the home is visible at a glance.
 */
public class RoomPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;
    private List<Room> currentRooms = new ArrayList<>();
    private Room selectedRoom;

    private final JPanel roomGrid = new JPanel();
    private final JLabel emptyLabel = new JLabel("No rooms yet. Add your first room above.");

    public RoomPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("Rooms", "The physical layout of your smart home");
        add(title, BorderLayout.NORTH);

        Card formCard = buildFormCard();

        roomGrid.setOpaque(false);
        roomGrid.setLayout(new WrapLayout(FlowLayout.LEFT, 14, 14));        emptyLabel.setFont(Theme.FONT_BODY);
        emptyLabel.setForeground(Theme.TEXT_MUTED);

        JScrollPane gridScroll = new JScrollPane(roomGrid);
        gridScroll.setBorder(null);
        gridScroll.setOpaque(false);
        gridScroll.getViewport().setOpaque(false);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);
        Theme.styleScrollBar(gridScroll.getVerticalScrollBar());

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(formCard, BorderLayout.NORTH);
        centerPanel.add(gridScroll, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private Card buildFormCard() {
        Card card = new Card(new BorderLayout());
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setOpaque(false);
        PillButton addButton = new PillButton("+ Add Room", PillButton.Variant.PRIMARY);
        addButton.addActionListener(e -> onAddRoom());
        row.add(addButton);
        JLabel hint = new JLabel("Click a room card below to rename or remove it.");
        hint.setFont(Theme.FONT_SMALL);
        hint.setForeground(Theme.TEXT_MUTED);
        row.add(hint);
        card.add(row, BorderLayout.CENTER);
        return card;
    }

    private void onAddRoom() {
        String name = JOptionPane.showInputDialog(this, "Room name:", "Add Room", JOptionPane.PLAIN_MESSAGE);
        if (name == null) return; // cancelled
        try {
            mainFrame.getHomeService().addRoom(name);
            mainFrame.refreshAll();
            SectionTitle.showToast(getRootPane(), "Room added", Theme.SUCCESS, 1800);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onRenameRoom(Room room) {
        String newName = JOptionPane.showInputDialog(this, "New name:", room.getRoomName());
        if (newName == null) return;
        try {
            mainFrame.getHomeService().renameRoom(room.getRoomId(), newName);
            mainFrame.refreshAll();
        } catch (RoomNotFoundException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onRemoveRoom(Room room) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + room.getRoomName() + "\"? Devices and sensors inside it will become unassigned.",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            mainFrame.getHomeService().removeRoom(room.getRoomId());
            if (room.equals(selectedRoom)) selectedRoom = null;
            mainFrame.refreshAll();
        } catch (RoomNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JPanel buildRoomCard(Room room) {
        List<Device> devices = mainFrame.getDeviceService().getDevicesInRoom(room.getRoomId());
        List<Sensor> sensors = mainFrame.getSensorService().getAllSensors().stream()
                .filter(s -> room.getRoomId().equals(s.getRoomId())).toList();
        long activeDevices = devices.stream().filter(Device::isOn).count();

        Card card = new Card(new BorderLayout(0, 8));
        card.setPreferredSize(new Dimension(260, 220));
        card.withPadding(16, 16, 14, 16);

        JLabel nameLabel = new JLabel(room.getRoomName());
        nameLabel.setFont(Theme.FONT_HEADING.deriveFont(15f));
        nameLabel.setForeground(Theme.TEXT_PRIMARY);

        JLabel countsLabel = new JLabel(devices.size() + " devices \u00B7 " + sensors.size() + " sensors");
        countsLabel.setFont(Theme.FONT_SMALL);
        countsLabel.setForeground(Theme.TEXT_SECONDARY);

        JPanel headerStack = new JPanel();
        headerStack.setOpaque(false);
        headerStack.setLayout(new BoxLayout(headerStack, BoxLayout.Y_AXIS));
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);
        countsLabel.setAlignmentX(LEFT_ALIGNMENT);
        headerStack.add(nameLabel);
        headerStack.add(Box.createVerticalStrut(2));
        headerStack.add(countsLabel);

        StatusPill activePill = activeDevices > 0
                ? new StatusPill(activeDevices + " active", Theme.SUCCESS, Theme.SUCCESS, Theme.SUCCESS_DIM)
                : new StatusPill("idle", Theme.TEXT_MUTED, Theme.TEXT_SECONDARY, Theme.BG_RAISED);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);
        headerRow.add(headerStack, BorderLayout.WEST);
        JPanel pillWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pillWrap.setOpaque(false);
        pillWrap.add(activePill);
        headerRow.add(pillWrap, BorderLayout.EAST);
        card.add(headerRow, BorderLayout.NORTH);

        JTextArea contentsArea = new JTextArea();
        contentsArea.setEditable(false);
        contentsArea.setOpaque(false);
        contentsArea.setFont(Theme.FONT_MONO.deriveFont(11f));
        contentsArea.setForeground(Theme.TEXT_SECONDARY);
        contentsArea.setLineWrap(true);
        contentsArea.setWrapStyleWord(true);
        StringBuilder sb = new StringBuilder();
        if (devices.isEmpty() && sensors.isEmpty()) {
            sb.append("Empty room.");
        } else {
            for (Device d : devices) {
                sb.append(d.isOn() ? "\u25CF " : "\u25CB ").append(d.getDeviceName()).append('\n');
            }
            for (Sensor s : sensors) {
                sb.append("\u25C9 ").append(s.getSensorName()).append('\n');
            }
        }
        contentsArea.setText(sb.toString());
        JScrollPane contentsScroll = new JScrollPane(contentsArea);
        contentsScroll.setBorder(null);
        contentsScroll.setOpaque(false);
        contentsScroll.getViewport().setOpaque(false);
        Theme.styleScrollBar(contentsScroll.getVerticalScrollBar());
        card.add(contentsScroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        footer.setOpaque(false);
        PillButton renameBtn = new PillButton("Rename", PillButton.Variant.GHOST);
        renameBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        renameBtn.addActionListener(e -> onRenameRoom(room));
        PillButton removeBtn = new PillButton("Remove", PillButton.Variant.GHOST);
        removeBtn.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        removeBtn.setForeground(Theme.DANGER);
        removeBtn.addActionListener(e -> onRemoveRoom(room));
        footer.add(renameBtn);
        footer.add(removeBtn);
        card.add(footer, BorderLayout.SOUTH);

        return card;
    }

    public void refresh() {
        currentRooms = mainFrame.getHomeService().getAllRooms();
        roomGrid.removeAll();
        if (currentRooms.isEmpty()) {
            roomGrid.add(emptyLabel);
        } else {
            for (Room room : currentRooms) {
                roomGrid.add(buildRoomCard(room));
            }
        }
        roomGrid.revalidate();
        roomGrid.repaint();
    }
}
