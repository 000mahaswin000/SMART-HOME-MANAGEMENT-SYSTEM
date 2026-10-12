package smarthome.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import smarthome.exception.InvalidScheduleException;
import smarthome.model.Device;
import smarthome.model.Home;
import smarthome.model.Schedule;
import smarthome.model.SystemLog;

/**
 * Creates, removes, and runs device schedules.
 * This class does not depend on the user interface.
 */
public class ScheduleService {

    private final Home home;
    private Timer timer;
    private Consumer<String> fireCallback;

    public ScheduleService(Home home) {
        this.home = home;
    }

    /** Sets an optional listener for a fired schedule. */
    public void setFireCallback(Consumer<String> fireCallback) {
        this.fireCallback = fireCallback;
    }

    /** Creates and stores a new schedule. */
    public Schedule createSchedule(String scheduleName, Device device,
            Schedule.ScheduleAction action, LocalTime time, boolean repeatDaily) {
        if (device == null) {
            throw new InvalidScheduleException("A target device must be selected");
        }
        if (time == null) {
            throw new InvalidScheduleException("A scheduled time must be provided");
        }

        Schedule schedule = new Schedule(scheduleName, device.getDeviceId(),
                device.getDeviceName(), action, time, repeatDaily);
        home.getSchedules().put(schedule.getScheduleId(), schedule);
        home.addLog(SystemLog.EventType.SCHEDULE, "Schedule created: " + schedule);
        return schedule;
    }

    /** Removes a schedule. */
    public void deleteSchedule(String scheduleId) {
        home.getSchedules().remove(scheduleId);
    }

    /** Enables or disables a schedule. */
    public void setScheduleEnabled(String scheduleId, boolean enabled) {
        Schedule schedule = home.getSchedules().get(scheduleId);
        if (schedule != null) {
            schedule.setEnabled(enabled);
        }
    }

    /** Returns all schedules as a separate list. */
    public List<Schedule> getAllSchedules() {
        return new ArrayList<>(home.getSchedules().values());
    }

    /** Starts checking schedules every 30 seconds. */
    public void startScheduler() {
        if (timer != null) {
            return;
        }

        timer = new Timer("schedule-checker", true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkSchedules();
            }
        }, 0, 30_000);
    }

    /** Stops checking schedules. */
    public void stopScheduler() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /** Checks whether any schedule should run now. */
    private void checkSchedules() {
        try {
            LocalTime now = LocalTime.now().withSecond(0).withNano(0);
            String today = LocalDate.now().toString();
            List<Schedule> schedules = new ArrayList<>(home.getSchedules().values());

            for (Schedule schedule : schedules) {
                if (!schedule.isEnabled()) {
                    continue;
                }

                LocalTime scheduledTime = schedule.getScheduledTime()
                        .withSecond(0).withNano(0);
                if (!scheduledTime.equals(now)) {
                    continue;
                }

                if (today.equals(schedule.getLastFiredDate())) {
                    continue;
                }

                fireSchedule(schedule, today);
            }
        } catch (Exception ex) {
            System.err.println("Schedule check error: " + ex.getMessage());
        }
    }

    /** Runs one schedule and updates the device. */
    private void fireSchedule(Schedule schedule, String today) {
        Device device = home.getDevices().get(schedule.getDeviceId());
        if (device == null) {
            return;
        }

        if (schedule.getAction() == Schedule.ScheduleAction.TURN_ON) {
            device.turnOn();
        } else {
            device.turnOff();
        }

        schedule.setLastFiredDate(today);
        if (!schedule.isRepeatDaily()) {
            schedule.setEnabled(false);
        }

        String message = "Schedule \"" + schedule.getScheduleName()
                + "\" fired: " + schedule.getAction()
                + " " + device.getDeviceName();
        home.addLog(SystemLog.EventType.SCHEDULE, message);

        if (fireCallback != null) {
            fireCallback.accept("Schedule fired: " + schedule.getScheduleName()
                    + " -> " + schedule.getAction()
                    + " " + device.getDeviceName());
        }
    }
}
