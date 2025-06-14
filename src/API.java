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

public class API {
    private static final String BASE_URL = "http://dronesim.facets-labs.com";
    private static final String API_DRONE_TYPES = "/api/dronetypes/";
    private static final String API_DRONES = "/api/drones/";
    private static final String API_DRONE_DYNAMICS = "/api/dronedynamics/";
    
    private static final String TOKEN = "Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52";

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

        while (running) {
            System.out.println("\n1. List drones");
            System.out.println("2. Get specific drone details");
            System.out.println("3. List drone types");
            System.out.println("4. List drone dynamics");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    getDrones();
                    break;
                case 2:
                    System.out.print("Enter drone ID: ");
                    String droneId = scanner.nextLine();
                    getSpecificDrone(droneId);
                    break;
                case 3:
                    getDroneTypes();
                    break;
                case 4:
                    getDroneDynamics(1);
                    break;
                case 5:
                    running = false;
                    break;
            }
        }
        scanner.close();
    }

    public static List<DroneType> getDroneTypes() {
        try {
            String currentUrl = BASE_URL + API_DRONE_TYPES + "?format=json";
            List<DroneType> lastResponse = new ArrayList<>();
            while (currentUrl != null && !currentUrl.isEmpty()) {
                String response = sendGetRequest(currentUrl);
                ApiResponse<DroneType> apiResponse = parseDroneTypesResponse(response);
                for(DroneType droneType : apiResponse.results) {
                    lastResponse.add(droneType);
                }
                /*for (DroneType droneType : apiResponse.results) {
                    System.out.println(droneType);
                }*/
                currentUrl = apiResponse.next;
            }
            return lastResponse;
        } catch (IOException e) {}
        return null;
    }

    public static List<Drone> getDrones() {
        try {
            String currentUrl = BASE_URL + API_DRONES + "?format=json";
            List<Drone> lastResponse = new ArrayList<>();
            while (currentUrl != null && !currentUrl.isEmpty()) {
                String response = sendGetRequest(currentUrl);
                ApiResponse<Drone> apiResponse = parseDronesResponse(response);
                for (Drone drone : apiResponse.results) {
                    lastResponse.add(drone);
                }
                /*for (Drone drone : apiResponse.results) {
                    System.out.println(drone);
                }*/
                currentUrl = apiResponse.next;
            }
            return lastResponse;
        } catch (IOException e) {}
        return null;
    }

    private static void getSpecificDrone(String droneId) {
        try {
            String url = BASE_URL + API_DRONES + droneId + "/?format=json";
            String response = sendGetRequest(url);
            Drone drone = parseSingleDrone(response);
            System.out.println(drone);
        } catch (IOException e) {}
    }

    public static List<DroneDynamics> getDroneDynamics(int PageNum) {
        int i = 0;
        try {
            String currentUrl = BASE_URL + API_DRONE_DYNAMICS + "?format=json";
            List<DroneDynamics> lastResponse = null;
            while (currentUrl != null && !currentUrl.isEmpty() && i < PageNum) {
                String response = sendGetRequest(currentUrl);
                ApiResponse<DroneDynamics> apiResponse = parseDroneDynamicsResponse(response);
                /*for (DroneDynamics dynamics : apiResponse.results) {
                    System.out.println(dynamics);
                }*/
                currentUrl = apiResponse.next;
                lastResponse = apiResponse.results;
                i++;
            }
            return lastResponse;
        } catch (IOException e) {}
        return null;
    }

    private static ApiResponse<DroneType> parseDroneTypesResponse(String json) {
        ApiResponse<DroneType> response = new ApiResponse<>();
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
                DroneType droneType = parseDroneType(objectJson);
                response.results.add(droneType);
            }
        }
        return response;
    }

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

    private static Drone parseSingleDrone(String json) {
        return parseDrone(json);
    }

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

    private static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestProperty("Authorization", TOKEN);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:138.0) Gecko/20100101 Firefox/138.0");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        }

        throw new IOException();
    }
}