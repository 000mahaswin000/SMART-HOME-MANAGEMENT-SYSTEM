package smarthome.ui;

import smarthome.model.Alert;
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
 * Alerts tab: displays every alert raised by the automation/security
 * system, colour-coded by severity via {@link StatusPill}, with
 * actions to mark as read or clear, plus a live search box and a
 * severity filter combo.
 */
public class AlertPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String ALL_SEVERITIES = "All Severities";

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Alert> allAlerts = new ArrayList<>();
    private List<Alert> currentAlerts = new ArrayList<>();

    private final SearchField searchField = new SearchField("Filter alerts...");
    private final JComboBox<String> severityCombo = new JComboBox<>(
            new String[]{ALL_SEVERITIES, "CRITICAL", "HIGH", "MEDIUM", "LOW"});

    public AlertPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("Alerts", "Everything raised by automation rules and security mode");
        add(title, BorderLayout.NORTH);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.add(searchField);
        filterRow.add(severityCombo);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Type", "Message", "Severity", "Time", "Status"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        TableStyler.style(table);
        table.getColumnModel().getColumn(2).setPreferredWidth(320);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(3).setCellRenderer(new SeverityRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ReadStatusRenderer());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1, true));
        Theme.styleScrollBar(tableScroll.getVerticalScrollBar());

        Card tableCard = new Card(new BorderLayout(0, 10));
        tableCard.add(filterRow, BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        PillButton markReadButton = new PillButton("Mark as Read", PillButton.Variant.NEUTRAL);
        PillButton clearSelectedButton = new PillButton("Clear Selected", PillButton.Variant.NEUTRAL);
        PillButton clearAllButton = new PillButton("Clear All", PillButton.Variant.DANGER);

        markReadButton.addActionListener(e -> onMarkAsRead());
        clearSelectedButton.addActionListener(e -> onClearSelected());
        clearAllButton.addActionListener(e -> onClearAll());

        buttonRow.add(markReadButton);
        buttonRow.add(clearSelectedButton);
        buttonRow.add(clearAllButton);
        add(buttonRow, BorderLayout.SOUTH);

        searchField.onChange(this::applyFilterAndRender);
        severityCombo.addActionListener(e -> applyFilterAndRender());
    }

    private void applyFilterAndRender() {
        String query = searchField.getQuery();
        String severityFilter = (String) severityCombo.getSelectedItem();
        int selectedRow = table.getSelectedRow();

        currentAlerts = allAlerts.stream()
                .filter(a -> matches(a, query, severityFilter))
                .toList();

        tableModel.setRowCount(0);
        for (Alert alert : currentAlerts) {
            tableModel.addRow(new Object[]{
                    alert.getAlertId(), alert.getType(), alert.getMessage(), alert.getSeverity(),
                    alert.getFormattedTimestamp(), alert.isRead() ? "Read" : "Unread"
            });
        }
        if (selectedRow >= 0 && selectedRow < currentAlerts.size()) {
            table.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private boolean matches(Alert alert, String query, String severityFilter) {
        boolean severityOk = ALL_SEVERITIES.equals(severityFilter)
                || alert.getSeverity().name().equals(severityFilter);
        if (!severityOk) return false;
        if (query.isBlank()) return true;
        return alert.getMessage().toLowerCase().contains(query)
                || alert.getType().toLowerCase().contains(query);
    }

    private void onMarkAsRead() {
        Alert alert = getSelectedAlert();
        if (alert == null) {
            showError("Select an alert first.");
            return;
        }
        mainFrame.getAlertService().markAsRead(alert.getAlertId());
        mainFrame.refreshAll();
    }

    private void onClearSelected() {
        Alert alert = getSelectedAlert();
        if (alert == null) {
            showError("Select an alert first.");
            return;
        }
        mainFrame.getAlertService().clearAlert(alert.getAlertId());
        mainFrame.refreshAll();
    }

    private void onClearAll() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all alerts?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        mainFrame.getAlertService().clearAllAlerts();
        mainFrame.refreshAll();
    }

    private Alert getSelectedAlert() {
        int row = table.getSelectedRow();
        if (row < 0 || currentAlerts == null || row >= currentAlerts.size()) return null;
        return currentAlerts.get(row);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void refresh() {
        allAlerts = mainFrame.getAlertService().getAllAlertsSorted();
        applyFilterAndRender();
    }

    /** Renders the Severity column as a coloured StatusPill instead of plain coloured text. */
    private static class SeverityRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            wrap.setOpaque(true);
            wrap.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            wrap.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 0));
            wrap.add(StatusPill.severity(String.valueOf(value)));
            return wrap;
        }
    }

    /** Renders the Status column (Read/Unread) as a small dot + label. */
    private static class ReadStatusRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            boolean unread = "Unread".equals(value);
            JLabel label = new JLabel((unread ? "\u25CF " : "\u25CB ") + value);
            label.setFont(unread ? Theme.FONT_BODY_BOLD : Theme.FONT_BODY);
            label.setForeground(unread ? Theme.WARNING : Theme.TEXT_MUTED);
            label.setOpaque(true);
            label.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return label;
        }
    }
}
