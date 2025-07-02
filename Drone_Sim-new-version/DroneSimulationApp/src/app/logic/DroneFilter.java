package app.logic;

import java.util.List;
import java.util.stream.Collectors;

import app.data.DataRepository;
import app.model.Drone;

/**
 * Provides simple filters for drone data.
 * Allows filtering the drone list by type or status.
 */
public class DroneFilter {

    /**
     * Filters drones by their type.
     * The comparison is case-insensitive.
     *
     * @param type drone type to filter by (e.g. "Quadcopter")
     * @return list of matching drones
     */
    public static List<Drone> filterByType(String type) {
        return DataRepository.getInstance()
                .getAllDrones()
                .stream()
                .filter(d -> d.getDroneType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    /**
     * Filters drones by their status.
     * The comparison is case-insensitive.
     *
     * @param status drone status (e.g. "ON", "OFF", "N/A")
     * @return list of matching drones
     */
    public static List<Drone> filterByStatus(String status) {
        return DataRepository.getInstance()
                .getAllDrones()
                .stream()
                .filter(d -> d.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
}
