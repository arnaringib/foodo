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
    private static final String WEBSERVICE_URL = "http://foodo.siggijons.net/api/restaurants.json";
	
	RestaurantDbAdapter mDb;
    
    public RestaurantWebService(RestaurantDbAdapter db) {
    	mDb = db;
    }
    
    public boolean updateAll() {
    	try {
    		URL url = new URL(WEBSERVICE_URL);
    		URLConnection connection = url.openConnection();
    		
    		BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
    		StringBuilder builder = new StringBuilder();
    		String line;
    		while (( line = reader.readLine()) != null)
    		{
    			builder.append(line);
    		}
    		
    		JSONObject json = new JSONObject(builder.toString());
    		JSONArray list = json.getJSONObject("responseData").getJSONArray("Restaurants");
    		
    		//Empty database
    		//mDb.execSQL(DATABASE_EMPTY);
    		
    		int n = list.length();
    		for (int i = 0; i < n; i++) {
    			JSONObject o = list.getJSONObject(i);
    			mDb.createRestaurant(
    					o.getLong("id"), 
    					o.getString("name"), 
    					o.getInt("lat"),
    					o.getInt("lng"), 
    					o.getDouble("rating"),
    					o.getLong("rating_count"));
    		}
    		return true;
    	}
    	catch (MalformedURLException e) {
    		Log.d(TAG, "MalformedURLException in loadFromWebService");
    		return false;
    	}
    	catch (Exception e) {
    		//TODO log this
    		Log.d(TAG, "Exception in loadFromWebService");
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
    	Cursor cr = mDb.fetchRestaurant(restaurant_id);
    	
    	double old_rating;
    	double rating;
    	double count;
    	
    	old_rating = cr.getDouble(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING));
    	count = cr.getLong(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING_COUNT));
    	
    	rating = (old_rating * count) + new_rating;
    	count++;
    	rating /= count;
    	
    	
    	mDb.updateRestaurant(
    			cr.getLong(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_ROWID)), 
    			cr.getString(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)), 
    			cr.getInt(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)), 
    			cr.getInt(cr.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)), 
    			rating,
    			count
    	);
    	
    	return rating;
    }

}
