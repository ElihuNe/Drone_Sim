package app.model;

import org.json.JSONObject;

/**
 * Represents real-time dynamic data of a drone.
 * This includes values such as position, battery status, orientation and timestamp.
 */
public class DroneDynamics {

    private final String drone;           // Reference to the drone (URL or ID)
    private final String timestamp;       // Timestamp of this measurement
    private final double speed;           // Current speed of the drone
    private final String alignRoll;       // Roll angle (orientation)
    private final String alignPitch;      // Pitch angle (orientation)
    private final String alignYaw;        // Yaw angle (orientation)
    private final String longitude;       // Longitude position
    private final String latitude;        // Latitude position
    private final double batteryStatus;   // Battery status (raw value, e.g. mV or %)
    private final String lastSeen;        // Last seen timestamp
    private final String status;          // Operational status ("ON", "OFF", etc.)

    /**
     * Constructs a DroneDynamics object from JSON data.
     * Uses default values if some fields are missing.
     *
     * @param o JSON object containing dynamic drone info
     */
    public DroneDynamics(JSONObject o) {
        this.drone = o.getString("drone");
        this.timestamp = o.optString("timestamp", "-");
        this.speed = o.optDouble("speed", 0.0);
        this.alignRoll = o.optString("align_roll", "-");
        this.alignPitch = o.optString("align_pitch", "-");
        this.alignYaw = o.optString("align_yaw", "-");
        this.longitude = o.optString("longitude", "-");
        this.latitude = o.optString("latitude", "-");
        this.batteryStatus = o.optDouble("battery_status", 0.0);
        this.lastSeen = o.optString("last_seen", "-");
        this.status = o.optString("status", "-");
    }

    /** @return reference to the drone (usually a URL or ID) */
    public String getDrone() { return drone; }

    /** @return timestamp of the data record */
    public String getTimestamp() { return timestamp; }

    /** @return drone's current speed */
    public double getSpeed() { return speed; }

    /** @return roll angle of the drone */
    public String getAlignRoll() { return alignRoll; }

    /** @return pitch angle of the drone */
    public String getAlignPitch() { return alignPitch; }

    /** @return yaw angle of the drone */
    public String getAlignYaw() { return alignYaw; }

    /** @return current longitude */
    public String getLongitude() { return longitude; }

    /** @return current latitude */
    public String getLatitude() { return latitude; }

    /** @return battery status value */
    public double getBatteryStatus() { return batteryStatus; }

    /** @return timestamp when drone was last seen */
    public String getLastSeen() { return lastSeen; }

    /** @return current operational status of the drone */
    public String getStatus() { return status; }
}