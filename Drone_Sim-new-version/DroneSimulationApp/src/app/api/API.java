package app.api;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.*;

import app.model.Drone;
import app.model.DroneDynamics;
import app.model.DroneType;
import app.util.AppException;
import app.util.Constants;

/**
 * Handles all API requests to fetch drone data.
 */
public class API {

    /**
     * Gets all available drone types from the API.
     *
     * @return list of drone types
     */
    public static List<DroneType> getDroneTypes() {
        List<DroneType> result = new ArrayList<>();
        String nextUrl = Constants.BASE_URL + "/api/dronetypes/?format=json";

        // Fetch all pages until "next" is null (pagination handling)
        while (nextUrl != null) {
            String json = fetchJson(nextUrl);
            JSONObject root = new JSONObject(json);
            JSONArray arr = root.getJSONArray("results");

            // Convert each JSON object into a DroneType instance
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                result.add(new DroneType(o));
            }

            // Get the URL for the next page, or null if none
            nextUrl = root.optString("next", null);
        }

        return result;
    }

    /**
     * Gets all individual drones from the API.
     *
     * @return list of drones
     */
    public static List<Drone> getDrones() {
        List<Drone> result = new ArrayList<>();
        String nextUrl = Constants.BASE_URL + "/api/drones/?format=json";

        // Iterate through paginated results
        while (nextUrl != null) {
            String json = fetchJson(nextUrl);
            JSONObject root = new JSONObject(json);
            JSONArray arr = root.getJSONArray("results");

            // Parse each drone JSON object
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                result.add(new Drone(o));
            }

            // Continue with the next page if available
            nextUrl = root.optString("next", null);
        }

        return result;
    }

    /**
     * Fetches a specific drone type from a given URL.
     *
     * @param url API URL for the drone type
     * @return a DroneType object
     */
    public static DroneType getDroneTypeInstance(String url) {
        String json = fetchJson(url);
        JSONObject root = new JSONObject(json);
        return new DroneType(root);
    }

    /**
     * Gets one page of drone dynamics data.
     *
     * @param page page number (used for pagination)
     * @return list of drone dynamics
     */
    public static List<DroneDynamics> getDroneDynamics(int page) {
        String url = Constants.BASE_URL + "/api/dronedynamics/?offset=" + page * 10;
        String json = fetchJson(url);
        JSONObject root = new JSONObject(json);
        JSONArray arr = root.getJSONArray("results");

        List<DroneDynamics> result = new ArrayList<>();

        // Convert each JSON object into a DroneDynamics instance
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            result.add(new DroneDynamics(o));
        }

        return result;
    }

    /**
     * Helper method to fetch JSON content from a given URL.
     *
     * @param urlString full API request URL
     * @return JSON string response
     * @throws AppException if connection or reading fails
     */
    private static String fetchJson(String urlString) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();

            // Set request headers for authentication and content type
            conn.setRequestProperty("Authorization", Constants.TOKEN);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Read the response line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(sb::append);

            return sb.toString();
        } catch (IOException e) {
            // Wrap checked exception in a runtime exception
            throw new AppException("API fetch failed: " + e.getMessage());
        }
    }
}
