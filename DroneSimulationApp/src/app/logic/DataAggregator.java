package app.logic;

import app.data.DataRepository;
import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides helper methods to compute aggregated drone data.
 */
public class DataAggregator {

    /**
     * Calculates the average speed for each drone type.
     * Uses drone dynamics data grouped by drone type.
     *
     * @return map of drone type to average speed
     */
    public static Map<String, Double> getAverageSpeedPerType() {
        List<DroneDynamics> dynamics = DataRepository.getInstance().getAllDynamics(1); //prelimenary page number, gotta fix this

        // Group dynamics data by drone type using drone reference resolution
        Map<String, List<DroneDynamics>> byType = dynamics.stream()
            .collect(Collectors.groupingBy(d -> 
                DataRepository.getInstance()
                    .getDroneById(d.getDrone())
                    .getDroneType()
            ));

        // Calculate average speed for each group
        Map<String, Double> result = new HashMap<>();
        for (var entry : byType.entrySet()) {
            double avg = entry.getValue().stream()
                    .mapToDouble(DroneDynamics::getSpeed)
                    .average()
                    .orElse(0.0);
            result.put(entry.getKey(), avg);
        }

        return result;
    }

    /**
     * Groups drones into battery level ranges.
     * Useful for displaying battery health distribution.
     *
     * @return map of battery range label to count
     */
    public static Map<String, Long> getBatteryDistribution() {
        return DataRepository.getInstance()
                .getAllDynamics(1) 
                .stream()
                .collect(Collectors.groupingBy(
                        d -> {
                            double b = d.getBatteryStatus();
                            if (b > 75) return "75–100%";
                            else if (b > 50) return "50–75%";
                            else if (b > 25) return "25–50%";
                            else return "0–25%";
                        },
                        Collectors.counting()
                ));
    }

    /**
     * Groups drones into battery level ranges,
     * but only for a filtered set of drones.
     *
     * @param dronesByType map from drone labels to their dynamics lists
     * @return map of battery range label to count
     */
    public static Map<String, Long> getBatteryDistributionFiltered(Map<String, List<DroneDynamics>> dronesByType) {
        Map<String, Long> result = new LinkedHashMap<>();
        for (List<DroneDynamics> dynList : dronesByType.values()) {
            for (DroneDynamics dyn : dynList) {
                double b = dyn.getBatteryStatus();
                String range;
                if (b > 75) range = "75–100%";
                else if (b > 50) range = "50–75%";
                else if (b > 25) range = "25–50%";
                else range = "0–25%";
                result.merge(range, 1L, Long::sum);
            }
        }
        return result;
    }

    /**
     * Finds the most common drone types in the dataset.
     * Counts how many drones belong to each type and returns the top 5.
     *
     * @return map of top 5 drone types with their counts (sorted descending)
     */
    public static Map<String, Integer> getTopDroneTypes() {
        Map<String, Integer> counts = new HashMap<>();

        // Count drones per type
        for (Drone d : DataRepository.getInstance().getAllDrones()) {
            counts.merge(d.getDroneType(), 1, Integer::sum);
        }

        // Sort by count and limit to top 5
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }
}
