package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class FoodoOverlayProvider implements OverlayProvider {
	
	@Override
	public ArrayList<OverlayItem> getAllOverlays() {

		try {
			ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
			
			JSONObject result = getResponse("http://foodo.siggijons.net/api/restaurants.json");
			
			JSONArray list = result.getJSONArray("Restaurants");
			for (int i = 0; i < list.length(); i++)
			{
				JSONObject o = list.getJSONObject(i);
				GeoPoint p = new GeoPoint(o.getInt("lat"), o.getInt("lng"));
				overlays.add(new OverlayItem(p, o.getString("name"), ""));
			}
			
			return overlays;
		}
		catch(Exception e)
		{
			return null;
		}
	}

	@Override
	public ArrayList<OverlayItem> getNearbyOverlays(GeoPoint p, int zoom) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private JSONObject getResponse(String service_url) {
		try {
			URL url = new URL(service_url + "");
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			
			JSONObject json;
			
			json = new JSONObject(builder.toString());
			return json.getJSONObject("responseData");
		}
		catch (Exception e) {
			//TODO something
			return null;
		}
	}

}
