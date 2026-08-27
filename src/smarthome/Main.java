package smarthome;

import smarthome.automation.AutomationEngine;
import smarthome.automation.RaiseAlertAction;
import smarthome.automation.TurnOnDeviceAction;
import smarthome.automation.conditions.*;
import smarthome.model.*;
import smarthome.persistence.FileManager;
import smarthome.service.*;
import smarthome.ui.MainFrame;

import javax.swing.*;
import java.time.LocalTime;

/**
 * Application entry point.
 *
 * Responsibilities (kept deliberately thin - all business logic
 * lives in the service layer, not here):
 *   1. Load saved data, or start a fresh Home if none exists.
 *   2. Construct every service and the AutomationEngine, wiring the
 *      OBSERVER PATTERN link between SensorService and AutomationEngine.
 *   3. Seed sample data on first launch only.
 *   4. Start the background schedule checker.
 *   5. Build and show MainFrame on the Swing Event Dispatch Thread.
 */
public final class Main {

    private Main() {
        // Utility entry-point class - not meant to be instantiated.
    }

    public static void main(String[] args) {
        smarthome.ui.theme.Theme.install();
        SwingUtilities.invokeLater(Main::startApplication);
    }

    private static void startApplication() {
        FileManager fileManager = new FileManager();
        boolean isFirstLaunch = !fileManager.saveFileExists();

        Home home = fileManager.loadHome();
        if (home == null) {
            home = new Home();
            isFirstLaunch = true;
        }

        HomeService homeService = new HomeService(home);
        DeviceService deviceService = new DeviceService(home);
        SensorService sensorService = new SensorService(home);
        AutomationEngine automationEngine = new AutomationEngine(home);
        AutomationService automationService = new AutomationService(home, automationEngine);
        ScheduleService scheduleService = new ScheduleService(home);
        SecurityService securityService = new SecurityService(home);
        AlertService alertService = new AlertService(home);

        // OBSERVER PATTERN wiring: AutomationEngine observes every sensor event
        // published by SensorService.
        sensorService.addListener(automationEngine);

        if (isFirstLaunch) {
            seedSampleData(homeService, deviceService, sensorService, automationService, scheduleService);
        }

        scheduleService.startScheduler();

        MainFrame mainFrame = new MainFrame(home, fileManager, homeService, deviceService,
                sensorService, automationService, scheduleService, securityService, alertService,
                automationEngine);
        mainFrame.setVisible(true);
    }

    /**
     * Populates a brand-new Home with sample rooms, devices, sensors,
     * automation rules and schedules, matching the walkthrough data
     * described in the project brief. Only ever called once, on the
     * very first launch (no save file present).
     */
    private static void seedSampleData(HomeService homeService, DeviceService deviceService,
                                        SensorService sensorService, AutomationService automationService,
                                        ScheduleService scheduleService) {
        try {
            // ---------- Rooms ----------
            Room livingRoom = homeService.addRoom("Living Room");
            Room bedroom = homeService.addRoom("Bedroom");
            Room kitchen = homeService.addRoom("Kitchen");
            Room studyRoom = homeService.addRoom("Study Room");
            Room bathroom = homeService.addRoom("Bathroom");
            Room frontDoor = homeService.addRoom("Front Door");

            // ---------- Devices ----------
            Device mainLight = deviceService.createDevice("Light", "Main Light", livingRoom.getRoomId());
            deviceService.createDevice("Fan", "Ceiling Fan", livingRoom.getRoomId());
            Device livingRoomAC = deviceService.createDevice("Air Conditioner", "Living Room AC", livingRoom.getRoomId());
            deviceService.createDevice("Security Camera", "Living Room Camera", livingRoom.getRoomId());

            deviceService.createDevice("Light", "Bedroom Light", bedroom.getRoomId());
            Device bedroomFan = deviceService.createDevice("Fan", "Bedroom Fan", bedroom.getRoomId());

            deviceService.createDevice("Light", "Kitchen Light", kitchen.getRoomId());

            Device studyLight = deviceService.createDevice("Light", "Study Light", studyRoom.getRoomId());

            deviceService.createDevice("Light", "Bathroom Light", bathroom.getRoomId());

            deviceService.createDevice("Smart Lock", "Front Door Lock", frontDoor.getRoomId());

            // ---------- Sensors ----------
            sensorService.createSensor("Motion", "Living Room Motion Sensor", livingRoom.getRoomId());
            sensorService.createSensor("Temperature", "Bedroom Temperature Sensor", bedroom.getRoomId());
            sensorService.createSensor("Smoke", "Kitchen Smoke Sensor", kitchen.getRoomId());
            sensorService.createSensor("Light", "Study Light Sensor", studyRoom.getRoomId());
            sensorService.createSensor("Door", "Front Door Sensor", frontDoor.getRoomId());

            // ---------- Automation Rules ----------
            automationService.createRule(
                    "AC Auto-Cool",
                    new TemperatureAboveCondition(30.0),
                    new TurnOnDeviceAction(livingRoomAC.getDeviceId(), livingRoomAC.getDeviceName()));

            automationService.createRule(
                    "Motion Security Alert",
                    new MotionDetectedCondition(true),
                    new RaiseAlertAction("Security", "Motion detected while security mode is ON", Alert.Severity.HIGH));

            automationService.createRule(
                    "Smoke Critical Alert",
                    new SmokeDetectedCondition(),
                    new RaiseAlertAction("Fire Safety", "Smoke detected in the home", Alert.Severity.CRITICAL));

            automationService.createRule(
                    "Door Security Alert",
                    new DoorOpenedCondition(true),
                    new RaiseAlertAction("Security", "Door opened while security mode is ON", Alert.Severity.HIGH));

            automationService.createRule(
                    "Auto Light on Low Light",
                    new LightLevelBelowCondition(30),
                    new TurnOnDeviceAction(studyLight.getDeviceId(), studyLight.getDeviceName()));

            // ---------- Schedules ----------
            scheduleService.createSchedule("Evening Lights On", mainLight,
                    Schedule.ScheduleAction.TURN_ON, LocalTime.of(19, 0), true);
            scheduleService.createSchedule("Night Lights Off", mainLight,
                    Schedule.ScheduleAction.TURN_OFF, LocalTime.of(22, 30), true);
            scheduleService.createSchedule("Morning Fan On", bedroomFan,
                    Schedule.ScheduleAction.TURN_ON, LocalTime.of(6, 0), true);

        } catch (Exception ex) {
            // Sample data creation should never crash a first launch; if seeding
            // fails partway through, log it and continue with whatever was created.
            System.err.println("Warning: sample data seeding encountered an issue - " + ex.getMessage());
        }
    }
}
