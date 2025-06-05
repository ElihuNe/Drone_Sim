import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Enhanced Drone API Client with JSON parsing
 * Interacts with three main endpoints: dronetypes, drones, and dronedynamics
 */
public class API {
    // Base URL and API endpoints
    private static final String BASE_URL = "http://dronesim.facets-labs.com";
    private static final String API_DRONE_TYPES = "/api/dronetypes/";
    private static final String API_DRONES = "/api/drones/";
    private static final String API_DRONE_DYNAMICS = "/api/dronedynamics/";

    // Authentication token
    private static final String TOKEN = "Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52";

    // Data Models
    static class DroneType {
        int id;
        String manufacturer;
        String typename;
        double weight;
        double maxSpeed;
        double batteryCapacity;
        double controlRange;
        double maxCarriage;

        @Override
        public String toString() {
            return String.format(
                    "DroneType ID: %d\n" +
                            "  Manufacturer: %s\n" +
                            "  Type Name: %s\n" +
                            "  Weight: %.2f\n" +
                            "  Max Speed: %.2f\n" +
                            "  Battery Capacity: %.2f\n" +
                            "  Control Range: %.2f\n" +
                            "  Max Carriage: %.2f\n",
                    id, manufacturer, typename, weight, maxSpeed, batteryCapacity, controlRange, maxCarriage
            );
        }
    }

    static class Drone {
        int id;
        String droneType;
        String created;
        String serialNumber;
        double carriageWeight;
        String carriageType;

        @Override
        public String toString() {
            return String.format(
                    "Drone ID: %d\n" +
                            "  Drone Type: %s\n" +
                            "  Created: %s\n" +
                            "  Serial Number: %s\n" +
                            "  Carriage Weight: %.2f\n" +
                            "  Carriage Type: %s\n",
                    id, droneType, created, serialNumber, carriageWeight, carriageType
            );
        }
    }

    static class DroneDynamics {
        String drone;
        String timestamp;
        double speed;
        String alignRoll;
        String alignPitch;
        String alignYaw;
        String longitude;
        String latitude;
        double batteryStatus;
        String lastSeen;
        String status;

        @Override
        public String toString() {
            return String.format(
                    "Drone Dynamics:\n" +
                            "  Drone: %s\n" +
                            "  Timestamp: %s\n" +
                            "  Speed: %.2f\n" +
                            "  Align Roll: %s\n" +
                            "  Align Pitch: %s\n" +
                            "  Align Yaw: %s\n" +
                            "  Longitude: %s\n" +
                            "  Latitude: %s\n" +
                            "  Battery Status: %.2f\n" +
                            "  Last Seen: %s\n" +
                            "  Status: %s\n",
                    drone, timestamp, speed, alignRoll, alignPitch, alignYaw,
                    longitude, latitude, batteryStatus, lastSeen, status
            );
        }
    }

    static class ApiResponse<T> {
        int count;
        String next;
        String previous;
        List<T> results;

        ApiResponse() {
            results = new ArrayList<>();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Enhanced Drone API Client ===");

        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. List drone types");
            System.out.println("2. List drones");
            System.out.println("3. Get specific drone details");
            System.out.println("4. List drone dynamics");
            System.out.println("5. Get specific drone dynamics");
            System.out.println("6. Exit");
            System.out.print("Enter your choice (1-6): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    getDroneTypes();
                    break;
                case 2:
                    getDrones();
                    break;
                case 3:
                    System.out.print("Enter drone ID: ");
                    String droneId = scanner.nextLine();
                    getSpecificDrone(droneId);
                    break;
                case 4:
                    getDroneDynamics();
                    break;
                case 5:
                    System.out.print("Enter drone ID for dynamics: ");
                    String dynamicsId = scanner.nextLine();
                    getSpecificDroneDynamics(dynamicsId);
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting the application. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    /**
     * GET /api/dronetypes/
     * Retrieves all drone types and parses them
     */
    private static void getDroneTypes() {
        try {
            String url = BASE_URL + API_DRONE_TYPES + "?format=json";
            String response = sendGetRequest(url);

            System.out.println("\n=== DRONE TYPES ===");

            ApiResponse<DroneType> apiResponse = parseDroneTypesResponse(response);

            System.out.println("Total Count: " + apiResponse.count);
            if (apiResponse.next != null) {
                System.out.println("Next Page: " + apiResponse.next);
            }
            if (apiResponse.previous != null) {
                System.out.println("Previous Page: " + apiResponse.previous);
            }

            System.out.println("\nDrone Types:");
            for (DroneType droneType : apiResponse.results) {
                System.out.println(droneType);
            }

        } catch (IOException e) {
            System.out.println("Error fetching drone types: " + e.getMessage());
        }
    }

    /**
     * GET /api/drones/
     * Retrieves all drones and parses them
     */
    private static void getDrones() {
        try {
            String url = BASE_URL + API_DRONES + "?format=json";
            String response = sendGetRequest(url);

            System.out.println("\n=== DRONES ===");

            ApiResponse<Drone> apiResponse = parseDronesResponse(response);

            System.out.println("Total Count: " + apiResponse.count);
            if (apiResponse.next != null) {
                System.out.println("Next Page: " + apiResponse.next);
            }
            if (apiResponse.previous != null) {
                System.out.println("Previous Page: " + apiResponse.previous);
            }

            System.out.println("\nDrones:");
            for (Drone drone : apiResponse.results) {
                System.out.println(drone);
            }

        } catch (IOException e) {
            System.out.println("Error fetching drones: " + e.getMessage());
        }
    }

    /**
     * GET /api/drones/{id}/
     * Retrieves a specific drone by ID and parses it
     */
    private static void getSpecificDrone(String droneId) {
        try {
            String url = BASE_URL + API_DRONES + droneId + "/?format=json";
            String response = sendGetRequest(url);

            System.out.println("\n=== DRONE DETAILS (ID: " + droneId + ") ===");

            Drone drone = parseSingleDrone(response);
            System.out.println(drone);

        } catch (IOException e) {
            System.out.println("Error fetching drone details: " + e.getMessage());
        }
    }

    /**
     * GET /api/dronedynamics/
     * Retrieves all drone dynamics and parses them
     */
    private static void getDroneDynamics() {
        try {
            String url = BASE_URL + API_DRONE_DYNAMICS + "?format=json";
            String response = sendGetRequest(url);

            System.out.println("\n=== DRONE DYNAMICS ===");

            ApiResponse<DroneDynamics> apiResponse = parseDroneDynamicsResponse(response);

            System.out.println("Total Count: " + apiResponse.count);
            if (apiResponse.next != null) {
                System.out.println("Next Page: " + apiResponse.next);
            }
            if (apiResponse.previous != null) {
                System.out.println("Previous Page: " + apiResponse.previous);
            }

            System.out.println("\nDrone Dynamics:");
            for (DroneDynamics dynamics : apiResponse.results) {
                System.out.println(dynamics);
            }

        } catch (IOException e) {
            System.out.println("Error fetching drone dynamics: " + e.getMessage());
        }
    }

    /**
     * GET /api/dronedynamics/{id}/
     * Retrieves specific drone dynamics by ID and parses them
     */
    private static void getSpecificDroneDynamics(String droneId) {
        try {
            String url = BASE_URL + API_DRONE_DYNAMICS + droneId + "/?format=json";
            String response = sendGetRequest(url);

            System.out.println("\n=== DRONE DYNAMICS (ID: " + droneId + ") ===");

            DroneDynamics dynamics = parseSingleDroneDynamics(response);
            System.out.println(dynamics);

        } catch (IOException e) {
            System.out.println("Error fetching drone dynamics: " + e.getMessage());
        }
    }

    /**
     * Parse drone types response
     */
    private static ApiResponse<DroneType> parseDroneTypesResponse(String json) {
        ApiResponse<DroneType> response = new ApiResponse<>();

        // Parse count
        response.count = extractInt(json, "\"count\":\\s*(\\d+)");

        // Parse next and previous URLs
        response.next = extractString(json, "\"next\":\\s*\"([^\"]+)\"");
        response.previous = extractString(json, "\"previous\":\\s*\"([^\"]+)\"");

        // Parse results array
        Pattern resultsPattern = Pattern.compile("\"results\":\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher resultsMatcher = resultsPattern.matcher(json);

        if (resultsMatcher.find()) {
            String resultsContent = resultsMatcher.group(1);
            Pattern objectPattern = Pattern.compile("\\{([^{}]+(?:\\{[^{}]*\\}[^{}]*)*)\\}");
            Matcher objectMatcher = objectPattern.matcher(resultsContent);

            while (objectMatcher.find()) {
                String objectJson = "{" + objectMatcher.group(1) + "}";
                DroneType droneType = parseDroneType(objectJson);
                response.results.add(droneType);
            }
        }

        return response;
    }

    /**
     * Parse single drone type object
     */
    private static DroneType parseDroneType(String json) {
        DroneType droneType = new DroneType();
        droneType.id = extractInt(json, "\"id\":\\s*(\\d+)");
        droneType.manufacturer = extractString(json, "\"manufacturer\":\\s*\"([^\"]+)\"");
        droneType.typename = extractString(json, "\"typename\":\\s*\"([^\"]+)\"");
        droneType.weight = extractDouble(json, "\"weight\":\\s*([\\d.]+)");
        droneType.maxSpeed = extractDouble(json, "\"max_speed\":\\s*([\\d.]+)");
        droneType.batteryCapacity = extractDouble(json, "\"battery_capacity\":\\s*([\\d.]+)");
        droneType.controlRange = extractDouble(json, "\"control_range\":\\s*([\\d.]+)");
        droneType.maxCarriage = extractDouble(json, "\"max_carriage\":\\s*([\\d.]+)");
        return droneType;
    }

    /**
     * Parse drones response
     */
    private static ApiResponse<Drone> parseDronesResponse(String json) {
        ApiResponse<Drone> response = new ApiResponse<>();

        response.count = extractInt(json, "\"count\":\\s*(\\d+)");
        response.next = extractString(json, "\"next\":\\s*\"([^\"]+)\"");
        response.previous = extractString(json, "\"previous\":\\s*\"([^\"]+)\"");

        Pattern resultsPattern = Pattern.compile("\"results\":\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher resultsMatcher = resultsPattern.matcher(json);

        if (resultsMatcher.find()) {
            String resultsContent = resultsMatcher.group(1);
            Pattern objectPattern = Pattern.compile("\\{([^{}]+(?:\\{[^{}]*\\}[^{}]*)*)\\}");
            Matcher objectMatcher = objectPattern.matcher(resultsContent);

            while (objectMatcher.find()) {
                String objectJson = "{" + objectMatcher.group(1) + "}";
                Drone drone = parseDrone(objectJson);
                response.results.add(drone);
            }
        }

        return response;
    }

    /**
     * Parse single drone object
     */
    private static Drone parseDrone(String json) {
        Drone drone = new Drone();
        drone.id = extractInt(json, "\"id\":\\s*(\\d+)");
        drone.droneType = extractString(json, "\"dronetype\":\\s*\"([^\"]+)\"");
        drone.created = extractString(json, "\"created\":\\s*\"([^\"]+)\"");
        drone.serialNumber = extractString(json, "\"serialnumber\":\\s*\"([^\"]+)\"");
        drone.carriageWeight = extractDouble(json, "\"carriage_weight\":\\s*([\\d.]+)");
        drone.carriageType = extractString(json, "\"carriage_type\":\\s*\"([^\"]+)\"");
        return drone;
    }

    /**
     * Parse single drone from individual endpoint
     */
    private static Drone parseSingleDrone(String json) {
        return parseDrone(json);
    }

    /**
     * Parse drone dynamics response
     */
    private static ApiResponse<DroneDynamics> parseDroneDynamicsResponse(String json) {
        ApiResponse<DroneDynamics> response = new ApiResponse<>();

        response.count = extractInt(json, "\"count\":\\s*(\\d+)");
        response.next = extractString(json, "\"next\":\\s*\"([^\"]+)\"");
        response.previous = extractString(json, "\"previous\":\\s*\"([^\"]+)\"");

        Pattern resultsPattern = Pattern.compile("\"results\":\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher resultsMatcher = resultsPattern.matcher(json);

        if (resultsMatcher.find()) {
            String resultsContent = resultsMatcher.group(1);
            Pattern objectPattern = Pattern.compile("\\{([^{}]+(?:\\{[^{}]*\\}[^{}]*)*)\\}");
            Matcher objectMatcher = objectPattern.matcher(resultsContent);

            while (objectMatcher.find()) {
                String objectJson = "{" + objectMatcher.group(1) + "}";
                DroneDynamics dynamics = parseDroneDynamics(objectJson);
                response.results.add(dynamics);
            }
        }

        return response;
    }

    /**
     * Parse single drone dynamics object
     */
    private static DroneDynamics parseDroneDynamics(String json) {
        DroneDynamics dynamics = new DroneDynamics();
        dynamics.drone = extractString(json, "\"drone\":\\s*\"([^\"]+)\"");
        dynamics.timestamp = extractString(json, "\"timestamp\":\\s*\"([^\"]+)\"");
        dynamics.speed = extractDouble(json, "\"speed\":\\s*([\\d.]+)");
        dynamics.alignRoll = extractString(json, "\"align_roll\":\\s*\"([^\"]+)\"");
        dynamics.alignPitch = extractString(json, "\"align_pitch\":\\s*\"([^\"]+)\"");
        dynamics.alignYaw = extractString(json, "\"align_yaw\":\\s*\"([^\"]+)\"");
        dynamics.longitude = extractString(json, "\"longitude\":\\s*\"([^\"]+)\"");
        dynamics.latitude = extractString(json, "\"latitude\":\\s*\"([^\"]+)\"");
        dynamics.batteryStatus = extractDouble(json, "\"battery_status\":\\s*([\\d.]+)");
        dynamics.lastSeen = extractString(json, "\"last_seen\":\\s*\"([^\"]+)\"");
        dynamics.status = extractString(json, "\"status\":\\s*\"([^\"]+)\"");
        return dynamics;
    }

    /**
     * Parse single drone dynamics from individual endpoint
     */
    private static DroneDynamics parseSingleDroneDynamics(String json) {
        return parseDroneDynamics(json);
    }

    // Utility methods for parsing JSON values
    private static String extractString(String json, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private static int extractInt(String json, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private static double extractDouble(String json, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(json);
        return m.find() ? Double.parseDouble(m.group(1)) : 0.0;
    }

    /**
     * Sends an HTTP GET request with authentication
     */
    private static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request properties
        connection.setRequestProperty("Authorization", TOKEN);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:138.0) Gecko/20100101 Firefox/138.0");

        // Get response
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            // Try to read error response
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;

            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();

            throw new IOException("HTTP Error " + responseCode + ": " + errorResponse.toString());
        }
    }
}