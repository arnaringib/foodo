 	package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

public class RestaurantWebService extends Activity {
	
	private static final String TAG = "RestaurantWebService";
    private static final String WEBSERVICE_URL = "http://foodo.nord.is/api";
    	
	RestaurantDbAdapter mDb; 
	
	
    public RestaurantWebService(RestaurantDbAdapter db) {
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
    		JSONObject json = loadData(WEBSERVICE_URL + "/restaurants");
    		Log.d(TAG, json.toString());
    		JSONArray list = json.getJSONObject("responseData").getJSONArray("Restaurants");
    		
    		//Empty database
    		mDb.emptyDatabase();
    		
    		int n = list.length();
    		for (int i = 0; i < n; i++) {
    			JSONObject o = list.getJSONObject(i);
    			mDb.createRestaurant(
    					o.getLong("id"), 
    					o.getString("name"), 
    					o.getInt("lat"),
    					o.getInt("lng"), 
    					o.getDouble("rating"),
    					o.getLong("rating_count"),
    	   				o.getString("address"),
        				o.getInt("zip"),
        				o.getString("city"),
        				o.getString("website"),
        				o.getString("email"),
        				o.getString("phone"),
        				o.getInt("pricegroup")
        		);
    			for(int j = 0; j != o.getJSONArray("types").length(); j++){
    				mDb.createRestaurantsTypes(
    						o.getLong("id"),
    						o.getJSONArray("types").getLong(j)
    				);
    			}
    			Log.d(TAG, o.getString("name"));
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
    /**
     * Add restaurant rating
     * 
     * @param restaurant_id Restaurant Id
     * @param new_rating Given rating
     * @param user_id user_id
     * @return new rating
     */
    public double addRating(long restaurant_id, double new_rating, long user_id) {
    	//TODO talk to webservice    	 
    	try {
    			JSONObject json = loadData(WEBSERVICE_URL + "/restaurant/id/" + restaurant_id + "/rate/" + new_rating + "/user_id/" + user_id);
	    		JSONObject o = json.getJSONObject("responseData").getJSONObject("Restaurants");
    		
    		//JSONObject o = list.getJSONObject(0);
    		mDb.updateRestaurant(
    				o.getLong("id"), 
    				o.getString("name"), 
    				o.getInt("lat"),
    				o.getInt("lng"), 
    				o.getDouble("rating"),
    				o.getLong("rating_count"),
    				o.getString("address"),
    				o.getInt("zip"),
    				o.getString("city"),
    				o.getString("website"),
    				o.getString("email"),
    				o.getString("phone"),
    				o.getInt("pricegroup")
    	    );
    		
    		return o.getDouble("rating");
    	}
    	catch (Exception e) {
    		Log.d(TAG, "Could not upload rating, changing locally");
    		Log.d(TAG, e.toString());
    		Cursor cr = mDb.fetchRestaurant(restaurant_id);
        	
        	double old_rating;
        	double rating;
        	double count;
        	
        	old_rating = cr.getDouble(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING));
        	count = cr.getLong(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING_COUNT));
        	
        	rating = (old_rating * count) + new_rating;
        	count++;
        	rating /= count;
        	
        	return rating;
    	}
    }
    
    public boolean updateAllTypes() {
    	try {
    		JSONObject json = loadData(WEBSERVICE_URL + "/types");
    		Log.d(TAG, json.toString());
    		JSONArray list = json.getJSONObject("responseData").getJSONArray("Types");
    		
    		//Empty database
    		mDb.emptyTypesTable();
    		
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
