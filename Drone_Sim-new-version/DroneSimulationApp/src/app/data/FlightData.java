package app.data;

import app.model.DroneDynamics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FlightData {

    private static int page = 0;

    private static LinkedHashMap<Integer, List<DroneDynamics>> futureDronePositions = new LinkedHashMap<>();
    private static List<DroneDynamics> currentDronePositions = new ArrayList<>();
    private static LinkedHashMap<Integer, List<DroneDynamics>> pastDronePositions = new LinkedHashMap<>();

    public void loadDronePositions(){
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
                futureDronePositions.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).addAll(entry.getValue());
            }
        }
        changePageForward();

        page = page + 10;

    }

    public void resetData(){
        page = 0;
        futureDronePositions.clear();
    }

    public List<DroneDynamics> getCurrentDronePositions (){
        return currentDronePositions;
    }

    public void changePageForward(){
        if(!currentDronePositions.isEmpty()) {
            for(DroneDynamics d : currentDronePositions) {
                pastDronePositions.computeIfAbsent(DataRepository
                        .getInstance()
                        .getDroneById(d.getDrone())
                        .getId(), k -> new ArrayList<>()).addFirst(d);
            }
        }

        List<DroneDynamics> drones = new ArrayList<>();
        for (var entry : futureDronePositions.entrySet()) {
            List<DroneDynamics> value = entry.getValue();

            if(!value.isEmpty()) {
                DroneDynamics index = value.getFirst();
                drones.add(index);
            }
        }
        currentDronePositions = drones;

        for (var entry : futureDronePositions.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                entry.getValue().removeFirst();
            }
        }
    }

    public void changePageBackward(){
        if(!currentDronePositions.isEmpty()) {
            for(DroneDynamics d : currentDronePositions) {
                futureDronePositions.computeIfAbsent(DataRepository
                        .getInstance()
                        .getDroneById(d.getDrone())
                        .getId(), k -> new ArrayList<>()).addFirst(d);
            }
        }

        List<DroneDynamics> drones = new ArrayList<>();
        for (var entry : pastDronePositions.entrySet()) {
            List<DroneDynamics> value = entry.getValue();

            if(!value.isEmpty()) {
                DroneDynamics index = value.getFirst();
                drones.add(index);
            }
        }
        currentDronePositions = drones;

        for (var entry : pastDronePositions.entrySet()) {
            if(!entry.getValue().isEmpty()) {
                entry.getValue().removeFirst();
            }
        }

    }

}
