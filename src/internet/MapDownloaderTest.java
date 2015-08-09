package internet;

import java.io.IOException;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

public class MapDownloaderTest {

	@Test
	public void testUrl() {
		try {
			System.out.println(MapDownloader.getMap("http://theaigames.com/competitions/warlight-ai-challenge-2/games/55c6a03135ec1d4702e522b2/map"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testParser() {
		try {
			String map = MapDownloader.getMap("http://theaigames.com/competitions/warlight-ai-challenge-2/games/55c6a03135ec1d4702e522b2/map");
			System.out.println(MapDownloader.parse(map));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
