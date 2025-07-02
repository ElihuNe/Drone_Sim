package app.data;

import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;

import java.util.List;

/**
 * Interface for providing access to drone-related data.
 * Implementing classes must support access to drones, drone types, and dynamic data.
 */
public interface IDataProvider {

    /**
     * Returns all drones.
     *
     * @return list of drones
     */
    List<Drone> getAllDrones();

    /**
     * Returns all drone types.
     *
     * @return list of drone types
     */
    List<DroneType> getAllDroneTypes();

    /**
     * Returns drone dynamics for the given page.
     * Pagination is used to avoid large payloads.
     *
     * @param page page index (e.g. 0, 1, 2...)
     * @return list of drone dynamics for the page
     */
    List<DroneDynamics> getDroneDynamics(int page);

    /**
     * Finds a drone by ID string.
     * Implementations may extract the numeric ID from the string.
     *
     * @param id string that contains the drone ID (e.g. from a URL)
     * @return matching drone or null if not found
     */
    Drone getDroneById(String id);
}
