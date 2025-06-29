package app.logic;
import java.util.List;
import java.util.stream.Collectors;

import app.data.DataRepository;
import app.model.Drone;


public class DroneFilter {

    public static List<Drone> filterByType(String type) {
        return DataRepository.getInstance().getAllDrones().stream()
                .filter(d -> d.getDroneType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    public static List<Drone> filterByStatus(String status) {
        return DataRepository.getInstance().getAllDrones().stream()
                .filter(d -> d.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }
}
