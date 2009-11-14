package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class TypesWebService {
	private static final String TAG = "TypesWebService";
    private static final String WEBSERVICE_URL = "http://foodo.nord.is/api";
	
	TypesDbAdapter mDb;
    
    public TypesWebService(TypesDbAdapter db) {
    	mDb = db;
    }
    
    private JSONObject loadData(String path) {
    	try {
	    	URL url = new URL(path);
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);	
			}
			return new JSONObject(builder.toString());
    	}
    	catch (MalformedURLException e) {
    		Log.d(TAG, "MalformedURLException in loadFromWebService");
    		return null;
    	}
    	catch (Exception e) {
    		Log.d(TAG, "Exception in WebService");
    		return null;
    	}
    }
    
    public boolean updateAll() {
    	try {
    		JSONObject json = loadData(WEBSERVICE_URL + "/types");
    		Log.d(TAG, json.toString());
    		JSONArray list = json.getJSONObject("responseData").getJSONArray("Types");
    		
    		//Empty database
    		mDb.emptyDatabase();
    		
    		int n = list.length();
    		for (int i = 0; i < n; i++) {
    			JSONObject o = list.getJSONObject(i);
    			mDb.createType(
    					o.getLong("id"), 
    					o.getString("type")
        		);
    			Log.d(TAG, o.getString("type"));
    		}
    		return true;
    	}
    	catch (Exception e) {
    		//TODO log this
    		Log.d(TAG, "Exception in loadFromWebService");
    		Log.d(TAG, e.toString());
    		return false;
    	}
    }
}
