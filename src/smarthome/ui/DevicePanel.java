package smarthome.ui;

import smarthome.exception.DeviceNotFoundException;
import smarthome.exception.InvalidDeviceStateException;
import smarthome.exception.RoomNotFoundException;
import smarthome.model.*;
import smarthome.service.DeviceService;
import smarthome.ui.components.*;
import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Devices tab: create devices via the factory (DeviceService),
 * turn them on/off, configure per-type settings, and remove them.
 * Includes a live search box that filters the table by name, type
 * or room, and a "Total power draw" summary chip that updates on
 * every refresh.
 */
public class DevicePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Device> allDevices = new ArrayList<>();
    private List<Device> currentDevices = new ArrayList<>();

    private final JComboBox<String> typeCombo = new JComboBox<>(DeviceService.DEVICE_TYPES);
    private final JTextField nameField = new JTextField(14);
    private final JComboBox<Room> roomCombo = new JComboBox<>();
    private final SearchField searchField = new SearchField("Filter devices...");
    private final JLabel powerChip = new JLabel();

    public DevicePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        add(buildHeader(), BorderLayout.NORTH);

        Card formCard = buildFormCard();

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Type", "Room", "Status", "Power (W)"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        TableStyler.style(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusCellRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1, true));
        Theme.styleScrollBar(tableScroll.getVerticalScrollBar());

        Card tableCard = new Card(new BorderLayout());
        tableCard.add(tableScroll, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 14));
        centerPanel.setOpaque(false);
        centerPanel.add(formCard, BorderLayout.NORTH);
        centerPanel.add(tableCard, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        add(buildButtonRow(), BorderLayout.SOUTH);

        searchField.onChange(this::applyFilterAndRender);
        table.getSelectionModel().addListSelectionListener(e -> {
        });
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        SectionTitle title = new SectionTitle("Devices", "Create, control and configure every smart device");
        header.add(title, BorderLayout.WEST);

        powerChip.setFont(Theme.FONT_BODY_BOLD);
        powerChip.setForeground(Theme.ACCENT);
        powerChip.setOpaque(true);
        powerChip.setBackground(Theme.ACCENT_DIM);
        powerChip.setBorder(new EmptyBorder(8, 14, 8, 14));
        JPanel chipWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        chipWrap.setOpaque(false);
        chipWrap.add(roundedChip(powerChip));
        header.add(chipWrap, BorderLayout.EAST);
        return header;
    }

    private JComponent roundedChip(JLabel label) {
        JPanel wrap = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.ACCENT_DIM);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS, Theme.RADIUS);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        wrap.setOpaque(false);
        label.setOpaque(false);
        wrap.add(label, BorderLayout.CENTER);
        return wrap;
    }

    private Card buildFormCard() {
        Card card = new Card(new BorderLayout(0, 10));
        JLabel heading = new JLabel("Add Device");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.TEXT_PRIMARY);
        card.add(heading, BorderLayout.NORTH);

        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        formRow.setOpaque(false);
        formRow.add(fieldLabel("Type"));
        formRow.add(typeCombo);
        formRow.add(fieldLabel("Name"));
        formRow.add(nameField);
        formRow.add(fieldLabel("Room"));
        formRow.add(roomCombo);
        PillButton addButton = new PillButton("+ Add Device", PillButton.Variant.PRIMARY);
        addButton.addActionListener(e -> onAddDevice());
        formRow.add(addButton);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        formRow.setAlignmentX(LEFT_ALIGNMENT);
        searchRow.setAlignmentX(LEFT_ALIGNMENT);
        stack.add(formRow);
        stack.add(Box.createVerticalStrut(4));
        stack.add(searchRow);
        card.add(stack, BorderLayout.CENTER);
        return card;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_SMALL);
        label.setForeground(Theme.TEXT_SECONDARY);
        return label;
    }

    private JPanel buildButtonRow() {
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        PillButton onButton = new PillButton("Turn ON", PillButton.Variant.PRIMARY);
        PillButton offButton = new PillButton("Turn OFF", PillButton.Variant.NEUTRAL);
        PillButton configureButton = new PillButton("Configure", PillButton.Variant.NEUTRAL);
        PillButton removeButton = new PillButton("Remove", PillButton.Variant.DANGER);

        onButton.addActionListener(e -> onTurnOn());
        offButton.addActionListener(e -> onTurnOff());
        configureButton.addActionListener(e -> onConfigure());
        removeButton.addActionListener(e -> onRemove());

        buttonRow.add(onButton);
        buttonRow.add(offButton);
        buttonRow.add(configureButton);
        buttonRow.add(removeButton);
        return buttonRow;
    }

    /** Called by MainFrame's global search bar to seed this panel's own filter from anywhere in the app. */
    public void applyExternalFilter(String query) {
        searchField.setQueryText(query);
        applyFilterAndRender();
    }

    private void applyFilterAndRender() {
        String query = searchField.getQuery();
        int selectedRow = table.getSelectedRow();
        Device previouslySelected = getSelectedDevice();

        currentDevices = allDevices.stream()
                .filter(d -> matches(d, query))
                .toList();

        tableModel.setRowCount(0);
        for (Device device : currentDevices) {
            Room room = mainFrame.getHome().getRooms().get(device.getRoomId());
            String roomName = room != null ? room.getRoomName() : "Unassigned";
            tableModel.addRow(new Object[]{
                    device.getDeviceId(), device.getDeviceName(), device.getDeviceType(),
                    roomName, device.getStatusSummary(),
                    String.format("%.1f", device.getCurrentPowerConsumption())
            });
        }
        if (previouslySelected != null) {
            int idx = currentDevices.indexOf(previouslySelected);
            if (idx >= 0) {
                table.setRowSelectionInterval(idx, idx);
                return;
            }
        }
        if (selectedRow >= 0 && selectedRow < currentDevices.size()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private boolean matches(Device device, String query) {
        if (query.isBlank()) return true;
        Room room = mainFrame.getHome().getRooms().get(device.getRoomId());
        String roomName = room != null ? room.getRoomName() : "";
        return device.getDeviceName().toLowerCase().contains(query)
                || device.getDeviceType().toLowerCase().contains(query)
                || roomName.toLowerCase().contains(query);
    }

    private void onAddDevice() {
        String type = (String) typeCombo.getSelectedItem();
        String name = nameField.getText();
        Room room = (Room) roomCombo.getSelectedItem();
        if (room == null) {
            showError("Create a room first (Rooms tab).");
            return;
        }
        try {
            mainFrame.getDeviceService().createDevice(type, name, room.getRoomId());
            nameField.setText("");
            mainFrame.refreshAll();
            SectionTitle.showToast(getRootPane(), "Device added", Theme.SUCCESS, 1800);
        } catch (IllegalArgumentException | RoomNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void onTurnOn() {
        Device device = getSelectedDevice();
        if (device == null) {
            showError("Select a device first.");
            return;
        }
        try {
            mainFrame.getDeviceService().turnOnDevice(device.getDeviceId());
            mainFrame.refreshAll();
        } catch (DeviceNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void onTurnOff() {
        Device device = getSelectedDevice();
        if (device == null) {
            showError("Select a device first.");
            return;
        }
        try {
            mainFrame.getDeviceService().turnOffDevice(device.getDeviceId());
            mainFrame.refreshAll();
        } catch (DeviceNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void onRemove() {
        Device device = getSelectedDevice();
        if (device == null) {
            showError("Select a device first.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove \"" + device.getDeviceName() + "\"?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        try {
            mainFrame.getDeviceService().removeDevice(device.getDeviceId());
            mainFrame.refreshAll();
        } catch (DeviceNotFoundException ex) {
            showError(ex.getMessage());
        }
    }

    private void onConfigure() {
        Device device = getSelectedDevice();
        if (device == null) {
            showError("Select a device first.");
            return;
        }
        try {
            if (device instanceof Light light) {
                configureLight(light);
            } else if (device instanceof Fan fan) {
                configureFan(fan);
            } else if (device instanceof AirConditioner ac) {
                configureAirConditioner(ac);
            } else if (device instanceof SmartTV tv) {
                configureSmartTV(tv);
            } else if (device instanceof SmartLock lock) {
                configureSmartLock(lock);
            } else if (device instanceof SecurityCamera camera) {
                JOptionPane.showMessageDialog(this,
                        "Monitoring: " + (camera.isMonitoring() ? "Active" : "Inactive")
                                + "\nMotion: " + (camera.isMotionDetected() ? "DETECTED" : "Clear"),
                        "Security Camera Status", JOptionPane.INFORMATION_MESSAGE);
            }
            mainFrame.refreshAll();
        } catch (InvalidDeviceStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void configureLight(Light light) {
        SpinnerNumberModel model = new SpinnerNumberModel(light.getBrightness(), 0, 100, 5);
        JSpinner spinner = new JSpinner(model);
        int result = JOptionPane.showConfirmDialog(this, wrapWithLabel("Brightness (%):", spinner),
                "Configure Light", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            light.setBrightness((Integer) spinner.getValue());
        }
    }

    private void configureFan(Fan fan) {
        SpinnerNumberModel model = new SpinnerNumberModel(fan.getSpeedLevel(), 0, 5, 1);
        JSpinner spinner = new JSpinner(model);
        int result = JOptionPane.showConfirmDialog(this, wrapWithLabel("Speed (0-5):", spinner),
                "Configure Fan", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            fan.setSpeedLevel((Integer) spinner.getValue());
        }
    }

    private void configureAirConditioner(AirConditioner ac) {
        SpinnerNumberModel tempModel = new SpinnerNumberModel(
                ac.getTemperature(), AirConditioner.MIN_TEMP, AirConditioner.MAX_TEMP, 1);
        JSpinner tempSpinner = new JSpinner(tempModel);
        JComboBox<AirConditioner.Mode> modeCombo = new JComboBox<>(AirConditioner.Mode.values());
        modeCombo.setSelectedItem(ac.getMode());

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Temperature (16-30\u00B0C):"));
        panel.add(tempSpinner);
        panel.add(new JLabel("Mode:"));
        panel.add(modeCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Configure Air Conditioner",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            ac.setTemperature((Integer) tempSpinner.getValue());
            ac.setMode((AirConditioner.Mode) modeCombo.getSelectedItem());
        }
    }

    private void configureSmartTV(SmartTV tv) {
        SpinnerNumberModel volumeModel = new SpinnerNumberModel(tv.getVolume(), 0, 100, 5);
        SpinnerNumberModel channelModel = new SpinnerNumberModel(tv.getChannel(), 1, 999, 1);
        JSpinner volumeSpinner = new JSpinner(volumeModel);
        JSpinner channelSpinner = new JSpinner(channelModel);

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Volume (0-100):"));
        panel.add(volumeSpinner);
        panel.add(new JLabel("Channel (1-999):"));
        panel.add(channelSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Configure Smart TV",
                JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            tv.setVolume((Integer) volumeSpinner.getValue());
            tv.setChannel((Integer) channelSpinner.getValue());
        }
    }

    private void configureSmartLock(SmartLock lock) {
        String[] options = {"LOCK", "UNLOCK"};
        int choice = JOptionPane.showOptionDialog(this,
                "Current status: " + (lock.isLocked() ? "LOCKED" : "UNLOCKED"),
                "Configure Smart Lock", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[lock.isLocked() ? 0 : 1]);
        if (choice == 0) {
            lock.lock();
        } else if (choice == 1) {
            lock.unlock();
        }
    }

    private JPanel wrapWithLabel(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    private Device getSelectedDevice() {
        int row = table.getSelectedRow();
        if (row < 0 || currentDevices == null || row >= currentDevices.size()) return null;
        return currentDevices.get(row);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refresh() {
        Room previouslySelectedRoom = (Room) roomCombo.getSelectedItem();
        roomCombo.removeAllItems();
        for (Room room : mainFrame.getHomeService().getAllRooms()) {
            roomCombo.addItem(room);
        }
        if (previouslySelectedRoom != null) {
            for (int i = 0; i < roomCombo.getItemCount(); i++) {
                if (roomCombo.getItemAt(i).getRoomId().equals(previouslySelectedRoom.getRoomId())) {
                    roomCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        allDevices = mainFrame.getDeviceService().getAllDevices();
        double totalPower = allDevices.stream().mapToDouble(Device::getCurrentPowerConsumption).sum();
        powerChip.setText(String.format("\u26A1 %.0f W total", totalPower));

        applyFilterAndRender();
    }

    /** Colours the Status column so ON/OFF and other states read as a pill rather than plain text. */
    private static class StatusCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            String text = String.valueOf(value);
            boolean positive = text.startsWith("ON") || text.contains("LOCKED") && !text.contains("UNLOCKED")
                    || text.contains("Monitoring");
            boolean negative = text.equals("OFF") || text.contains("UNLOCKED");
            Color fg = positive ? Theme.SUCCESS : negative ? Theme.TEXT_SECONDARY : Theme.TEXT_PRIMARY;

            JLabel label = new JLabel(text);
            label.setFont(Theme.FONT_BODY);
            label.setForeground(fg);
            label.setOpaque(true);
            label.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return label;
        }
    }
}
