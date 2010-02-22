package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class MenuWebService {
	
	private static final String TAG = "MenuWebService";
    private static final String WEBSERVICE_URL = "http://foodo.morpho.nord.is/api";
	
    
    public JSONArray getMenu(long restaurant_id) {
    	try {
    		URL url = new URL(WEBSERVICE_URL + "/restaurants/" + restaurant_id + "/menu");
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);	
			}
			
			JSONObject o = new JSONObject(builder.toString());
			
			
			return o.getJSONObject("responseData").getJSONArray("Menu");
    	}
    	catch (Exception e) {
    		Log.d(TAG, "Exception in getMenu()");
    		Log.d(TAG, e.getMessage());
    		return null;
    	}
    }

}
