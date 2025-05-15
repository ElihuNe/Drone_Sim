import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Enhanced Drone Simulator Client with JSON Parsing
 * This implementation includes basic JSON parsing and better handling of pagination
 */
public class Main {
    // Base URL and API endpoints
    private static final String BASE_URL = "https://dronesim.facets-labs.com";
    private static final String API_DRONES = "/api/drones/";
    private static final String API_DRONE_TYPES = "/api/dronetypes/";
    private static final String API_DRONE_DYNAMICS = "/api/dronedynamics/";

    // Authentication token
    private static final String TOKEN = "Token 06a36f0d16c34735ba23a08de0fd6bf9e4d81e52 ";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=== Enhanced Drone Simulator Client ===");

        while (running) {
            System.out.println("\nSelect an option:");
            System.out.println("1. List all drones");
            System.out.println("2. Get drone details");
            System.out.println("3. Get drone dynamics");
            System.out.println("4. List drone types");
            System.out.println("5. Display drone status summary");
            System.out.println("6. Monitor drone in real-time");
            System.out.println("7. Exit");
            System.out.print("Enter your choice (1-7): ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    listAllDrones();
                    break;
                case 2:
                    System.out.print("Enter drone ID: ");
                    String droneId = scanner.nextLine();
                    getDroneDetails(droneId);
                    break;
                case 3:
                    System.out.print("Enter drone ID: ");
                    String dynamicsDroneId = scanner.nextLine();
                    getDroneDynamics(dynamicsDroneId);
                    break;
                case 4:
                    listDroneTypes();
                    break;
                case 5:
                    displayDroneStatusSummary();
                    break;
                case 6:
                    System.out.print("Enter drone ID to monitor: ");
                    String monitorDroneId = scanner.nextLine();
                    System.out.print("Monitoring duration in seconds: ");
                    int duration = scanner.nextInt();
                    monitorDroneInRealTime(monitorDroneId, duration);
                    break;
                case 7:
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
     * Retrieves a list of all drones with pagination support
     */
    private static void listAllDrones() {
        try {
            String url = BASE_URL + API_DRONES + "?format=json";
            List<String> drones = new ArrayList<>();

            // Handle pagination
            boolean hasMorePages = true;
            while (hasMorePages) {
                String response = sendGetRequest(url);

                // Extract drones from response
                List<String> pageDrones = extractDronesFromJson(response);
                drones.addAll(pageDrones);

                // Check for next page URL
                String nextPageUrl = extractNextPageUrl(response);
                if (nextPageUrl != null && !nextPageUrl.isEmpty()) {
                    url = nextPageUrl;
                } else {
                    hasMorePages = false;
                }
            }

            System.out.println("Found " + drones.size() + " drones");
            for (String drone : drones) {
                System.out.println(drone);
            }

        } catch (IOException e) {
            System.out.println("Error fetching drone list: " + e.getMessage());
        }
    }

    /**
     * Retrieves details for a specific drone
     */
    private static void getDroneDetails(String droneId) {
        try {
            String response = sendGetRequest(BASE_URL + API_DRONES + droneId + "/?format=json");

            // Parse important drone details
            String droneModelUrl = extractJsonValue(response, "drone_type");
            String serialNumber = extractJsonValue(response, "serial_number");
            String cargoWeight = extractJsonValue(response, "cargo_weight");
            String maxSpeed = extractJsonValue(response, "max_speed");

            System.out.println("\nDRONE DETAILS:");
            System.out.println("ID: " + droneId);
            System.out.println("Model: " + droneModelUrl);
            System.out.println("Serial Number: " + serialNumber);
            System.out.println("Cargo Weight: " + cargoWeight);
            System.out.println("Maximum Speed: " + maxSpeed);

            // Get additional information from the drone type
            if (droneModelUrl != null && !droneModelUrl.isEmpty()) {
                getDroneTypeInfo(droneModelUrl);
            }

        } catch (IOException e) {
            System.out.println("Error fetching drone details: " + e.getMessage());
        }
    }

    /**
     * Gets information about a drone type from its URL
     */
    private static void getDroneTypeInfo(String typeUrl) {
        try {
            String response = sendGetRequest(typeUrl + "?format=json");

            String manufacturer = extractJsonValue(response, "manufacturer");
            String model = extractJsonValue(response, "model");
            String description = extractJsonValue(response, "description");

            System.out.println("\nDRONE MODEL INFORMATION:");
            System.out.println("Manufacturer: " + manufacturer);
            System.out.println("Model: " + model);
            System.out.println("Description: " + description);

        } catch (IOException e) {
            System.out.println("Error fetching drone type information: " + e.getMessage());
        }
    }

    /**
     * Retrieves dynamic data for a specific drone
     */
    private static void getDroneDynamics(String droneId) {
        try {
            String response = sendGetRequest(BASE_URL + API_DRONE_DYNAMICS + droneId + "/?format=json");

            // Parse dynamic data
            String speed = extractJsonValue(response, "speed");
            String latitude = extractJsonValue(response, "latitude");
            String longitude = extractJsonValue(response, "longitude");
            String batteryStatus = extractJsonValue(response, "battery_status");
            String lastSeen = extractJsonValue(response, "last_seen");
            String status = extractJsonValue(response, "status");

            System.out.println("\nDRONE DYNAMICS:");
            System.out.println("ID: " + droneId);
            System.out.println("Speed: " + speed);
            System.out.println("Position: " + latitude + ", " + longitude);
            System.out.println("Battery: " + batteryStatus);
            System.out.println("Last Seen: " + lastSeen);
            System.out.println("Status: " + status);

        } catch (IOException e) {
            System.out.println("Error fetching drone dynamics: " + e.getMessage());
        }
    }

    /**
     * Retrieves a list of all drone types
     */
    private static void listDroneTypes() {
        try {
            String response = sendGetRequest(BASE_URL + API_DRONE_TYPES + "?format=json");
            List<String> droneTypes = extractDroneTypesFromJson(response);

            System.out.println("\nAVAILABLE DRONE TYPES:");
            for (String droneType : droneTypes) {
                System.out.println(droneType);
            }

        } catch (IOException e) {
            System.out.println("Error fetching drone types: " + e.getMessage());
        }
    }

    /**
     * Displays a summary of all drones and their current status
     */
    private static void displayDroneStatusSummary() {
        try {
            String response = sendGetRequest(BASE_URL + API_DRONES + "?format=json");
            List<String> droneIds = extractDroneIdsFromJson(response);

            System.out.println("\nDRONE STATUS SUMMARY:");
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-5s %-15s %-10s %-10s %-10s\n", "ID", "Status", "Battery", "Speed", "Last Seen");
            System.out.println("------------------------------------------------------------");

            for (String droneId : droneIds) {
                String dynamicsResponse = sendGetRequest(BASE_URL + API_DRONE_DYNAMICS + droneId + "/?format=json");

                String status = extractJsonValue(dynamicsResponse, "status");
                String battery = extractJsonValue(dynamicsResponse, "battery_status");
                String speed = extractJsonValue(dynamicsResponse, "speed");
                String lastSeen = extractJsonValue(dynamicsResponse, "last_seen");

                // Format the last seen timestamp
                if (lastSeen != null && lastSeen.length() > 16) {
                    lastSeen = lastSeen.substring(0, 16);
                }

                System.out.printf("%-5s %-15s %-10s %-10s %-10s\n",
                        droneId, status, battery, speed, lastSeen);
            }

        } catch (IOException e) {
            System.out.println("Error creating status summary: " + e.getMessage());
        }
    }

    /**
     * Monitors a drone in real-time for a specified duration
     */
    private static void monitorDroneInRealTime(String droneId, int durationSeconds) {
        System.out.println("\nMonitoring drone " + droneId + " for " + durationSeconds + " seconds...");
        System.out.println("Press Ctrl+C to stop monitoring early.");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (durationSeconds * 1000);

        try {
            while (System.currentTimeMillis() < endTime) {
                // Get the latest dynamic data
                String response = sendGetRequest(BASE_URL + API_DRONE_DYNAMICS + droneId + "/?format=json");

                String speed = extractJsonValue(response, "speed");
                String latitude = extractJsonValue(response, "latitude");
                String longitude = extractJsonValue(response, "longitude");
                String battery = extractJsonValue(response, "battery_status");
                String status = extractJsonValue(response, "status");

                // Clear console and display updated info
                clearConsole();
                System.out.println("REAL-TIME MONITORING - DRONE " + droneId);
                System.out.println("-----------------------------");
                System.out.println("Status: " + status);
                System.out.println("Battery: " + battery);
                System.out.println("Speed: " + speed);
                System.out.println("Position: " + latitude + ", " + longitude);
                System.out.println("-----------------------------");

                // Wait before next update
                Thread.sleep(2000); // Update every 2 seconds
            }

            System.out.println("\nMonitoring complete.");

        } catch (IOException | InterruptedException e) {
            System.out.println("Error during monitoring: " + e.getMessage());
        }
    }

    /**
     * Sends an HTTP GET request with proper authentication
     */
    private static String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request properties
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", TOKEN);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:138.0) Gecko/20100101 Firefox/138.0");

        // Get response
        int responseCode = connection.getResponseCode();

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
            throw new IOException("HTTP Error: " + responseCode);
        }
    }

    // --- JSON Parsing Helper Methods ---
    // Note: In a real application, use a proper JSON library like Jackson or Gson

    /**
     * Extracts a simple value from a JSON string
     */
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int keyIndex = json.indexOf(searchKey);

        if (keyIndex == -1) {
            return "N/A";
        }

        int valueStart = keyIndex + searchKey.length();
        char firstChar = json.charAt(valueStart);

        if (firstChar == '"') {
            // String value
            int valueEnd = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, valueEnd);
        } else {
            // Numeric or boolean value
            int commaIndex = json.indexOf(",", valueStart);
            int braceIndex = json.indexOf("}", valueStart);

            int valueEnd = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;
            return json.substring(valueStart, valueEnd).trim();
        }
    }

    /**
     * Extracts the next page URL from a paginated response
     */
    private static String extractNextPageUrl(String json) {
        String nextKey = "\"next\":";
        int nextIndex = json.indexOf(nextKey);

        if (nextIndex == -1) {
            return null;
        }

        int urlStart = nextIndex + nextKey.length();
        char firstChar = json.charAt(urlStart);

        if (firstChar == 'n') {
            // "next": null
            return null;
        } else if (firstChar == '"') {
            // "next": "http://..."
            int urlEnd = json.indexOf("\"", urlStart + 1);
            return json.substring(urlStart + 1, urlEnd);
        }

        return null;
    }

    /**
     * Extracts drone information from a JSON response
     */
    private static List<String> extractDronesFromJson(String json) {
        List<String> drones = new ArrayList<>();

        // Very basic extraction - in a real app, use a proper JSON parser
        int resultsStart = json.indexOf("\"results\":");
        if (resultsStart == -1) {
            return drones;
        }

        int arrayStart = json.indexOf("[", resultsStart);
        int arrayEnd = findMatchingBracket(json, arrayStart);

        if (arrayStart == -1 || arrayEnd == -1) {
            return drones;
        }

        String resultsArray = json.substring(arrayStart + 1, arrayEnd);

        // Split the array by objects
        int startIndex = 0;
        int nestingLevel = 0;

        for (int i = 0; i < resultsArray.length(); i++) {
            char c = resultsArray.charAt(i);

            if (c == '{') {
                if (nestingLevel == 0) {
                    startIndex = i;
                }
                nestingLevel++;
            } else if (c == '}') {
                nestingLevel--;
                if (nestingLevel == 0) {
                    String droneObject = resultsArray.substring(startIndex, i + 1);

                    // Extract drone ID and URL
                    String id = extractJsonValue(droneObject, "id");
                    String url = extractJsonValue(droneObject, "url");

                    drones.add("Drone #" + id + " - " + url);
                }
            }
        }

        return drones;
    }

    /**
     * Extracts drone type information from a JSON response
     */
    private static List<String> extractDroneTypesFromJson(String json) {
        List<String> droneTypes = new ArrayList<>();

        // Very basic extraction - in a real app, use a proper JSON parser
        int resultsStart = json.indexOf("\"results\":");
        if (resultsStart == -1) {
            return droneTypes;
        }

        int arrayStart = json.indexOf("[", resultsStart);
        int arrayEnd = findMatchingBracket(json, arrayStart);

        if (arrayStart == -1 || arrayEnd == -1) {
            return droneTypes;
        }

        String resultsArray = json.substring(arrayStart + 1, arrayEnd);

        // Split the array by objects
        int startIndex = 0;
        int nestingLevel = 0;

        for (int i = 0; i < resultsArray.length(); i++) {
            char c = resultsArray.charAt(i);

            if (c == '{') {
                if (nestingLevel == 0) {
                    startIndex = i;
                }
                nestingLevel++;
            } else if (c == '}') {
                nestingLevel--;
                if (nestingLevel == 0) {
                    String typeObject = resultsArray.substring(startIndex, i + 1);

                    // Extract type information
                    String manufacturer = extractJsonValue(typeObject, "manufacturer");
                    String model = extractJsonValue(typeObject, "model");

                    droneTypes.add(manufacturer + " - " + model);
                }
            }
        }

        return droneTypes;
    }

    /**
     * Extracts drone IDs from a JSON response
     */
    private static List<String> extractDroneIdsFromJson(String json) {
        List<String> droneIds = new ArrayList<>();

        // Very basic extraction - in a real app, use a proper JSON parser
        int resultsStart = json.indexOf("\"results\":");
        if (resultsStart == -1) {
            return droneIds;
        }

        int arrayStart = json.indexOf("[", resultsStart);
        int arrayEnd = findMatchingBracket(json, arrayStart);

        if (arrayStart == -1 || arrayEnd == -1) {
            return droneIds;
        }

        String resultsArray = json.substring(arrayStart + 1, arrayEnd);

        // Split the array by objects
        int startIndex = 0;
        int nestingLevel = 0;

        for (int i = 0; i < resultsArray.length(); i++) {
            char c = resultsArray.charAt(i);

            if (c == '{') {
                if (nestingLevel == 0) {
                    startIndex = i;
                }
                nestingLevel++;
            } else if (c == '}') {
                nestingLevel--;
                if (nestingLevel == 0) {
                    String droneObject = resultsArray.substring(startIndex, i + 1);

                    // Extract drone ID
                    String id = extractJsonValue(droneObject, "id");
                    if (!id.equals("N/A")) {
                        droneIds.add(id);
                    }
                }
            }
        }

        return droneIds;
    }

    /**
     * Finds the matching closing bracket for an opening bracket
     */
    private static int findMatchingBracket(String text, int openBracketIndex) {
        char openBracket = text.charAt(openBracketIndex);
        char closeBracket;

        if (openBracket == '[') {
            closeBracket = ']';
        } else if (openBracket == '{') {
            closeBracket = '}';
        } else {
            return -1;
        }

        int nestingLevel = 1;

        for (int i = openBracketIndex + 1; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == openBracket) {
                nestingLevel++;
            } else if (c == closeBracket) {
                nestingLevel--;
                if (nestingLevel == 0) {
                    return i;
                }
            }
        }

        return -1;
    }

    /**
     * Clears the console (works on most terminals)
     */
    private static void clearConsole() {
        try {
            String operatingSystem = System.getProperty("os.name");

            if (operatingSystem.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clearing fails, just print newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}