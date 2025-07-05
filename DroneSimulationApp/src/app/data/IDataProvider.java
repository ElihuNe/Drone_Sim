package app.data;

import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;

import java.util.List;

/**
 * Interface for providing access to drone-related data.
 */
public interface IDataProvider {

    List<Drone> getAllDrones();

    List<DroneType> getAllDroneTypes();

    List<DroneDynamics> getDroneDynamics(int page);

    Drone getDroneById(String id);
}