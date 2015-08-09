package internet;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
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
}
