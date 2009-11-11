 	package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import android.database.Cursor;
import android.util.Log;

public class RestaurantWebService {
	
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
    		JSONObject json = loadData(WEBSERVICE_URL + "/api/restaurants");
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
        				o.getString("phone")
        		);
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
     * @return new rating
     */
    public double addRating(long restaurant_id, double new_rating) {
    	//TODO talk to webservice
    	try {
    		JSONObject json = loadData(WEBSERVICE_URL + "/restaurant/id/" + restaurant_id + "/rate/" + new_rating);
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
    				o.getString("phone")
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

}
