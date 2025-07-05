package app.model; 

import org.json.JSONObject;


/** Static properties of a single drone. */
public class Drone {
    private final int id;
    private final String droneType;
    private final String created;
    private final String serialNumber;
    private final double carriageWeight;
    private final String carriageType;

    public Drone(JSONObject o) {
        this.id = o.getInt("id");
        this.droneType = o.getString("dronetype");
        this.created = o.getString("created");
        this.serialNumber = o.getString("serialnumber");
        this.carriageWeight = o.getDouble("carriage_weight");
        this.carriageType = o.getString("carriage_type");
    }
    
    public int getId() { return id; }
    public String getDroneType() { return droneType; }
    public String getCreated() { return created; }
    public String getSerialNumber() { return serialNumber; }
    public double getCarriageWeight() { return carriageWeight; }
    public String getCarriageType() { return carriageType; }

}