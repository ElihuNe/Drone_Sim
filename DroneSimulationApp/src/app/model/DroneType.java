package app.model; 

import org.json.JSONObject;

public class DroneType {
    private final int id;
    private final String manufacturer;
    private final String typename;
    private final double weight;
    private final double maxSpeed;
    private final double batteryCapacity;
    private final double controlRange;
    private final double maxCarriage;

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

    public int getId() { return id; }
    public String getManufacturer() { return manufacturer; }
    public String getTypename() { return typename; }
    public double getWeight() { return weight; }
    public double getMaxSpeed() { return maxSpeed; }
    public double getBatteryCapacity() { return batteryCapacity; }
    public double getControlRange() { return controlRange; }
    public double getMaxCarriage() { return maxCarriage; }
}
