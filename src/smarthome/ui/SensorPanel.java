package smarthome.ui;

import smarthome.model.*;
import smarthome.service.SensorService;
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
 * Sensors tab: create sensors, and simulate real-world sensor events
 * (since there is no physical hardware) via dedicated buttons for
 * each sensor type. Every simulation call flows through
 * SensorService, which notifies the AutomationEngine observer and
 * therefore evaluates automation rules immediately. Includes a live
 * search box filtering by name, type or room.
 */
public class SensorPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Sensor> allSensors = new ArrayList<>();
    private List<Sensor> currentSensors = new ArrayList<>();

    private final JComboBox<String> typeCombo = new JComboBox<>(SensorService.SENSOR_TYPES);
    private final JTextField nameField = new JTextField(14);
    private final JComboBox<Room> roomCombo = new JComboBox<>();
    private final SearchField searchField = new SearchField("Filter sensors...");

    private final PillButton temperatureButton = new PillButton("Simulate Temp", PillButton.Variant.NEUTRAL);
    private final PillButton triggerMotionButton = new PillButton("Trigger Motion", PillButton.Variant.DANGER);
    private final PillButton clearMotionButton = new PillButton("Clear Motion", PillButton.Variant.NEUTRAL);
    private final PillButton triggerSmokeButton = new PillButton("Trigger Smoke", PillButton.Variant.DANGER);
    private final PillButton clearSmokeButton = new PillButton("Clear Smoke", PillButton.Variant.NEUTRAL);
    private final PillButton openDoorButton = new PillButton("Open Door", PillButton.Variant.NEUTRAL);
    private final PillButton closeDoorButton = new PillButton("Close Door", PillButton.Variant.NEUTRAL);
    private final PillButton lightLevelButton = new PillButton("Change Light Level", PillButton.Variant.NEUTRAL);

    public SensorPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("Sensors", "Simulate real-world sensor readings and triggers");
        add(title, BorderLayout.NORTH);

        Card formCard = buildFormCard();

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Room", "Type", "Value", "Status"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        TableStyler.style(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(4).setCellRenderer(new ValueCellRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonAvailability();
        });
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

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        buttonRow.setOpaque(false);
        temperatureButton.addActionListener(e -> onSimulateTemperature());
        triggerMotionButton.addActionListener(e -> onTriggerMotion());
        clearMotionButton.addActionListener(e -> onClearMotion());
        triggerSmokeButton.addActionListener(e -> onTriggerSmoke());
        clearSmokeButton.addActionListener(e -> onClearSmoke());
        openDoorButton.addActionListener(e -> onOpenDoor());
        closeDoorButton.addActionListener(e -> onCloseDoor());
        lightLevelButton.addActionListener(e -> onChangeLightLevel());

        buttonRow.add(temperatureButton);
        buttonRow.add(triggerMotionButton);
        buttonRow.add(clearMotionButton);
        buttonRow.add(triggerSmokeButton);
        buttonRow.add(clearSmokeButton);
        buttonRow.add(openDoorButton);
        buttonRow.add(closeDoorButton);
        buttonRow.add(lightLevelButton);
        add(buttonRow, BorderLayout.SOUTH);

        searchField.onChange(this::applyFilterAndRender);
        updateButtonAvailability();
    }

    private Card buildFormCard() {
        Card card = new Card(new BorderLayout(0, 10));
        JLabel heading = new JLabel("Add Sensor");
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
        PillButton addButton = new PillButton("+ Add Sensor", PillButton.Variant.PRIMARY);
        addButton.addActionListener(e -> onAddSensor());
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

    /** Called by MainFrame's global search bar to seed this panel's own filter from anywhere in the app. */
    public void applyExternalFilter(String query) {
        searchField.setQueryText(query);
        applyFilterAndRender();
    }

    private void applyFilterAndRender() {
        String query = searchField.getQuery();
        int selectedRow = table.getSelectedRow();
        Sensor previouslySelected = getSelectedSensor();

        currentSensors = allSensors.stream().filter(s -> matches(s, query)).toList();

        tableModel.setRowCount(0);
        for (Sensor sensor : currentSensors) {
            Room room = mainFrame.getHome().getRooms().get(sensor.getRoomId());
            String roomName = room != null ? room.getRoomName() : "Unassigned";
            tableModel.addRow(new Object[]{
                    sensor.getSensorId(), sensor.getSensorName(), roomName, sensor.getSensorType(),
                    sensor.getFormattedValue(), sensor.isActive() ? "Active" : "Inactive"
            });
        }
        if (previouslySelected != null) {
            int idx = currentSensors.indexOf(previouslySelected);
            if (idx >= 0) {
                table.setRowSelectionInterval(idx, idx);
                updateButtonAvailability();
                return;
            }
        }
        if (selectedRow >= 0 && selectedRow < currentSensors.size()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
        updateButtonAvailability();
    }

    private boolean matches(Sensor sensor, String query) {
        if (query.isBlank()) return true;
        Room room = mainFrame.getHome().getRooms().get(sensor.getRoomId());
        String roomName = room != null ? room.getRoomName() : "";
        return sensor.getSensorName().toLowerCase().contains(query)
                || sensor.getSensorType().toLowerCase().contains(query)
                || roomName.toLowerCase().contains(query);
    }

    private void onAddSensor() {
        String type = (String) typeCombo.getSelectedItem();
        String name = nameField.getText();
        Room room = (Room) roomCombo.getSelectedItem();
        if (room == null) {
            showError("Create a room first (Rooms tab).");
            return;
        }
        try {
            mainFrame.getSensorService().createSensor(type, name, room.getRoomId());
            nameField.setText("");
            mainFrame.refreshAll();
            SectionTitle.showToast(getRootPane(), "Sensor added", Theme.SUCCESS, 1800);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void onSimulateTemperature() {
        if (!(getSelectedSensor() instanceof TemperatureSensor sensor)) return;
        SpinnerNumberModel model = new SpinnerNumberModel(sensor.getTemperatureCelsius(), -20.0, 60.0, 0.5);
        JSpinner spinner = new JSpinner(model);
        int result = JOptionPane.showConfirmDialog(this, wrapWithLabel("Temperature (\u00B0C):", spinner),
                "Simulate Temperature", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            mainFrame.getSensorService().simulateTemperature(sensor, (Double) spinner.getValue());
            mainFrame.refreshAll();
        }
    }

    private void onTriggerMotion() {
        if (getSelectedSensor() instanceof MotionSensor sensor) {
            mainFrame.getSensorService().triggerMotion(sensor);
            mainFrame.refreshAll();
        }
    }

    private void onClearMotion() {
        if (getSelectedSensor() instanceof MotionSensor sensor) {
            mainFrame.getSensorService().clearMotion(sensor);
            mainFrame.refreshAll();
        }
    }

    private void onTriggerSmoke() {
        if (getSelectedSensor() instanceof SmokeSensor sensor) {
            mainFrame.getSensorService().triggerSmoke(sensor);
            mainFrame.refreshAll();
        }
    }

    private void onClearSmoke() {
        if (getSelectedSensor() instanceof SmokeSensor sensor) {
            mainFrame.getSensorService().clearSmoke(sensor);
            mainFrame.refreshAll();
        }
    }

    private void onOpenDoor() {
        if (getSelectedSensor() instanceof DoorSensor sensor) {
            mainFrame.getSensorService().openDoor(sensor);
            mainFrame.refreshAll();
        }
    }

    private void onCloseDoor() {
        if (getSelectedSensor() instanceof DoorSensor sensor) {
            mainFrame.getSensorService().closeDoor(sensor);
            mainFrame.refreshAll();
        }
    }

    private void onChangeLightLevel() {
        if (!(getSelectedSensor() instanceof LightSensor sensor)) return;
        SpinnerNumberModel model = new SpinnerNumberModel(sensor.getLightLevel(), 0, 100, 5);
        JSpinner spinner = new JSpinner(model);
        int result = JOptionPane.showConfirmDialog(this, wrapWithLabel("Light Level (%):", spinner),
                "Change Light Level", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                mainFrame.getSensorService().changeLightLevel(sensor, (Integer) spinner.getValue());
                mainFrame.refreshAll();
            } catch (IllegalArgumentException ex) {
                showError(ex.getMessage());
            }
        }
    }

    private JPanel wrapWithLabel(String label, JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(component);
        return panel;
    }

    private Sensor getSelectedSensor() {
        int row = table.getSelectedRow();
        if (row < 0 || currentSensors == null || row >= currentSensors.size()) return null;
        return currentSensors.get(row);
    }

    private void updateButtonAvailability() {
        Sensor selected = getSelectedSensor();
        temperatureButton.setEnabled(selected instanceof TemperatureSensor);
        triggerMotionButton.setEnabled(selected instanceof MotionSensor);
        clearMotionButton.setEnabled(selected instanceof MotionSensor);
        triggerSmokeButton.setEnabled(selected instanceof SmokeSensor);
        clearSmokeButton.setEnabled(selected instanceof SmokeSensor);
        openDoorButton.setEnabled(selected instanceof DoorSensor);
        closeDoorButton.setEnabled(selected instanceof DoorSensor);
        lightLevelButton.setEnabled(selected instanceof LightSensor);
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

        allSensors = mainFrame.getSensorService().getAllSensors();
        applyFilterAndRender();
    }

    /** Highlights alarm-style values (MOTION DETECTED, SMOKE DETECTED, OPEN) in a warning colour. */
    private static class ValueCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            String text = String.valueOf(value);
            boolean alarm = text.contains("DETECTED") || text.equals("OPEN");
            Color fg = alarm ? Theme.DANGER : Theme.TEXT_PRIMARY;

            JLabel label = new JLabel(text);
            label.setFont(alarm ? Theme.FONT_BODY_BOLD : Theme.FONT_BODY);
            label.setForeground(fg);
            label.setOpaque(true);
            label.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return label;
        }
    }
}
