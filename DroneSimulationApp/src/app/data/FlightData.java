package app.data;

import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * FlightData holds all in-memory flight-related data for drones.
 * Provides functions to extract specific analytics.
 */
public class FlightData {

    private static int page = 0;

    private static LinkedHashMap<Integer, List<DroneDynamics>> futureDronePositions = new LinkedHashMap<>();
    private static List<DroneDynamics> currentDronePositions = new ArrayList<>();
    private static LinkedHashMap<Integer, List<DroneDynamics>> pastDronePositions = new LinkedHashMap<>();

    /**
     * Loads drone positions of the next 10 pages. Stores them in HashMap, grouped by drone ID.
     */
    public void loadDronePositions() {
        List<DroneDynamics> dynamics = DataRepository.getInstance().getAllDynamics(page);
        LinkedHashMap<Integer, List<DroneDynamics>> tmp;
        tmp = dynamics.stream()
                .collect(Collectors.groupingBy(d ->
                                DataRepository
                                        .getInstance()
                                        .getDroneById(d.getDrone()).getId(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
        for (var entry : tmp.entrySet()) {
            if (entry.getValue() != null) {
                futureDronePositions
                        .computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                        .addAll(entry.getValue());
            }
        }
        changePageForward();
        page = page + 10;
    }

    /**
     * Resets the flight data to its initial state (page 0).
     */
    public void resetData() {
        page = 0;
        futureDronePositions.clear();
    }

    public List<DroneDynamics> getCurrentDronePositions() {
        return currentDronePositions;
    }

    /**
     * Changes the current page to the next page, by loading the first instance
     * of each drone from the futureDronePositions HashMap.
     */

    public void changePageForward() {
        if (!currentDronePositions.isEmpty()) {
            for (DroneDynamics d : currentDronePositions) {
                pastDronePositions
                        .computeIfAbsent(
                                DataRepository
                                        .getInstance()
                                        .getDroneById(d.getDrone())
                                        .getId(),
                                k -> new ArrayList<>())
                        .addFirst(d);
            }
        }

        List<DroneDynamics> drones = new ArrayList<>();
        for (var entry : futureDronePositions.entrySet()) {
            List<DroneDynamics> value = entry.getValue();

            if (!value.isEmpty()) {
                DroneDynamics index = value.getFirst();
                drones.add(index);
            }
        }
        currentDronePositions = drones;

        for (var entry : futureDronePositions.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                entry.getValue().removeFirst();
            }
        }
    }

    /**
     * Changes the current page to the previous page, by loading the last instance
     */

    public void changePageBackward() {
        if (!currentDronePositions.isEmpty()) {
            for (DroneDynamics d : currentDronePositions) {
                futureDronePositions
                        .computeIfAbsent(
                                DataRepository
                                        .getInstance()
                                        .getDroneById(d.getDrone())
                                        .getId(),
                                k -> new ArrayList<>())
                        .addFirst(d);
            }
        }

        List<DroneDynamics> drones = new ArrayList<>();
        for (var entry : pastDronePositions.entrySet()) {
            List<DroneDynamics> value = entry.getValue();

            if (!value.isEmpty()) {
                DroneDynamics index = value.getFirst();
                drones.add(index);
            }
        }
        currentDronePositions = drones;

        for (var entry : pastDronePositions.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                entry.getValue().removeFirst();
            }
        }
    }

    /**
     * Get all DroneDynamics-Data of every Drone
     * that corresponds to the given DroneType.

     * @param type Drohnen-Typ-Code (example SkSk..., HoHs..., etc.)
     * @return Map with Key = Drone-Label (example "Eachine: E58 : AAA2-440.0"),
     *         Value = List of all Dynamics of the given DroneType.
     */
    public Map<String, List<DroneDynamics>> getDynamicsByType(String type) {
        Map<String, List<DroneDynamics>> dronesByType = new LinkedHashMap<>();

        // check all saved Dynamics in futureDronePositions and pastDronePositions
        List<Map<Integer, List<DroneDynamics>>> allDataMaps = List.of(
                futureDronePositions,
                pastDronePositions
        );

        for (Map<Integer, List<DroneDynamics>> dataMap : allDataMaps) {
            for (Map.Entry<Integer, List<DroneDynamics>> entry : dataMap.entrySet()) {
                Integer droneId = entry.getKey();
                Drone drone = DataRepository.getInstance().getDroneById(droneId.toString());
                if (drone != null && drone.getDroneType().equals(type)) {
                    var dt = DataRepository.getInstance().getDroneTypeByUrl(drone.getDroneType());
                    String droneLabel = dt.getManufacturer() + ": " + dt.getTypename() + " : " + drone.getSerialNumber();
                    dronesByType.put(droneLabel, entry.getValue());
                }
            }
        }

        // check for current position
        for (DroneDynamics dyn : currentDronePositions) {
            Drone drone = DataRepository.getInstance().getDroneById(dyn.getDrone());
            if (drone != null && drone.getDroneType().equals(type)) {
                var dt = DataRepository.getInstance().getDroneTypeByUrl(drone.getDroneType());
                String key = dt.getManufacturer() + ": " + dt.getTypename() + " : " + drone.getSerialNumber();
                dronesByType
                        .computeIfAbsent(key, k -> new ArrayList<>())
                        .add(dyn);
            }
        }

        return dronesByType;
    }
}

