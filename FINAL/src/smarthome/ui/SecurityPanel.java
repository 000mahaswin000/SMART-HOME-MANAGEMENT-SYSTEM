package smarthome.ui;

import smarthome.model.DoorSensor;
import smarthome.model.MotionSensor;
import smarthome.model.Room;
import smarthome.model.SecurityCamera;
import smarthome.model.Sensor;
import smarthome.ui.components.*;
import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Security tab: enable or disable security mode via a large hero
 * card, and see the current state of every security-relevant sensor
 * and camera in a styled table below. When security mode is ON,
 * motion and door events are treated as security alerts by the
 * automation rules that require it.
 */
public class SecurityPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;
    private final JLabel statusLabel = new JLabel();
    private final JLabel statusIcon = new JLabel();
    private final PillButton toggleButton = new PillButton("", PillButton.Variant.PRIMARY);
    private final Card heroCard = new Card(new BorderLayout(0, 0));
    private final DefaultTableModel tableModel;

    public SecurityPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        SectionTitle title = new SectionTitle("Security", "Arm or disarm home security monitoring");
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 14));
        center.setOpaque(false);
        center.add(buildHeroCard(), BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Device/Sensor", "Room", "Type", "Status"}, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        TableStyler.style(table);
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER_SUBTLE, 1, true));
        Theme.styleScrollBar(tableScroll.getVerticalScrollBar());

        Card tableCard = new Card(new BorderLayout(0, 8));
        JLabel tableHeading = new JLabel("Security Sensors & Cameras");
        tableHeading.setFont(Theme.FONT_HEADING);
        tableHeading.setForeground(Theme.TEXT_PRIMARY);
        tableCard.add(tableHeading, BorderLayout.NORTH);
        tableCard.add(tableScroll, BorderLayout.CENTER);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);
    }

    private Card buildHeroCard() {
        heroCard.withPadding(24, 26, 24, 26);

        statusIcon.setFont(new Font(statusIcon.getFont().getFamily(), Font.PLAIN, 40));
        statusIcon.setHorizontalAlignment(SwingConstants.CENTER);
        statusIcon.setPreferredSize(new Dimension(70, 70));

        statusLabel.setFont(Theme.FONT_DISPLAY);

        JLabel explanation = new JLabel(
                "<html>When armed, motion detected or a door opening will generate a<br>"
                        + "security alert. When disarmed, those events are still recorded<br>"
                        + "in the logs but no alert is raised.</html>");
        explanation.setFont(Theme.FONT_BODY);
        explanation.setForeground(Theme.TEXT_SECONDARY);

        toggleButton.addActionListener(e -> {
            mainFrame.getSecurityService().toggleSecurityMode();
            mainFrame.refreshAll();
        });

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        statusLabel.setAlignmentX(LEFT_ALIGNMENT);
        explanation.setAlignmentX(LEFT_ALIGNMENT);
        toggleButton.setAlignmentX(LEFT_ALIGNMENT);
        textStack.add(statusLabel);
        textStack.add(Box.createVerticalStrut(8));
        textStack.add(explanation);
        textStack.add(Box.createVerticalStrut(14));
        textStack.add(toggleButton);

        JPanel iconWrap = new JPanel(new BorderLayout());
        iconWrap.setOpaque(false);
        iconWrap.add(statusIcon, BorderLayout.NORTH);
        iconWrap.setBorder(new EmptyBorder(4, 0, 0, 24));

        heroCard.add(iconWrap, BorderLayout.WEST);
        heroCard.add(textStack, BorderLayout.CENTER);
        return heroCard;
    }

    public void refresh() {
        boolean on = mainFrame.getSecurityService().isSecurityModeOn();
        statusLabel.setText(on ? "System Armed" : "System Disarmed");
        statusLabel.setForeground(on ? Theme.SUCCESS : Theme.TEXT_SECONDARY);
        statusIcon.setText(on ? "\u26E8" : "\u26E8");
        statusIcon.setForeground(on ? Theme.SUCCESS : Theme.TEXT_MUTED);
        statusIcon.setOpaque(true);
        statusIcon.setBackground(on ? Theme.SUCCESS_DIM : Theme.BG_RAISED);
        heroCard.withBorderColor(on ? Theme.SUCCESS : Theme.BORDER_SUBTLE);
        heroCard.repaint();
        toggleButton.setText(on ? "Disarm Security Mode" : "Arm Security Mode");

        tableModel.setRowCount(0);
        for (Sensor sensor : mainFrame.getSensorService().getAllSensors()) {
            if (!(sensor instanceof MotionSensor) && !(sensor instanceof DoorSensor)) continue;
            Room room = mainFrame.getHome().getRooms().get(sensor.getRoomId());
            tableModel.addRow(new Object[]{
                    sensor.getSensorName(), room != null ? room.getRoomName() : "Unassigned",
                    sensor.getSensorType(), sensor.getFormattedValue()
            });
        }
        for (var device : mainFrame.getDeviceService().getAllDevices()) {
            if (!(device instanceof SecurityCamera camera)) continue;
            Room room = mainFrame.getHome().getRooms().get(device.getRoomId());
            tableModel.addRow(new Object[]{
                    device.getDeviceName(), room != null ? room.getRoomName() : "Unassigned",
                    "Security Camera", camera.getStatusSummary()
            });
        }
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                         boolean hasFocus, int row, int column) {
            String text = String.valueOf(value);
            boolean alarm = text.contains("DETECTED") || text.equals("OPEN");
            JLabel label = new JLabel(text);
            label.setFont(alarm ? Theme.FONT_BODY_BOLD : Theme.FONT_BODY);
            label.setForeground(alarm ? Theme.DANGER : Theme.TEXT_PRIMARY);
            label.setOpaque(true);
            label.setBackground(isSelected ? Theme.ACCENT_DIM
                    : (row % 2 == 0 ? Theme.BG_SURFACE : Theme.BG_SURFACE_ALT));
            label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            return label;
        }
    }
}
