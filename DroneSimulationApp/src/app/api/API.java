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

public class API {

    /**Follow paginated “next” links to collect all DroneType entries
     * @return List of DroneType objects
     */
    public static List<DroneType> getDroneTypes() {
        List<DroneType> result = new ArrayList<>();
        String nextUrl = Constants.BASE_URL + "/api/dronetypes/?format=json";
        while (nextUrl != null) {
            String json = fetchJson(nextUrl);
            JSONObject root = new JSONObject(json);
            JSONArray arr = root.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                result.add(new DroneType(o));
            }
            nextUrl = root.optString("next", null);
        }
        return result;
    }

    /** Follow pagination to collect all Drone entries
     *
     * @return List of Drone objects
     */
    public static List<Drone> getDrones() {
        List<Drone> result = new ArrayList<>();
        String nextUrl = Constants.BASE_URL + "/api/drones/?format=json";
        while (nextUrl != null) {
            String json = fetchJson(nextUrl);
            JSONObject root = new JSONObject(json);
            JSONArray arr = root.getJSONArray("results");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                result.add(new Drone(o));
            }
            nextUrl = root.optString("next", null);
        }
        return result;
    }

    /** Fetch a single DroneType by its URL
     *
     * @param url URL of the DroneType
     * @return DroneType object
     */
    public static DroneType getDroneTypeInstance(String url){
        String json = fetchJson(url);
        JSONObject root = new JSONObject(json);
        return new DroneType(root);
    }

    /** Fetch exactly one “page” of DroneDynamics (10 items per page)
     *
     * @param page current page number, starting from 0 (0-based)
     * @return List of DroneDynamics objects, load 10 pages at a time to avoid overloading the API and reduce load time
     */
    public static List<DroneDynamics> getDroneDynamics(int page) {
        String url = Constants.BASE_URL + "/api/dronedynamics/?offset=" + page * 10;
        String json = fetchJson(url);
        JSONObject root = new JSONObject(json);
        JSONArray arr = root.getJSONArray("results");
        List<DroneDynamics> result = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.getJSONObject(i);
            result.add(new DroneDynamics(o));
        }
        return result;
    }

    /** Performs HTTP GET, sets headers, reads JSON; wraps IO errors in AppException
     *
     * @param urlString URL to fetch from
     * @return JSON string containing the response body, throws AppException if fetch fails
     */
    private static String fetchJson(String urlString) {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
            conn.setRequestProperty("Authorization", Constants.TOKEN);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(sb::append);
            return sb.toString();
        } catch (IOException e) {
            throw new AppException("API fetch failed: " + e.getMessage());
        }
    }
}