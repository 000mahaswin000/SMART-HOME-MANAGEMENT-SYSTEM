package smarthome.ui;

import smarthome.model.*;
import smarthome.ui.components.*;
import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard tab: shows summary statistics for the whole home, a
 * power-consumption breakdown per room, an active-vs-inactive donut
 * for devices and sensors, and a feed of the most recent system
 * events with per-type icon colouring.
 */
public class DashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final MainFrame mainFrame;

    private final StatCard roomsCard = new StatCard("\u25A6", "Total Rooms", Theme.INFO);
    private final StatCard devicesCard = new StatCard("\u2699", "Total Devices", Theme.ACCENT);
    private final StatCard activeDevicesCard = new StatCard("\u26A1", "Active Devices", Theme.SUCCESS);
    private final StatCard sensorsCard = new StatCard("\u25C9", "Total Sensors", Theme.INFO);
    private final StatCard activeSensorsCard = new StatCard("\u2733", "Active Sensors", Theme.SUCCESS);
    private final StatCard rulesCard = new StatCard("\u26A1", "Active Rules", Theme.WARNING);
    private final StatCard scheduledCard = new StatCard("\u23F0", "Scheduled Tasks", Theme.INFO);
    private final StatCard securityCard = new StatCard("\u26E8", "Security Status", Theme.DANGER);
    private final StatCard alertsCard = new StatCard("\u26A0", "Unread Alerts", Theme.WARNING);
    private final StatCard powerCard = new StatCard("\u2607", "Live Power Draw", Theme.ACCENT);

    private final DonutChart deviceDonut = new DonutChart();
    private final DonutChart sensorDonut = new DonutChart();
    private final BarChart roomPowerChart = new BarChart();

    private final JPanel eventsFeed = new JPanel();
    private final JLabel eventsEmptyLabel = new JLabel("No events yet.");

    public DashboardPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setOpaque(false);
        setLayout(new BorderLayout(0, 0));
        setBorder(new EmptyBorder(Theme.PAD, Theme.PAD, Theme.PAD, Theme.PAD));

        JScrollPane scroll = new JScrollPane(buildContent());
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        Theme.styleScrollBar(scroll.getVerticalScrollBar());
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildContent() {
        ScrollableContent root = new ScrollableContent();

        SectionTitle title = new SectionTitle("Dashboard", "Live overview of your entire smart home");
        title.setAlignmentX(LEFT_ALIGNMENT);
        root.add(title);

        JPanel statsGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        statsGrid.setOpaque(false);
        statsGrid.setAlignmentX(LEFT_ALIGNMENT);
        for (StatCard card : new StatCard[]{roomsCard, devicesCard, activeDevicesCard, sensorsCard,
                activeSensorsCard, rulesCard, scheduledCard, securityCard, alertsCard, powerCard}) {
            card.setPreferredSize(new Dimension(198, 118));
            statsGrid.add(card);
        }
        root.add(statsGrid);
        root.add(Box.createVerticalStrut(16));

        JPanel middleRow = new JPanel(new GridLayout(1, 3, 12, 0));
        middleRow.setOpaque(false);
        middleRow.setAlignmentX(LEFT_ALIGNMENT);
        middleRow.setPreferredSize(new Dimension(100, 210));
        middleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));

        middleRow.add(chartCard("Devices", deviceDonut));
        middleRow.add(chartCard("Sensors", sensorDonut));

        Card powerCardWrap = new Card(new BorderLayout(0, 10));
        JLabel powerTitle = new JLabel("Power by Room");
        powerTitle.setFont(Theme.FONT_HEADING);
        powerTitle.setForeground(Theme.TEXT_PRIMARY);
        powerCardWrap.add(powerTitle, BorderLayout.NORTH);
        JScrollPane barScroll = new JScrollPane(roomPowerChart,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        barScroll.setBorder(null);
        barScroll.setOpaque(false);
        barScroll.getViewport().setOpaque(false);
        Theme.styleScrollBar(barScroll.getVerticalScrollBar());
        powerCardWrap.add(barScroll, BorderLayout.CENTER);
        middleRow.add(powerCardWrap);

        root.add(middleRow);
        root.add(Box.createVerticalStrut(16));

        Card eventsCard = new Card(new BorderLayout(0, 10));
        eventsCard.setAlignmentX(LEFT_ALIGNMENT);
        eventsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
        JLabel eventsTitle = new JLabel("Recent Events");
        eventsTitle.setFont(Theme.FONT_HEADING);
        eventsTitle.setForeground(Theme.TEXT_PRIMARY);
        eventsCard.add(eventsTitle, BorderLayout.NORTH);

        eventsFeed.setOpaque(false);
        eventsFeed.setLayout(new BoxLayout(eventsFeed, BoxLayout.Y_AXIS));
        eventsEmptyLabel.setForeground(Theme.TEXT_MUTED);
        eventsEmptyLabel.setFont(Theme.FONT_BODY);
        eventsFeed.add(eventsEmptyLabel);

        JScrollPane eventsScroll = new JScrollPane(eventsFeed);
        eventsScroll.setBorder(null);
        eventsScroll.setOpaque(false);
        eventsScroll.getViewport().setOpaque(false);
        eventsScroll.setPreferredSize(new Dimension(100, 280));
        Theme.styleScrollBar(eventsScroll.getVerticalScrollBar());
        eventsCard.add(eventsScroll, BorderLayout.CENTER);
        root.add(eventsCard);

        return root;
    }

    private Card chartCard(String title, DonutChart chart) {
        Card card = new Card(new BorderLayout(0, 6));
        JLabel label = new JLabel(title);
        label.setFont(Theme.FONT_HEADING);
        label.setForeground(Theme.TEXT_PRIMARY);
        card.add(label, BorderLayout.NORTH);
        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setOpaque(false);
        wrap.add(chart);
        card.add(wrap, BorderLayout.CENTER);
        return card;
    }

    private static Color eventColor(SystemLog.EventType type) {
        return switch (type) {
            case DEVICE -> Theme.ACCENT;
            case SENSOR -> Theme.INFO;
            case AUTOMATION -> Theme.WARNING;
            case SCHEDULE -> Theme.INFO;
            case SECURITY -> Theme.DANGER;
            case SYSTEM -> Theme.TEXT_MUTED;
        };
    }

    private JPanel buildEventRow(SystemLog log) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(6, 2, 6, 2));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        Color c = eventColor(log.getEventType());
        JLabel dot = new JLabel("\u25CF");
        dot.setForeground(c);
        dot.setFont(Theme.FONT_SMALL);
        dot.setPreferredSize(new Dimension(14, 20));

        JLabel desc = new JLabel(log.getDescription());
        desc.setFont(Theme.FONT_BODY);
        desc.setForeground(Theme.TEXT_PRIMARY);

        JLabel time = new JLabel(log.getFormattedTimestamp());
        time.setFont(Theme.FONT_SMALL);
        time.setForeground(Theme.TEXT_MUTED);

        JPanel left = new JPanel(new BorderLayout(8, 0));
        left.setOpaque(false);
        left.add(dot, BorderLayout.WEST);
        left.add(desc, BorderLayout.CENTER);

        row.add(left, BorderLayout.CENTER);
        row.add(time, BorderLayout.EAST);
        return row;
    }

    public void refresh() {
        roomsCard.setValue(String.valueOf(mainFrame.getHomeService().getAllRooms().size()));

        int totalDevices = mainFrame.getDeviceService().getAllDevices().size();
        int activeDevices = mainFrame.getDeviceService().getActiveDeviceCount();
        devicesCard.setValue(String.valueOf(totalDevices));
        activeDevicesCard.setValue(String.valueOf(activeDevices));

        int totalSensors = mainFrame.getSensorService().getAllSensors().size();
        int activeSensors = mainFrame.getSensorService().getActiveSensorCount();
        sensorsCard.setValue(String.valueOf(totalSensors));
        activeSensorsCard.setValue(String.valueOf(activeSensors));

        int activeRules = mainFrame.getAutomationService().getActiveRuleCount();
        int totalRules = mainFrame.getAutomationService().getAllRules().size();
        rulesCard.setValue(activeRules + " / " + totalRules);

        scheduledCard.setValue(String.valueOf(mainFrame.getScheduleService().getAllSchedules().size()));

        boolean securityOn = mainFrame.getSecurityService().isSecurityModeOn();
        securityCard.setValue(securityOn ? "ARMED" : "DISARMED");
        securityCard.setValueColor(securityOn ? Theme.SUCCESS : Theme.TEXT_SECONDARY);

        int unread = mainFrame.getAlertService().getUnreadAlertCount();
        alertsCard.setValue(String.valueOf(unread));
        alertsCard.setValueColor(unread > 0 ? Theme.WARNING : Theme.TEXT_PRIMARY);

        double totalPower = mainFrame.getDeviceService().getAllDevices().stream()
                .mapToDouble(Device::getCurrentPowerConsumption).sum();
        powerCard.setValue(String.format("%.0f W", totalPower));

        deviceDonut.setData(List.of(
                new DonutChart.Segment("Active", activeDevices, Theme.SUCCESS),
                new DonutChart.Segment("Idle", totalDevices - activeDevices, Theme.BG_RAISED)
        ), totalDevices + "");

        sensorDonut.setData(List.of(
                new DonutChart.Segment("Active", activeSensors, Theme.INFO),
                new DonutChart.Segment("Idle", totalSensors - activeSensors, Theme.BG_RAISED)
        ), totalSensors + "");

        Map<String, Double> powerByRoom = new LinkedHashMap<>();
        for (Room room : mainFrame.getHomeService().getAllRooms()) {
            double roomPower = mainFrame.getDeviceService().getDevicesInRoom(room.getRoomId()).stream()
                    .mapToDouble(Device::getCurrentPowerConsumption).sum();
            powerByRoom.put(room.getRoomName(), roomPower);
        }
        List<BarChart.Row> rows = new ArrayList<>();
        for (Map.Entry<String, Double> entry : powerByRoom.entrySet()) {
            rows.add(new BarChart.Row(entry.getKey(), entry.getValue(), String.format("%.0fW", entry.getValue())));
        }
        roomPowerChart.setData(rows);

        List<SystemLog> logs = mainFrame.getHome().getLogs();
        eventsFeed.removeAll();
        if (logs.isEmpty()) {
            eventsFeed.add(eventsEmptyLabel);
        } else {
            int start = Math.max(0, logs.size() - 25);
            for (int i = logs.size() - 1; i >= start; i--) {
                eventsFeed.add(buildEventRow(logs.get(i)));
            }
        }
        eventsFeed.revalidate();
        eventsFeed.repaint();
    }
}
