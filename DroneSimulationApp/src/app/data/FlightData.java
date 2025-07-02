package app.data;

import app.model.Drone;
import app.model.DroneDynamics;

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

    public void resetData() {
        page = 0;
        futureDronePositions.clear();
    }

    public List<DroneDynamics> getCurrentDronePositions() {
        return currentDronePositions;
    }

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
     * Liefert alle DroneDynamics-Daten aller Drohnen,
     * die dem angegebenen Drohnentyp entsprechen.
     *
     * @param type Drohnen-Typ-Code (z. B. SkSk..., HoHs..., etc.)
     * @return Map mit Key = Drohnen-Label (z. B. "Eachine: E58 : AAA2-440.0"),
     *         Value = Liste aller Dynamics-Einträge dieser Drohne
     */
    public Map<String, List<DroneDynamics>> getDynamicsByType(String type) {
        Map<String, List<DroneDynamics>> dronesByType = new LinkedHashMap<>();

        // alle gespeicherten Dynamics in ALLEN Pages prüfen
        List<Map<Integer, List<DroneDynamics>>> allDataMaps = List.of(
                futureDronePositions,
                pastDronePositions
        );

        for (Map<Integer, List<DroneDynamics>> dataMap : allDataMaps) {
            for (Map.Entry<Integer, List<DroneDynamics>> entry : dataMap.entrySet()) {
                Integer droneId = entry.getKey();
                Drone drone = DataRepository.getInstance().getDroneById(droneId.toString());
                if (drone != null && drone.getDroneType().equals(type)) {
                    var dt = app.api.API.getDroneTypeInstance(drone.getDroneType());
                    String droneLabel = dt.getManufacturer() + ": " + dt.getTypename() + " : " + drone.getSerialNumber();
                    dronesByType.put(droneLabel, entry.getValue());
                }
            }
        }

        // auch aktuelle Positionen prüfen
        for (DroneDynamics dyn : currentDronePositions) {
            Drone drone = DataRepository.getInstance().getDroneById(dyn.getDrone());
            if (drone != null && drone.getDroneType().equals(type)) {
                var dt = app.api.API.getDroneTypeInstance(drone.getDroneType());
                String key = dt.getManufacturer() + ": " + dt.getTypename() + " : " + drone.getSerialNumber();
                dronesByType
                        .computeIfAbsent(key, k -> new ArrayList<>())
                        .add(dyn);
            }
        }

        return dronesByType;
    }
}

