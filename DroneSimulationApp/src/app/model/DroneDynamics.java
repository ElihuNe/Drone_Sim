package app.model; 

import org.json.JSONObject;

public class DroneDynamics {
    private final String drone;
    private final String timestamp;
    private final double speed;
    private final String alignRoll, alignPitch, alignYaw;
    private final String longitude, latitude;
    private final double batteryStatus;
    private final String lastSeen, status;

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

    public String getDrone() { return drone; }
    public String getTimestamp() { return timestamp; }
    public double getSpeed() { return speed; }
    public String getAlignRoll() { return alignRoll; }
    public String getAlignPitch() { return alignPitch; }
    public String getAlignYaw() { return alignYaw; }
    public String getLongitude() { return longitude; }
    public String getLatitude() { return latitude; }
    public double getBatteryStatus() { return batteryStatus; }
    public String getLastSeen() { return lastSeen; }
    public String getStatus() { return status; }
}
