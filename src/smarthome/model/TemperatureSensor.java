package smarthome.model;

/**
 * Sensor reporting temperature in Celsius.
 */
public class TemperatureSensor extends Sensor {

    private static final long serialVersionUID = 1L;

    private double temperatureCelsius;

    public TemperatureSensor(String sensorName, String roomId) {
        super(sensorName, roomId);
        this.temperatureCelsius = 25.0; // comfortable default
    }

    public double getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(double temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    @Override
    public String getSensorType() {
        return "Temperature";
    }

    @Override
    public String getFormattedValue() {
        return String.format("%.1f\u00B0C", temperatureCelsius);
    }
}
