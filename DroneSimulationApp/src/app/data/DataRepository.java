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


public class DataRepository implements IDataProvider {

    private static final DataRepository instance = new DataRepository();

    private final List<Drone> drones = new ArrayList<>();
    private final List<DroneType> droneTypes = new ArrayList<>();
    private final Map<Integer, Drone> droneMap = new HashMap<>();

    private final Map<Integer, List<DroneDynamics>> dynamicsCache = new HashMap<>();

    private DataRepository() {
        drones.addAll(API.getDrones());
        droneTypes.addAll(API.getDroneTypes());
        for (Drone d : drones) {
            droneMap.put(d.getId(), d);
        }
    }

    public static DataRepository getInstance() {
        return instance;
    }

    //Collections.unmodifiableList(drones)

    public List<Drone> getAllDrones() {
        return drones;
    }

    public List<DroneType> getAllDroneTypes() {
        return droneTypes;
    }

    public List<DroneDynamics> getDroneDynamics(int page) {
        if (!dynamicsCache.containsKey(page)) {
            dynamicsCache.put(page, API.getDroneDynamics(page));
        }
        return dynamicsCache.get(page);
    }

    public List<DroneDynamics> getAllDynamics() {
        List<DroneDynamics> all = new ArrayList<>();
        for (int i = 1; i <= 5; i++) { // Limited pages to avoid overloading
            all.addAll(getDroneDynamics(i));
        }
        return all;
    }

    public Drone getDroneById(String droneRef) {
        try {
            int id = Integer.parseInt(droneRef.replaceAll("[^0-9]", ""));
            return droneMap.get(id);
        } catch (Exception e) {
            return null;
        }
    }
}

