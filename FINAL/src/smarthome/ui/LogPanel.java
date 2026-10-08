package smarthome.ui;

import smarthome.model.SystemLog;
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
 * Logs tab: a searchable, filterable, colour-coded history of every
 * system event (device, sensor, automation, schedule, security,
 * system), newest first.
 */
public class LogPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String ALL_TYPES = "All Types";

    private final MainFrame mainFrame;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<SystemLog> allLogs = new ArrayList<>();

    private final SearchField searchField = new SearchField("Filter logs...");
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{
            ALL_TYPES, "DEVICE", "SENSOR", "AUTOMATION", "SCHEDULE", "SECURITY", "SYSTEM"
    });
    private final JLabel countLabel = new JLabel();

    public LogPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("System Logs", "A complete history of everything that has happened");
        add(title, BorderLayout.NORTH);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterRow.setOpaque(false);
        filterRow.add(searchField);
        filterRow.add(typeCombo);
        countLabel.setFont(Theme.FONT_SMALL);
        countLabel.setForeground(Theme.TEXT_MUTED);
        JPanel filterRowWrap = new JPanel(new BorderLayout());
        filterRowWrap.setOpaque(false);
        filterRowWrap.add(filterRow, BorderLayout.WEST);
        JPanel countWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
        countWrap.setOpaque(false);
        countWrap.add(countLabel);
        filterRowWrap.add(countWrap, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new Object[]{"Time", "Type", "Description"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        TableStyler.style(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(1).setCellRenderer(new TypeRenderer());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1, true));
        Theme.styleScrollBar(tableScroll.getVerticalScrollBar());

        Card card = new Card(new BorderLayout(0, 10));
        card.add(filterRowWrap, BorderLayout.NORTH);
        card.add(tableScroll, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        PillButton clearButton = new PillButton("Clear Logs", PillButton.Variant.DANGER);
        clearButton.addActionListener(e -> onClearLogs());
        buttonRow.add(clearButton);
        add(buttonRow, BorderLayout.SOUTH);

        searchField.onChange(this::applyFilterAndRender);
        typeCombo.addActionListener(e -> applyFilterAndRender());
    }

    private void applyFilterAndRender() {
        String query = searchField.getQuery();
        String typeFilter = (String) typeCombo.getSelectedItem();

        List<SystemLog> filtered = allLogs.stream()
                .filter(log -> matches(log, query, typeFilter))
                .toList();

        tableModel.setRowCount(0);
        for (int i = filtered.size() - 1; i >= 0; i--) {
            SystemLog log = filtered.get(i);
            tableModel.addRow(new Object[]{
                    log.getFormattedTimestamp(), log.getEventType(), log.getDescription()
            });
        }
        countLabel.setText(filtered.size() + " of " + allLogs.size() + " events");
    }

    private boolean matches(SystemLog log, String query, String typeFilter) {
        boolean typeOk = ALL_TYPES.equals(typeFilter) || log.getEventType().name().equals(typeFilter);
        if (!typeOk) return false;
        if (query.isBlank()) return true;
        return log.getDescription().toLowerCase().contains(query);
    }

    private void onClearLogs() {
        int confirm = JOptionPane.showConfirmDialog(this, "Clear all logs?",
                "Confirm Clear", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;
        mainFrame.getHome().getLogs().clear();
        mainFrame.refreshAll();
    }

    public void refresh() {
        allLogs = mainFrame.getHome().getLogs();
        applyFilterAndRender();
    }

    private static Color typeColor(String type) {
        return switch (type) {
            case "DEVICE" -> Theme.ACCENT;
            case "SENSOR" -> Theme.INFO;
            case "AUTOMATION" -> Theme.WARNING;
            case "SCHEDULE" -> Theme.INFO;
            case "SECURITY" -> Theme.DANGER;
            default -> Theme.TEXT_MUTED;
        };
    }

    private static class TypeRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            String text = String.valueOf(value);
            Color c = typeColor(text);
            JLabel label = new JLabel(text);
            label.setFont(Theme.FONT_SMALL_BOLD);
            label.setForeground(c);
            label.setOpaque(true);
            label.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return label;
        }
    }
}
