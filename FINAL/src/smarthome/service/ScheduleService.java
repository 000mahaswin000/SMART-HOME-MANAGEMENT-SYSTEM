package smarthome.service;

import smarthome.exception.InvalidScheduleException;
import smarthome.model.Device;
import smarthome.model.Home;
import smarthome.model.Schedule;
import smarthome.model.SystemLog;

import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Service layer for schedule management and execution.
 *
 * Uses a single java.util.concurrent.ScheduledExecutorService background
 * thread that wakes once every minute (not "unnecessary threads" -
 * exactly one, doing exactly one lightweight job) and checks whether
 * any enabled schedule's time has arrived. When a schedule fires, the
 * actual device mutation and any UI callback are marshalled back onto
 * the Swing Event Dispatch Thread via SwingUtilities.invokeLater(),
 * per Swing's threading rules.
 */
public class ScheduleService {

    private final Home home;
    private ScheduledExecutorService executor;
    private Consumer<String> fireCallback; // notifies UI (log panel) when a schedule fires

    public ScheduleService(Home home) {
        this.home = home;
    }

    public void setFireCallback(Consumer<String> fireCallback) {
        this.fireCallback = fireCallback;
    }

    public Schedule createSchedule(String scheduleName, Device device,
                                    Schedule.ScheduleAction action, LocalTime time, boolean repeatDaily) {
        if (device == null) {
            throw new InvalidScheduleException("A target device must be selected");
        }
        if (time == null) {
            throw new InvalidScheduleException("A scheduled time must be provided");
        }
        Schedule schedule = new Schedule(scheduleName, device.getDeviceId(), device.getDeviceName(),
                action, time, repeatDaily);
        home.getSchedules().put(schedule.getScheduleId(), schedule);
        home.addLog(SystemLog.EventType.SCHEDULE, "Schedule created: " + schedule);
        return schedule;
    }

    public void deleteSchedule(String scheduleId) {
        home.getSchedules().remove(scheduleId);
    }

    public void setScheduleEnabled(String scheduleId, boolean enabled) {
        Schedule schedule = home.getSchedules().get(scheduleId);
        if (schedule != null) {
            schedule.setEnabled(enabled);
        }
    }

    public List<Schedule> getAllSchedules() {
        return new ArrayList<>(home.getSchedules().values());
    }

    /** Start the single background checker thread. Safe to call once at application startup. */
    public void startScheduler() {
        if (executor != null && !executor.isShutdown()) {
            return;
        }
        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "schedule-checker");
            t.setDaemon(true); // never blocks JVM shutdown
            return t;
        });
        executor.scheduleAtFixedRate(this::checkSchedulesSafely, 0, 30, TimeUnit.SECONDS);
    }

    /** Stop the background checker thread cleanly. Must be called on application exit. */
    public void stopScheduler() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    private void checkSchedulesSafely() {
        try {
            checkSchedules();
        } catch (Exception e) {
            // A scheduler tick must never kill the background thread.
            System.err.println("Schedule check error: " + e.getMessage());
        }
    }

    private void checkSchedules() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        String today = LocalDate.now().toString();

        List<Schedule> snapshot = new ArrayList<>(home.getSchedules().values());
        for (Schedule schedule : snapshot) {
            if (!schedule.isEnabled()) continue;
            LocalTime scheduledMinute = schedule.getScheduledTime().withSecond(0).withNano(0);
            if (!scheduledMinute.equals(now)) continue;
            if (today.equals(schedule.getLastFiredDate())) continue; // already fired today

            // Fire: mutate state and touch Swing components only on the EDT.
            SwingUtilities.invokeLater(() -> fireSchedule(schedule, today));
        }
    }

    private void fireSchedule(Schedule schedule, String today) {
        Device device = home.getDevices().get(schedule.getDeviceId());
        if (device != null) {
            if (schedule.getAction() == Schedule.ScheduleAction.TURN_ON) {
                device.turnOn();
            } else {
                device.turnOff();
            }
            schedule.setLastFiredDate(today);
            if (!schedule.isRepeatDaily()) {
                schedule.setEnabled(false); // one-off schedules disable themselves after firing
            }
            home.addLog(SystemLog.EventType.SCHEDULE, "Schedule \"" + schedule.getScheduleName()
                    + "\" fired: " + schedule.getAction() + " " + device.getDeviceName());
            if (fireCallback != null) {
                fireCallback.accept("Schedule fired: " + schedule.getScheduleName()
                        + " -> " + schedule.getAction() + " " + device.getDeviceName());
            }
        }
    }
}
