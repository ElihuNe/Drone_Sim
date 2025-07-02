package app.model;

import org.json.JSONObject;

/**
 * Represents a drone model with its technical specifications.
 * Each instance describes the static properties of a drone type.
 */
public class DroneType {

    private final int id;                     // Unique ID of the drone type
    private final String manufacturer;        // Name of the manufacturer
    private final String typename;            // Model or type name
    private final double weight;              // Weight of the drone (kg)
    private final double maxSpeed;            // Maximum flight speed
    private final double batteryCapacity;     // Battery capacity (e.g. mAh)
    private final double controlRange;        // Max control range (e.g. meters)
    private final double maxCarriage;         // Maximum payload the drone can carry

    /**
     * Creates a DroneType object from a JSON response.
     *
     * @param o JSON object containing drone type info
     */
    public DroneType(JSONObject o) {
        this.id = o.getInt("id");
        this.manufacturer = o.getString("manufacturer");
        this.typename = o.getString("typename");
        this.weight = o.getDouble("weight");
        this.maxSpeed = o.getDouble("max_speed");
        this.batteryCapacity = o.getDouble("battery_capacity");
        this.controlRange = o.getDouble("control_range");
        this.maxCarriage = o.getDouble("max_carriage");
    }

    /** @return unique ID of the drone type */
    public int getId() { return id; }

    /** @return manufacturer name */
    public String getManufacturer() { return manufacturer; }

    /** @return model/type name */
    public String getTypename() { return typename; }

    /** @return drone's own weight */
    public double getWeight() { return weight; }

    /** @return drone's maximum speed */
    public double getMaxSpeed() { return maxSpeed; }

    /** @return battery capacity of the drone */
    public double getBatteryCapacity() { return batteryCapacity; }

    /** @return maximum remote control range */
    public double getControlRange() { return controlRange; }

    /** @return maximum payload capacity */
    public double getMaxCarriage() { return maxCarriage; }
}
