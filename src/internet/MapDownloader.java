package internet;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import map.Map;
import map.Region;
import map.SuperRegion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Finds and gets map data from a given url
 * @author jalal
 *
 */
public class MapDownloader {
	
	private static Document game;
	
	public static String getMap(String url) throws IOException {
		game = Jsoup.connect(url).get();
		return game.text();
	}
	
	public static String getFormattedMap(String url) throws IOException {
		game = Jsoup.connect(url).get();
		return "This map's url is " + url + "\n" + game.text();
	}
	
	public static String parse(String map) {
		StringBuilder builder = new StringBuilder();

		JSONObject mapObject = new JSONObject(map);
		JSONArray regionArray = mapObject.getJSONArray("Regions");
		
		int index= 0;
		while(index < regionArray.length()) {
			JSONObject obj = regionArray.getJSONObject(index++);
			
			builder.append("id: " + obj.getInt("id") + "\n");
			builder.append("super region: " + obj.getInt("superRegion") + "\n");
			builder.append("neighbors: " + obj.getJSONArray("neighbors").toString() + "\n");
			builder.append("\n");
		}
		
		return builder.toString();
	}
	
	/**
	 * Converts a map string into a map object
	 * @param mapString : string that represents the map to be created
	 * @return : a Map object to use in the game
	 */
	public static Map createMap(String mapString)
	{
		Map map = new Map();

		//parse the map string
		try {
			JSONObject jsonMap = new JSONObject(mapString);
			
			// create SuperRegion objects
			JSONArray superRegions = jsonMap.getJSONArray("SuperRegions");
			for (int i = 0; i < superRegions.length(); i++) { 
				JSONObject jsonSuperRegion = superRegions.getJSONObject(i);
				map.add(new SuperRegion(jsonSuperRegion.getInt("id"), jsonSuperRegion.getInt("bonus")));
			}
			
			// create Region object
			JSONArray regions = jsonMap.getJSONArray("Regions");
			for (int i = 0; i < regions.length(); i++) { 
				JSONObject jsonRegion = regions.getJSONObject(i);
				SuperRegion superRegion = map.getSuperRegion(jsonRegion.getInt("superRegion"));
				map.add(new Region(jsonRegion.getInt("id"), superRegion));
			}
			
			// add the Regions' neighbors
			for (int i = 0; i < regions.length(); i++) { 
				JSONObject jsonRegion = regions.getJSONObject(i);
				Region region = map.getRegion(jsonRegion.getInt("id"));
				JSONArray neighbors = jsonRegion.getJSONArray("neighbors");
				for (int j = 0; j < neighbors.length(); j++) {
					Region neighbor = map.getRegion(neighbors.getInt(j));
					region.addNeighbor(neighbor);
				}
			}
		} catch (JSONException e) {
			System.err.println("JSON: Can't parse map string: " + e);
		}

		Collections.sort(map.getRegions());
		Collections.sort(map.getSuperRegions());
		
		return map;
	}
}
