package is.hi.foodo;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class FoodoMap extends MapActivity {
	
	private static final String TAG = "FoodoMap";
	
	private MyLocationOverlay myLocOverlay;
	
	MapView mapView;
	Drawable drawable;
	FoodoOverlays foodoRestaurantsOverlays;
	
	RestaurantDbAdapter mDbHelper;
	List<Overlay> mapRestaurantsOverlays;
	
	RestaurantWebService mService;
	
	Filter filter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
               
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mDbHelper = new RestaurantDbAdapter(this);
        mDbHelper.open();
       
        initFilter();
        initMyLocation();
        setupOverlays();
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void startDetails(long id) {
		Intent i = new Intent(this, FoodoDetails.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, id);
    	startActivity(i);
	}
	
	/* Create the menu items */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,0,0, R.string.menu_listview);
		 menu.add(0,1,1, R.string.menu_filter);
		 menu.add(0,2,2, R.string.menu_update);
		 
		 return true;
	}
	
	CharSequence text3 = "Want more??";
	int duration = Toast.LENGTH_SHORT;
	
	/* when menu button option selected */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		switch (item.getItemId()) {
		case 0:
			Intent listView = new Intent(this, FoodoList.class);
			startActivityForResult(listView, 1);
			return true;
		case 1:
			Intent filter = new Intent(this, FoodoFilter.class);
			startActivityForResult(filter, 1);
			return true;
		case 2:
			Toast.makeText(context, "Update...", Toast.LENGTH_SHORT).show();

			if (updateOverlays())
			{
				Toast.makeText(context, "Update complete", Toast.LENGTH_SHORT).show();
				setupOverlays();
			}
			else {
				Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}
	
	private boolean updateOverlays() 
	{
		if (mService == null)
			mService = new RestaurantWebService(mDbHelper);
		return mService.updateAll();
	}
	private void initMyLocation() {
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
 
	}
 
	private void initFilter(){
		filter = new Filter();
		
		Filter.types = new CharSequence[numberOfTypes()];    
		Filter.checkedTypes = new boolean[numberOfTypes()]; 
		Filter.priceFrom = "0";
		Filter.priceTo = "10000";
		Filter.radius = 10000;
		Filter.ratingFrom = "0.0";
		Filter.ratingTo = "5.0";
		
		// Collect data for types.
		collectTypes();
	}
	
	// Post: updates Filter.types and checkedTypes
	//		data gotten from server.
	private void collectTypes(){
		// Need to get this from server this is
		// temp data.
		// -Arnar
		CharSequence[] tmpT = {"Fast", "Fine dining",
				"Family", "Casual", "Sea", "Launch", "Mexican",
				"Asian", "Vegetarian", "Buffet", "Sandwiches",
				"Bistro", "Drive-in", "Take out", "Steakhouse",
				"Sushi"};
		boolean[] tmpB = {true, true,
				true, true, true, true, true,
				true, true, true, true,
				true, true, true, true,
				true};
		for(int i = 0; i != numberOfTypes(); i++){
			Filter.types[i] = tmpT[i];
			Filter.checkedTypes[i] = tmpB[i];			
		}
	}
	
	// Post: returns the number of types of restaurants.
	private int numberOfTypes(){
		return 16;
	}
	
	private void setupOverlays() {
		Cursor c = mDbHelper.fetchAllRestaurants();
		startManagingCursor(c);
		
		if (c.moveToFirst())
		{
			mapRestaurantsOverlays = mapView.getOverlays();
			mapRestaurantsOverlays.clear();
			
			drawable = getResources().getDrawable(R.drawable.bubble);
	        foodoRestaurantsOverlays = new FoodoOverlays(drawable);
			
			do {
				GeoPoint p = new GeoPoint(
						c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_LAT)), 
						c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_LNG))
				);	
				FoodoOverlayItem item = 
					new FoodoOverlayItem(
							p, 
							c.getString(c.getColumnIndex(RestaurantDbAdapter.KEY_NAME)), 
							"",
							c.getLong(c.getColumnIndex(RestaurantDbAdapter.KEY_ROWID))
					);
				foodoRestaurantsOverlays.addOverlay(item);
				Log.d(TAG, item.getTitle());
				
			} while (c.moveToNext());
			
			mapView.getOverlays().add(foodoRestaurantsOverlays);
			mapView.getOverlays().add(myLocOverlay);
		}
		else {
			Log.d(TAG, "Failed to move to first!");
		}
		c.close();
	}
	
}