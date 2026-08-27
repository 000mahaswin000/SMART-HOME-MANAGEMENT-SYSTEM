package smarthome.ui;

import smarthome.automation.Action;
import smarthome.automation.Condition;
import smarthome.automation.RaiseAlertAction;
import smarthome.automation.TurnOffDeviceAction;
import smarthome.automation.TurnOnDeviceAction;
import smarthome.automation.conditions.*;
import smarthome.model.Alert;
import smarthome.model.AutomationRule;
import smarthome.model.Device;
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
 * Automation tab: build "IF condition THEN action" rules from the
 * available Condition/Action strategy implementations (STRATEGY
 * PATTERN), and manage their enabled state. Rules are evaluated by
 * AutomationEngine whenever a sensor event occurs (OBSERVER PATTERN).
 * Includes a live search box filtering by rule name or description.
 */
public class AutomationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String[] CONDITION_TYPES = {
            "Temperature Above", "Motion Detected", "Smoke Detected", "Light Level Below", "Door Opened"
    };
    private static final String[] ACTION_TYPES = {
            "Turn ON Device", "Turn OFF Device", "Raise Alert"
    };

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<AutomationRule> allRules = new ArrayList<>();
    private List<AutomationRule> currentRules = new ArrayList<>();

    private final JTextField ruleNameField = new JTextField(12);
    private final JComboBox<String> conditionTypeCombo = new JComboBox<>(CONDITION_TYPES);
    private final JSpinner conditionValueSpinner = new JSpinner(new SpinnerNumberModel(30.0, -50.0, 100.0, 1.0));
    private final JCheckBox requireSecurityModeCheck = new JCheckBox("Require security mode ON");
    private final JComboBox<String> actionTypeCombo = new JComboBox<>(ACTION_TYPES);
    private final JComboBox<Device> targetDeviceCombo = new JComboBox<>();
    private final JComboBox<Alert.Severity> severityCombo = new JComboBox<>(Alert.Severity.values());
    private final JTextField alertMessageField = new JTextField(16);
    private final SearchField searchField = new SearchField("Filter rules...");

    public AutomationPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("Automation", "Build if-this-then-that rules that react to sensors");
        add(title, BorderLayout.NORTH);

        Card formCard = buildFormCard();

        tableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Rule", "Enabled"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        TableStyler.style(table);
        table.getColumnModel().getColumn(2).setPreferredWidth(380);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setCellRenderer(new EnabledRenderer());
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

        enableButton.addActionListener(e -> setSelectedRuleEnabled(true));
        disableButton.addActionListener(e -> setSelectedRuleEnabled(false));
        deleteButton.addActionListener(e -> onDeleteRule());

        buttonRow.add(enableButton);
        buttonRow.add(disableButton);
        buttonRow.add(deleteButton);
        add(buttonRow, BorderLayout.SOUTH);

        conditionTypeCombo.addActionListener(e -> updateFormAvailability());
        actionTypeCombo.addActionListener(e -> updateFormAvailability());
        searchField.onChange(this::applyFilterAndRender);
        updateFormAvailability();
    }

    private Card buildFormCard() {
        Card card = new Card(new BorderLayout(0, 8));
        JLabel heading = new JLabel("Add Rule");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.TEXT_PRIMARY);
        card.add(heading, BorderLayout.NORTH);

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row1.setOpaque(false);
        row1.add(fieldLabel("Rule Name"));
        row1.add(ruleNameField);
        row1.add(fieldLabel("Condition"));
        row1.add(conditionTypeCombo);
        row1.add(fieldLabel("Value"));
        row1.add(conditionValueSpinner);
        row1.add(requireSecurityModeCheck);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        row2.setOpaque(false);
        row2.add(fieldLabel("Action"));
        row2.add(actionTypeCombo);
        row2.add(fieldLabel("Target Device"));
        row2.add(targetDeviceCombo);
        row2.add(fieldLabel("Severity"));
        row2.add(severityCombo);
        row2.add(fieldLabel("Message"));
        row2.add(alertMessageField);
        PillButton addButton = new PillButton("+ Add Rule", PillButton.Variant.PRIMARY);
        addButton.addActionListener(e -> onAddRule());
        row2.add(addButton);

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchRow.setOpaque(false);
        searchRow.add(searchField);

        JPanel stack = new JPanel();
        stack.setOpaque(false);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        row1.setAlignmentX(LEFT_ALIGNMENT);
        row2.setAlignmentX(LEFT_ALIGNMENT);
        searchRow.setAlignmentX(LEFT_ALIGNMENT);
        stack.add(row1);
        stack.add(row2);
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

    private void updateFormAvailability() {
        String conditionType = (String) conditionTypeCombo.getSelectedItem();
        boolean needsValue = "Temperature Above".equals(conditionType) || "Light Level Below".equals(conditionType);
        boolean needsSecurityFlag = "Motion Detected".equals(conditionType) || "Door Opened".equals(conditionType);
        conditionValueSpinner.setEnabled(needsValue);
        requireSecurityModeCheck.setEnabled(needsSecurityFlag);

        String actionType = (String) actionTypeCombo.getSelectedItem();
        boolean needsDevice = "Turn ON Device".equals(actionType) || "Turn OFF Device".equals(actionType);
        boolean needsAlert = "Raise Alert".equals(actionType);
        targetDeviceCombo.setEnabled(needsDevice);
        severityCombo.setEnabled(needsAlert);
        alertMessageField.setEnabled(needsAlert);
    }

    private void applyFilterAndRender() {
        String query = searchField.getQuery();
        int selectedRow = table.getSelectedRow();

        currentRules = allRules.stream()
                .filter(r -> query.isBlank()
                        || r.getRuleName().toLowerCase().contains(query)
                        || r.getRuleDescription().toLowerCase().contains(query))
                .toList();

        tableModel.setRowCount(0);
        for (AutomationRule rule : currentRules) {
            tableModel.addRow(new Object[]{
                    rule.getRuleId(), rule.getRuleName(), rule.getRuleDescription(),
                    rule.isEnabled() ? "Yes" : "No"
            });
        }
        if (selectedRow >= 0 && selectedRow < currentRules.size()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void onAddRule() {
        String ruleName = ruleNameField.getText();
        try {
            Condition condition = buildCondition();
            Action action = buildAction();
            if (action == null) {
                showError("Select a target device, or fill in the alert message.");
                return;
            }
            mainFrame.getAutomationService().createRule(ruleName, condition, action);
            ruleNameField.setText("");
            alertMessageField.setText("");
            mainFrame.refreshAll();
            SectionTitle.showToast(getRootPane(), "Rule created", Theme.SUCCESS, 1800);
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private Condition buildCondition() {
        String conditionType = (String) conditionTypeCombo.getSelectedItem();
        double value = (Double) conditionValueSpinner.getValue();
        boolean requireSecurity = requireSecurityModeCheck.isSelected();
        return switch (conditionType) {
            case "Temperature Above" -> new TemperatureAboveCondition(value);
            case "Motion Detected" -> new MotionDetectedCondition(requireSecurity);
            case "Smoke Detected" -> new SmokeDetectedCondition();
            case "Light Level Below" -> new LightLevelBelowCondition((int) value);
            case "Door Opened" -> new DoorOpenedCondition(requireSecurity);
            default -> throw new IllegalArgumentException("Unknown condition type: " + conditionType);
        };
    }

    private Action buildAction() {
        String actionType = (String) actionTypeCombo.getSelectedItem();
        if ("Turn ON Device".equals(actionType) || "Turn OFF Device".equals(actionType)) {
            Device device = (Device) targetDeviceCombo.getSelectedItem();
            if (device == null) return null;
            return "Turn ON Device".equals(actionType)
                    ? new TurnOnDeviceAction(device.getDeviceId(), device.getDeviceName())
                    : new TurnOffDeviceAction(device.getDeviceId(), device.getDeviceName());
        } else {
            String message = alertMessageField.getText();
            if (message == null || message.isBlank()) return null;
            Alert.Severity severity = (Alert.Severity) severityCombo.getSelectedItem();
            return new RaiseAlertAction("Automation", message.trim(), severity);
        }
    }

    private void setSelectedRuleEnabled(boolean enabled) {
        AutomationRule rule = getSelectedRule();
        if (rule == null) {
            showError("Select a rule first.");
            return;
        }
        mainFrame.getAutomationService().setRuleEnabled(rule.getRuleId(), enabled);
        mainFrame.refreshAll();
    }

    private void onDeleteRule() {
        AutomationRule rule = getSelectedRule();
        if (rule == null) {
            showError("Select a rule first.");
            return;
        }
        mainFrame.getAutomationService().deleteRule(rule.getRuleId());
        mainFrame.refreshAll();
    }

    private AutomationRule getSelectedRule() {
        int row = table.getSelectedRow();
        if (row < 0 || currentRules == null || row >= currentRules.size()) return null;
        return currentRules.get(row);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refresh() {
        Device previouslySelectedDevice = (Device) targetDeviceCombo.getSelectedItem();
        targetDeviceCombo.removeAllItems();
        for (Device device : mainFrame.getDeviceService().getAllDevices()) {
            targetDeviceCombo.addItem(device);
        }
        if (previouslySelectedDevice != null) {
            for (int i = 0; i < targetDeviceCombo.getItemCount(); i++) {
                if (targetDeviceCombo.getItemAt(i).getDeviceId().equals(previouslySelectedDevice.getDeviceId())) {
                    targetDeviceCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        allRules = mainFrame.getAutomationService().getAllRules();
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
