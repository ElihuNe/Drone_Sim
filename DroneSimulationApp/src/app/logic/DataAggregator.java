package app.logic;

import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;
import java.util.*;
import java.util.stream.Collectors;

public class DataAggregator {

    public static Map<String, Double> getAverageSpeedPerType() {
        List<DroneDynamics> dynamics = DataRepository.getInstance().getAllDynamics();
        Map<String, List<DroneDynamics>> byType = dynamics.stream()
            .collect(Collectors.groupingBy(d -> DataRepository.getInstance()
            .getDroneById(d.getDrone()).getDroneType()));

        Map<String, Double> result = new HashMap<>();
        for (var entry : byType.entrySet()) {
            double avg = entry.getValue().stream().mapToDouble(DroneDynamics::getSpeed).average().orElse(0.0);
            result.put(entry.getKey(), avg);
        }
        return result;
    }

    public static Map<String, Long> getBatteryDistribution() {
        return DataRepository.getInstance().getAllDynamics().stream()
                .collect(Collectors.groupingBy(
                        d -> {
                            double b = d.getBatteryStatus();
                            if (b > 75) return "75–100%";
                            else if (b > 50) return "50–75%";
                            else if (b > 25) return "25–50%";
                            else return "0–25%";
                        }, Collectors.counting()));
    }

    public static Map<String, Integer> getTopDroneTypes() {
        Map<String, Integer> counts = new HashMap<>();
        for (Drone d : DataRepository.getInstance().getAllDrones()) {
            counts.merge(d.getDroneType(), 1, Integer::sum);
        }
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (a, b) -> b, LinkedHashMap::new));
    }
}
