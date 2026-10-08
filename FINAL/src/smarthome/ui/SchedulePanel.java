package smarthome.ui;

import smarthome.exception.InvalidScheduleException;
import smarthome.model.Device;
import smarthome.model.Schedule;
import smarthome.ui.components.*;
import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Schedules tab: create time-of-day schedules that turn a device on
 * or off, either once or repeating daily. ScheduleService checks
 * these once every 30 seconds in the background and fires any whose
 * time has arrived, marshalled safely onto the Swing EDT. Includes a
 * live search box filtering by name, device or action.
 */
public class SchedulePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Schedule> allSchedules = new ArrayList<>();
    private List<Schedule> currentSchedules = new ArrayList<>();

    private final JTextField nameField = new JTextField(12);
    private final JComboBox<Device> deviceCombo = new JComboBox<>();
    private final JComboBox<Schedule.ScheduleAction> actionCombo = new JComboBox<>(Schedule.ScheduleAction.values());
    private final JSpinner hourSpinner = new JSpinner(new SpinnerNumberModel(19, 0, 23, 1));
    private final JSpinner minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
    private final JCheckBox repeatDailyCheck = new JCheckBox("Repeat daily", true);
    private final SearchField searchField = new SearchField("Filter schedules...");

    public SchedulePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("Schedules", "Time-based tasks that run automatically each day");
        add(title, BorderLayout.NORTH);

        Card formCard = buildFormCard();

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Name", "Device", "Action", "Time", "Repeat", "Enabled"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        TableStyler.style(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(6).setCellRenderer(new EnabledRenderer());
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

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        PillButton enableButton = new PillButton("Enable Selected", PillButton.Variant.NEUTRAL);
        PillButton disableButton = new PillButton("Disable Selected", PillButton.Variant.NEUTRAL);
        PillButton deleteButton = new PillButton("Delete Selected", PillButton.Variant.DANGER);

        enableButton.addActionListener(e -> setSelectedScheduleEnabled(true));
        disableButton.addActionListener(e -> setSelectedScheduleEnabled(false));
        deleteButton.addActionListener(e -> onDeleteSchedule());

        buttonRow.add(enableButton);
        buttonRow.add(disableButton);
        buttonRow.add(deleteButton);
        add(buttonRow, BorderLayout.SOUTH);

        searchField.onChange(this::applyFilterAndRender);
    }

    private Card buildFormCard() {
        Card card = new Card(new BorderLayout(0, 8));
        JLabel heading = new JLabel("Add Schedule");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.TEXT_PRIMARY);
        card.add(heading, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row.setOpaque(false);
        row.add(fieldLabel("Name"));
        row.add(nameField);
        row.add(fieldLabel("Device"));
        row.add(deviceCombo);
        row.add(fieldLabel("Action"));
        row.add(actionCombo);
        row.add(fieldLabel("Time"));
        JPanel timeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        timeWrap.setOpaque(false);
        hourSpinner.setPreferredSize(new Dimension(52, 30));
        minuteSpinner.setPreferredSize(new Dimension(52, 30));
        timeWrap.add(hourSpinner);
        timeWrap.add(new JLabel(":"));
        timeWrap.add(minuteSpinner);
        row.add(timeWrap);
        row.add(repeatDailyCheck);
        PillButton addButton = new PillButton("+ Add Schedule", PillButton.Variant.PRIMARY);
        addButton.addActionListener(e -> onAddSchedule());
        row.add(addButton);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        row.setAlignmentX(LEFT_ALIGNMENT);
        searchRow.setAlignmentX(LEFT_ALIGNMENT);
        stack.add(row);
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

    private void applyFilterAndRender() {
        String query = searchField.getQuery();
        int selectedRow = table.getSelectedRow();

        currentSchedules = allSchedules.stream()
                .filter(s -> matches(s, query))
                .toList();

        tableModel.setRowCount(0);
        for (Schedule schedule : currentSchedules) {
            tableModel.addRow(new Object[]{
                    schedule.getScheduleId(), schedule.getScheduleName(), schedule.getDeviceName(),
                    schedule.getAction(), schedule.getScheduledTime(),
                    schedule.isRepeatDaily() ? "Daily" : "Once",
                    schedule.isEnabled() ? "Yes" : "No"
            });
        }
        if (selectedRow >= 0 && selectedRow < currentSchedules.size()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private boolean matches(Schedule schedule, String query) {
        if (query.isBlank()) return true;
        return schedule.getScheduleName().toLowerCase().contains(query)
                || schedule.getDeviceName().toLowerCase().contains(query)
                || schedule.getAction().toString().toLowerCase().contains(query);
    }

    private void onAddSchedule() {
        String name = nameField.getText();
        Device device = (Device) deviceCombo.getSelectedItem();
        Schedule.ScheduleAction action = (Schedule.ScheduleAction) actionCombo.getSelectedItem();
        LocalTime time = LocalTime.of((Integer) hourSpinner.getValue(), (Integer) minuteSpinner.getValue());
        boolean repeatDaily = repeatDailyCheck.isSelected();

        try {
            mainFrame.getScheduleService().createSchedule(name, device, action, time, repeatDaily);
            nameField.setText("");
            mainFrame.refreshAll();
            SectionTitle.showToast(getRootPane(), "Schedule added", Theme.SUCCESS, 1800);
        } catch (InvalidScheduleException | IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void setSelectedScheduleEnabled(boolean enabled) {
        Schedule schedule = getSelectedSchedule();
        if (schedule == null) {
            showError("Select a schedule first.");
            return;
        }
        mainFrame.getScheduleService().setScheduleEnabled(schedule.getScheduleId(), enabled);
        mainFrame.refreshAll();
    }

    private void onDeleteSchedule() {
        Schedule schedule = getSelectedSchedule();
        if (schedule == null) {
            showError("Select a schedule first.");
            return;
        }
        mainFrame.getScheduleService().deleteSchedule(schedule.getScheduleId());
        mainFrame.refreshAll();
    }

    private Schedule getSelectedSchedule() {
        int row = table.getSelectedRow();
        if (row < 0 || currentSchedules == null || row >= currentSchedules.size()) return null;
        return currentSchedules.get(row);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refresh() {
        Device previouslySelectedDevice = (Device) deviceCombo.getSelectedItem();
        deviceCombo.removeAllItems();
        for (Device device : mainFrame.getDeviceService().getAllDevices()) {
            deviceCombo.addItem(device);
        }
        if (previouslySelectedDevice != null) {
            for (int i = 0; i < deviceCombo.getItemCount(); i++) {
                if (deviceCombo.getItemAt(i).getDeviceId().equals(previouslySelectedDevice.getDeviceId())) {
                    deviceCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        allSchedules = mainFrame.getScheduleService().getAllSchedules();
        applyFilterAndRender();
    }

    private static class EnabledRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrap.setOpaque(true);
            wrap.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            wrap.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
            wrap.add(StatusPill.onOff("Yes".equals(value)));
            return wrap;
        }
    }
}
