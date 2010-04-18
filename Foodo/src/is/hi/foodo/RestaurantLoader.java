package is.hi.foodo;

import is.hi.foodo.net.FoodoService;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class RestaurantLoader {

	private static final String TAG = "RestaurantWebService";

	RestaurantDbAdapter mDb; 
	FoodoService mService;

	public RestaurantLoader(RestaurantDbAdapter db, FoodoService service) {
		mDb = db;
		mService = service;
	}

	private boolean updateDb(JSONArray list)
	{
		try {
			//Remove all restaurants from database
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

				int m = o.getJSONArray("types").length();
				for(int j = 0; j < m; j++){
					mDb.createRestaurantsTypes(
							o.getLong("id"),
							o.getJSONArray("types").getLong(j)
					);
				}
				//Log.d(TAG, o.getString("name"));
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

	public boolean updateAllRestaurants() {
		try {
			JSONArray list = mService.getRestaurants();
			return this.updateDb(list);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean updateAllRestaurantsFromLocation(GeoPoint myLocation, int radius) {
		Log.d(TAG, "UPDATING FROM LOCATION" + myLocation.toString() + "radius: " + radius);
		try {

			int targetRadius = 20;

			JSONArray list = mService.getNearByRestaurants(myLocation.getLatitudeE6()/1E6, myLocation.getLongitudeE6()/1E6, targetRadius);
			Log.d(TAG, list.toString());
			return this.updateDb(list);
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public boolean updateAllTypes() {
		try {
			JSONArray list = mService.getTypes();

			//Empty existing types
			mDb.emptyTypesTable();

			int n = list.length();
			for (int i = 0; i < n; i++) {
				JSONObject o = list.getJSONObject(i);
				mDb.createType(
						o.getLong("id"), 
						o.getString("name")
				);
				//Log.d(TAG, o.getString("name"));
			}
			return true;
		} 
		catch (Exception e) {
			Log.d(TAG, "Unexpected exception while loading types", e);
			return false;
		}
	}

}
