package app.data;

import app.api.API;
import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton class that stores and provides access to drone data.
 */
public class DataRepository implements IDataProvider {

    // Singleton instance of the repository
    private static final DataRepository instance = new DataRepository();

    // Internal storage for drones and their types
    private final List<Drone> drones = new ArrayList<>();
    private final List<DroneType> droneTypes = new ArrayList<>();

    // Maps drone ID to its corresponding Drone object for fast lookup
    private final Map<Integer, Drone> droneMap = new HashMap<>();

    // Caches dynamic drone data per page to reduce API calls
    private final Map<Integer, List<DroneDynamics>> dynamicsCache = new HashMap<>();

    /**
     * Private constructor that loads drone and type data once at startup.
     */
    private DataRepository() {
        drones.addAll(API.getDrones());           // Load all individual drones
        droneTypes.addAll(API.getDroneTypes());   // Load all drone types

        // Map each drone ID to its object for quick access
        for (Drone d : drones) {
            droneMap.put(d.getId(), d);
        }
    }

    /**
     * Returns the singleton instance.
     *
     * @return shared DataRepository instance
     */
    public static DataRepository getInstance() {
        return instance;
    }

    /**
     * Returns all drones.
     *
     * @return list of drones
     */
    public List<Drone> getAllDrones() {
        return drones;
    }

    /**
     * Returns all drone types.
     *
     * @return list of drone types
     */
    public List<DroneType> getAllDroneTypes() {
        return droneTypes;
    }

    /**
     * Returns dynamic data for one page. Uses cache to avoid redundant requests.
     *
     * @param page page index
     * @return list of dynamics for the page
     */
    public List<DroneDynamics> getDroneDynamics(int page) {
        // Fetch from cache or API if not already cached
        if (!dynamicsCache.containsKey(page)) {
            dynamicsCache.put(page, API.getDroneDynamics(page));
        }
        return dynamicsCache.get(page);
    }

    /**
     * Returns combined drone dynamics from several pages.
     *
     * @return list of all dynamics (limited)
     */
    public List<DroneDynamics> getAllDynamics(int page) {
        List<DroneDynamics> all = new ArrayList<>();

        // Limit the number of pages to avoid overloading the API
        for (int i = page; i < page+10; i++) {
            all.addAll(getDroneDynamics(i));
        }

        return all;
    }

    /**
     * Tries to find a drone by ID from a URL string.
     *
     * @param droneRef string containing drone reference
     * @return drone or null if parsing fails
     */
    public Drone getDroneById(String droneRef) {
        try {
            // Extract numeric ID from URL or reference string
            int id = Integer.parseInt(droneRef.replaceAll("[^0-9]", ""));
            return droneMap.get(id);
        } catch (Exception e) {
            return null; // In case parsing fails or ID not found
        }
    }
    
    /**
     * Returns a drone by its serial number.
     *
     **/
    
    public Drone getDroneBySerial(String serial) {
        for (Drone d : getAllDrones()) {
            if (serial.equals(d.getSerialNumber())) {
                return d;
            }
        }
        return null;
    }

}
