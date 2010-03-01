package is.hi.foodo;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class FoodoMap extends MapActivity implements Runnable, LocationListener {
	
	private static final String TAG = "FoodoMap";
	
	private static final int MENU_LIST = Menu.FIRST;
	private static final int MENU_FILTER = Menu.FIRST + 1;
	private static final int MENU_UPDATE = Menu.FIRST + 2;
	
	private static final int MSG_UPDATE_SUCCESSFUL = 1;
	private static final int MSG_UPDATE_FAILED = 2;
	private static final int FILTER_VIEW = 5;
	
	
	private ProgressDialog pd;
	
	private MyLocationOverlay myLocOverlay;
	
	MapView mapView;
	MapController control;
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
		if (mService == null)
			mService = new RestaurantWebService(mDbHelper,((FoodoApp)getApplicationContext()).getService());
		mService.updateAllTypes();
        mDbHelper.fetchAllTypes();
       

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILTER_VIEW) {
            if (resultCode == RESULT_OK) {
                setupOverlays();
            }
        }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/* Create the menu items */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,MENU_LIST,0, R.string.menu_listview);
		 menu.add(0,MENU_FILTER,1, R.string.menu_filter);
		 //menu.add(0,MENU_UPDATE,2, R.string.menu_update);
		 
		 return true;
	}
	
	/* when menu button option selected */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case MENU_LIST:
			Intent listView = new Intent(this, FoodoList.class);
			startActivityForResult(listView, 1);
			return true;
		case MENU_FILTER:
			Intent filter = new Intent(this, FoodoFilter.class);
			startActivityForResult(filter, FILTER_VIEW);
			return true;
		case MENU_UPDATE:
			updateOverlays();
			return true;
		}
		return false;
	}
	
	/**
	 * Shows a restaurants in details view
	 * 
	 * @param Restaurant ID  
	 */
	public void startDetails(long id) {
		Intent i = new Intent(this, FoodoDetails.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, id);
    	startActivity(i);
	}
	
	/**
	 * Initializes MyLocation
	 */
	private void initMyLocation() {
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
		
	}

	/**
	 * Initializes the filter
	 */
	private void initFilter(){
		filter = new Filter();
		
		Filter.types = new CharSequence[numberOfTypes()];
		Filter.typesId = new int[numberOfTypes()];
		Filter.checkedTypes = new boolean[numberOfTypes()]; 
		Filter.lowprice = true;
		Filter.mediumprice = true;
		Filter.highprice = true;
		Filter.radius = 10000;
		Filter.ratingFrom = "0";
		Filter.ratingTo = "5";
		
		// Collect data for types.
		collectTypes();
	}
	
	// Post: updates Filter.types and checkedTypes
	//		data gotten from server.
	private void collectTypes(){
		Cursor c = mDbHelper.fetchAllTypes();
		startManagingCursor(c);
		if (c.moveToFirst())
		{
			do {
				Filter.types[c.getPosition()] = c.getString(c.getColumnIndex(RestaurantDbAdapter.KEY_TYPE));
				Filter.typesId[c.getPosition()] = c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_TROWID)); 
				Filter.checkedTypes[c.getPosition()] = true;
				
			} while (c.moveToNext());
		}
		else {
			 Log.d(TAG, "No types found!");
		}
		c.close();
	}
		
	// Post: returns the number of types of restaurants.
	private int numberOfTypes(){
		Cursor c = mDbHelper.fetchAllTypes();
		startManagingCursor(c);
		return c.getCount();
	}
	
	/**
	 * Calculates the distance between two GPS coordinates (p1 and p2)
	 * 
	 * @param lat1 latitude of p1
	 * @param lon1 longitude of p1
	 * @param lat2 latitude of p2
	 * @param lon2 longitude of p2
	 * 
	 * @return distance between p1 and p2 in kilometers
	 */
	public static double calcDistance(double lat1, double lon1, double lat2, double lon2) {
		final double RADIAN = 57.29577951;
    	double latA = lat1 / RADIAN;
    	double lonB = lon1 / RADIAN;
    	double latC = lat2 / RADIAN;
    	double lonD = lon2 / RADIAN;
    	double q = Math.sin(latA) * Math.sin(latC) + Math.cos(latA) * Math.cos(latC) * Math.cos(lonB-lonD);
    	double dist;
    	double kmDist;
    	
    	if(q > 1)
    		dist = 3963.1 * Math.acos(1);
    	else
    		dist = 3963.1 * Math.acos(q);
    	
    	kmDist = dist /  0.621371192237;
    	
    	return kmDist;
	}
	
	/**
	 * Creates Overlays for restaurants in the database.
	 * Will try to update overlays of none are found by calling updateOverlays()
	 * which will call setupOverlays() again if any restaurants are found
	 */
	private void setupOverlays() {
		Cursor c = mDbHelper.fetchAllRestaurants(Filter.ratingFrom,
												Filter.ratingTo,
												Filter.lowprice,
												Filter.mediumprice,
												Filter.highprice,
												Filter.checkedTypes,
												Filter.typesId);
		startManagingCursor(c);
		
		if (c.moveToFirst())
		{
			mapRestaurantsOverlays = mapView.getOverlays();
			mapRestaurantsOverlays.clear();
			
			drawable = getResources().getDrawable(R.drawable.bubble);
	        foodoRestaurantsOverlays = new FoodoOverlays(drawable, mapView);
			
			do {
				GeoPoint p = new GeoPoint(
						c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_LAT)), 
						c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_LNG))
				);	
				
				FoodoOverlayItem item = new FoodoOverlayItem(
					p, 
					c.getString(c.getColumnIndex(RestaurantDbAdapter.KEY_NAME)), 
					"",
					c.getLong(c.getColumnIndex(RestaurantDbAdapter.KEY_ROWID))
				);
				
				if(myLocOverlay.getMyLocation() != null) {
					if(calcDistance(myLocOverlay.getMyLocation().getLatitudeE6()/1000000.0, myLocOverlay.getMyLocation().getLongitudeE6()/1000000.0, item.getPoint().getLatitudeE6()/1000000.0, item.getPoint().getLongitudeE6()/1000000.0) * 1000 < (double) Filter.radius) {
						Log.d(TAG, item.getTitle() + " : " + calcDistance(myLocOverlay.getMyLocation().getLatitudeE6()/1000000.0, myLocOverlay.getMyLocation().getLongitudeE6()/1000000.0, item.getPoint().getLatitudeE6()/1000000.0, item.getPoint().getLongitudeE6()/1000000.0) * 1000);
						foodoRestaurantsOverlays.addOverlay(item);
					}
				}
				else {
					foodoRestaurantsOverlays.addOverlay(item);
				}
			} while (c.moveToNext());
			
			mapView.getOverlays().add(foodoRestaurantsOverlays);
			mapView.getOverlays().add(myLocOverlay);
			
			mapView.refreshDrawableState();
		}
		else {
			updateOverlays();
		}
		c.close();
		mapView.invalidate();
	}
	
	/**
	 * Displays a progress dialog and starts a thread which will update overlays
	 */
	private void updateOverlays() 
	{
		pd = ProgressDialog.show(FoodoMap.this, "Working..", "Updating");
		Thread thread = new Thread(FoodoMap.this);
		thread.start();
	}

	@Override
	public void run() {
		if (mService.updateAllRestaurants())
			handler.sendEmptyMessage(MSG_UPDATE_SUCCESSFUL);
		else
			handler.sendEmptyMessage(MSG_UPDATE_FAILED);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case MSG_UPDATE_SUCCESSFUL:
				setupOverlays();
				break;
			case MSG_UPDATE_FAILED:
				Toast.makeText(FoodoMap.this, "Update failed", Toast.LENGTH_LONG).show();
				break;
			}
		}
	};

	@Override
	public void onLocationChanged(Location location) {
		control = mapView.getController();
		GeoPoint punktur = new GeoPoint((int)(location.getLatitude()*1E6),(int)( location.getLongitude()*1E6));
		control.animateTo(punktur);
		 
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
}