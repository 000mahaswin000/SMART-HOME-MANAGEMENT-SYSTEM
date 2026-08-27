package smarthome.ui;

import smarthome.automation.AutomationEngine;
import smarthome.model.Home;
import smarthome.persistence.FileManager;
import smarthome.service.*;
import smarthome.ui.components.SearchField;
import smarthome.ui.components.SectionTitle;
import smarthome.ui.components.SideNav;
import smarthome.ui.theme.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The main application window. Owns every service and wires them
 * together, hosts a sidebar-navigated set of panels (one per
 * functional area) inside a CardLayout content area, and centralises
 * the refreshAll() call that keeps every panel's displayed data in
 * sync after any state-changing action.
 *
 * Panels are deliberately given a reference to this MainFrame rather
 * than to individual services, so every panel constructor has the
 * same simple signature and can reach whichever services it needs
 * via the getters below.
 *
 * Visual layout: a fixed-width icon+label sidebar on the left (see
 * {@link SideNav}), a slim header bar across the top (live clock,
 * security-mode indicator, quick global search, save status), and a
 * CardLayout content area on the right holding all nine panels.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private static final String[] NAV_GLYPHS = {
            "\u25A6", "\u2302", "\u26A1", "\u25C9", "\u2699", "\u23F0", "\u26E8", "\u26A0", "\u2263"
    };
    private static final String[] NAV_LABELS = {
            "Dashboard", "Rooms", "Devices", "Sensors", "Automation", "Schedules", "Security", "Alerts", "Logs"
    };
    private static final int ALERTS_NAV_INDEX = 7;

    private final Home home;
    private final FileManager fileManager;

    private final HomeService homeService;
    private final DeviceService deviceService;
    private final SensorService sensorService;
    private final AutomationService automationService;
    private final ScheduleService scheduleService;
    private final SecurityService securityService;
    private final AlertService alertService;
    private final AutomationEngine automationEngine;

    private DashboardPanel dashboardPanel;
    private RoomPanel roomPanel;
    private DevicePanel devicePanel;
    private SensorPanel sensorPanel;
    private AutomationPanel automationPanel;
    private SchedulePanel schedulePanel;
    private SecurityPanel securityPanel;
    private AlertPanel alertPanel;
    private LogPanel logPanel;

    private SideNav sideNav;
    private CardLayout contentLayout;
    private JPanel contentArea;

    private JLabel clockLabel;
    private JLabel securityPillLabel;
    private JLabel saveStatusLabel;
    private SearchField globalSearch;

    public MainFrame(Home home, FileManager fileManager, HomeService homeService,
                      DeviceService deviceService, SensorService sensorService,
                      AutomationService automationService, ScheduleService scheduleService,
                      SecurityService securityService, AlertService alertService,
                      AutomationEngine automationEngine) {
        super("Smart Home Automation Simulator");
        this.home = home;
        this.fileManager = fileManager;
        this.homeService = homeService;
        this.deviceService = deviceService;
        this.sensorService = sensorService;
        this.automationService = automationService;
        this.scheduleService = scheduleService;
        this.securityService = securityService;
        this.alertService = alertService;
        this.automationEngine = automationEngine;

        getContentPane().setBackground(Theme.BG_BASE);
        buildMenuBar();
        buildLayout();
        wireEngineCallbacks();
        wireKeyboardShortcuts();
        buildWindowCloseBehaviour();
        startClock();

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1040, 640));
        setLocationRelativeTo(null);

        refreshAll();
    }

    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Theme.BG_DEEPEST);
        menuBar.setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER_SUBTLE));

        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save Now");
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        saveItem.addActionListener(e -> saveData(false));

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> performShutdown());

        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        JMenu viewMenu = new JMenu("View");
        for (int i = 0; i < NAV_LABELS.length; i++) {
            int index = i;
            JMenuItem item = new JMenuItem(NAV_LABELS[i]);
            item.addActionListener(e -> sideNav.select(index));
            viewMenu.add(item);
        }
        menuBar.add(viewMenu);

        setJMenuBar(menuBar);
    }

    private void buildLayout() {
        dashboardPanel = new DashboardPanel(this);
        roomPanel = new RoomPanel(this);
        devicePanel = new DevicePanel(this);
        sensorPanel = new SensorPanel(this);
        automationPanel = new AutomationPanel(this);
        schedulePanel = new SchedulePanel(this);
        securityPanel = new SecurityPanel(this);
        alertPanel = new AlertPanel(this);
        logPanel = new LogPanel(this);

        sideNav = new SideNav();
        for (int i = 0; i < NAV_LABELS.length; i++) {
            sideNav.addItem(NAV_GLYPHS[i], NAV_LABELS[i]);
        }
        sideNav.setOnSelect(index -> contentLayout.show(contentArea, String.valueOf(index)));

        contentLayout = new CardLayout();
        contentArea = new JPanel(contentLayout);
        contentArea.setOpaque(true);
        contentArea.setBackground(Theme.BG_BASE);
        contentArea.add(dashboardPanel, "0");
        contentArea.add(roomPanel, "1");
        contentArea.add(devicePanel, "2");
        contentArea.add(sensorPanel, "3");
        contentArea.add(automationPanel, "4");
        contentArea.add(schedulePanel, "5");
        contentArea.add(securityPanel, "6");
        contentArea.add(alertPanel, "7");
        contentArea.add(logPanel, "8");
        contentLayout.show(contentArea, "0");

        JPanel sideWrap = new JPanel(new BorderLayout());
        sideWrap.setBackground(Theme.BG_DEEPEST);
        sideWrap.setPreferredSize(new Dimension(198, 0));
        sideWrap.setBorder(new MatteBorder(0, 0, 0, 1, Theme.BORDER_SUBTLE));
        sideWrap.add(buildBrandHeader(), BorderLayout.NORTH);
        sideWrap.add(sideNav, BorderLayout.CENTER);

        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);
        main.add(buildHeaderBar(), BorderLayout.NORTH);
        main.add(contentArea, BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(sideWrap, BorderLayout.WEST);
        getContentPane().add(main, BorderLayout.CENTER);
    }

    private JPanel buildBrandHeader() {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(new EmptyBorder(18, 16, 16, 16));

        JLabel brand = new JLabel("\u2302 SmartHome");
        brand.setFont(Theme.FONT_HEADING.deriveFont(16f));
        brand.setForeground(Theme.TEXT_PRIMARY);
        brand.setAlignmentX(LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("Automation Simulator");
        tagline.setFont(Theme.FONT_SMALL);
        tagline.setForeground(Theme.TEXT_MUTED);
        tagline.setAlignmentX(LEFT_ALIGNMENT);

        wrap.add(brand);
        wrap.add(Box.createVerticalStrut(2));
        wrap.add(tagline);
        return wrap;
    }

    private JPanel buildHeaderBar() {
        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(Theme.BG_DEEPEST);
        header.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 1, 0, Theme.BORDER_SUBTLE),
                new EmptyBorder(10, 20, 10, 20)));

        globalSearch = new SearchField("Search devices, sensors, rooms...");
        globalSearch.onChange(this::handleGlobalSearch);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(globalSearch);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        right.setOpaque(false);

        clockLabel = new JLabel();
        clockLabel.setFont(Theme.FONT_BODY);
        clockLabel.setForeground(Theme.TEXT_SECONDARY);

        securityPillLabel = new JLabel();
        securityPillLabel.setFont(Theme.FONT_SMALL_BOLD);
        securityPillLabel.setOpaque(false);
        securityPillLabel.setBorder(new EmptyBorder(5, 12, 5, 12));

        saveStatusLabel = new JLabel("Saved");
        saveStatusLabel.setFont(Theme.FONT_SMALL);
        saveStatusLabel.setForeground(Theme.TEXT_MUTED);

        right.add(saveStatusLabel);
        right.add(divider());
        right.add(securityPillLabel);
        right.add(divider());
        right.add(clockLabel);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JComponent divider() {
        JPanel d = new JPanel();
        d.setPreferredSize(new Dimension(1, 16));
        d.setBackground(Theme.BORDER_SUBTLE);
        return d;
    }

    /**
     * Filters the currently visible panel's own search box (if it has
     * one) using the global search text as a convenience, and always
     * jumps to the Devices tab on a non-empty query so a name typed
     * from anywhere in the app resolves somewhere useful. Each panel
     * still owns its own local filtering logic; this only seeds it.
     */
    private void handleGlobalSearch() {
        String query = globalSearch.getQuery();
        if (query.isBlank()) return;
        devicePanel.applyExternalFilter(query);
        sensorPanel.applyExternalFilter(query);
        sideNav.select(2);
    }

    private void startClock() {
        updateClock();
        Timer timer = new Timer(1000, e -> updateClock());
        timer.start();
    }

    private void updateClock() {
        if (clockLabel == null) return;
        clockLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEE d MMM, HH:mm:ss")));
    }

    private void wireKeyboardShortcuts() {
        JRootPane root = getRootPane();
        InputMap inputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = root.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), "saveNow");
        actionMap.put("saveNow", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                saveData(false);
            }
        });
    }

    /**
     * The AutomationEngine calls back into the UI whenever it raises
     * an alert or logs an automation event, so the relevant panels
     * refresh immediately rather than waiting for the next manual
     * action. All triggers into these callbacks originate from
     * Swing action listeners or from ScheduleService's EDT-marshalled
     * firing, so it is safe to touch Swing components here directly.
     */
    private void wireEngineCallbacks() {
        automationEngine.addAlertCallback(alert -> {
            refreshAll();
            SectionTitle.showToast(getRootPane(), "New " + alert.getSeverity() + " alert: " + alert.getMessage(),
                    Theme.severityColor(alert.getSeverity().name()), 3200);
        });
        automationEngine.addLogCallback(description -> refreshAll());
        scheduleService.setFireCallback(description -> {
            refreshAll();
            SectionTitle.showToast(getRootPane(), description, Theme.ACCENT, 2800);
        });
    }

    private void buildWindowCloseBehaviour() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performShutdown();
            }
        });
    }

    private void performShutdown() {
        saveData(true);
        scheduleService.stopScheduler();
        dispose();
        System.exit(0);
    }

    private void saveData(boolean silent) {
        try {
            fileManager.saveHome(home);
            if (saveStatusLabel != null) {
                saveStatusLabel.setText("Saved " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                saveStatusLabel.setForeground(Theme.SUCCESS);
            }
            if (!silent) {
                SectionTitle.showToast(getRootPane(), "Saved successfully", Theme.SUCCESS, 2000);
            }
        } catch (IOException ex) {
            if (saveStatusLabel != null) {
                saveStatusLabel.setText("Save failed");
                saveStatusLabel.setForeground(Theme.DANGER);
            }
            if (!silent) {
                JOptionPane.showMessageDialog(this,
                        "Could not save data: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("Warning: failed to save data on exit - " + ex.getMessage());
            }
        }
    }

    /** Refreshes every panel's displayed data. Called after any state-changing action anywhere in the app. */
    public void refreshAll() {
        if (dashboardPanel != null) dashboardPanel.refresh();
        if (roomPanel != null) roomPanel.refresh();
        if (devicePanel != null) devicePanel.refresh();
        if (sensorPanel != null) sensorPanel.refresh();
        if (automationPanel != null) automationPanel.refresh();
        if (schedulePanel != null) schedulePanel.refresh();
        if (securityPanel != null) securityPanel.refresh();
        if (alertPanel != null) alertPanel.refresh();
        if (logPanel != null) logPanel.refresh();

        if (securityPillLabel != null) {
            boolean on = securityService.isSecurityModeOn();
            securityPillLabel.setText(on ? "\u26E8 Armed" : "\u26E8 Disarmed");
            securityPillLabel.setOpaque(true);
            securityPillLabel.setBackground(on ? Theme.SUCCESS_DIM : Theme.BG_RAISED);
            securityPillLabel.setForeground(on ? Theme.SUCCESS : Theme.TEXT_SECONDARY);
        }
        if (sideNav != null) {
            sideNav.setBadge(ALERTS_NAV_INDEX, alertService.getUnreadAlertCount());
        }
    }

    /** Programmatically switches the visible tab; used by cross-panel shortcuts (e.g. "Create room first" links). */
    public void showTab(int index) {
        if (sideNav != null) sideNav.select(index);
    }

    // ---------- Service accessors used by panels ----------

    public Home getHome() {
        return home;
    }

    public HomeService getHomeService() {
        return homeService;
    }

    public DeviceService getDeviceService() {
        return deviceService;
    }

    public SensorService getSensorService() {
        return sensorService;
    }

    public AutomationService getAutomationService() {
        return automationService;
    }

    public ScheduleService getScheduleService() {
        return scheduleService;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public AlertService getAlertService() {
        return alertService;
    }

    public AutomationEngine getAutomationEngine() {
        return automationEngine;
    }
}
