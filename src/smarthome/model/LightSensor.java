package smarthome.model;

/**
 * Sensor reporting ambient light level as a percentage (0-100).
 */
public class LightSensor extends Sensor {

    private static final long serialVersionUID = 1L;

    private int lightLevel; // 0-100

    public LightSensor(String sensorName, String roomId) {
        super(sensorName, roomId);
        this.lightLevel = 50;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        if (lightLevel < 0 || lightLevel > 100) {
            throw new IllegalArgumentException("Light level must be between 0 and 100");
        }
        this.lightLevel = lightLevel;
    }

    @Override
    public String getSensorType() {
        return "Light";
    }

    @Override
    public String getFormattedValue() {
        return lightLevel + "%";
    }
}
