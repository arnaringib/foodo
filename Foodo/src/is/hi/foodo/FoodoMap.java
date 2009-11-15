package is.hi.foodo;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class FoodoMap extends MapActivity implements Runnable {
	
	private static final String TAG = "FoodoMap";
	
	private static final int MENU_LIST = Menu.FIRST;
	private static final int MENU_FILTER = Menu.FIRST + 1;
	private static final int MENU_UPDATE = Menu.FIRST + 2;
	private static final int MENU_LOGIN = Menu.FIRST + 3;
	
	private static final int MSG_UPDATE_SUCCESSFUL = 1;
	private static final int MSG_UPDATE_FAILED = 2;
	
	private ProgressDialog pd;
	
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
		if (mService == null)
			mService = new RestaurantWebService(mDbHelper);
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
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	/* Create the menu items */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,MENU_LIST,0, R.string.menu_listview);
		 menu.add(0,MENU_FILTER,1, R.string.menu_filter);
		 menu.add(0,MENU_UPDATE,2, R.string.menu_update);
		 menu.add(0,MENU_LOGIN,3, R.string.menu_login);
		 
		 return true;
	}
	
	CharSequence text3 = "Want more??";
	int duration = Toast.LENGTH_SHORT;
	
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
			startActivityForResult(filter, 1);
			return true;
		case MENU_UPDATE:
			updateOverlays();
			return true;
		case MENU_LOGIN:
			Intent login = new Intent(this, FoodoLogin.class);
			startActivityForResult(login, 1);
			return true;
		}
		return false;
	}
	
	public void startDetails(long id) {
		Intent i = new Intent(this, FoodoDetails.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, id);
    	startActivity(i);
	}
	
	private void initMyLocation() {
		myLocOverlay = new MyLocationOverlay(this, mapView);
		myLocOverlay.enableMyLocation();
 
	}

	private void initFilter(){
		filter = new Filter();
		
		Filter.types = new CharSequence[numberOfTypes()];
		Filter.typesId = new int[numberOfTypes()];
		Filter.checkedTypes = new boolean[numberOfTypes()]; 
		Filter.lowprice = true;
		Filter.mediumprice = true;
		Filter.highprice = true;
		Filter.radius = 10000;
		Filter.ratingFrom = "0.0";
		Filter.ratingTo = "5.0";
		
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
	
	public double calcDistance(double lat1, double lon1, double lat2, double lon2) {
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
	}
	
	private void updateOverlays() 
	{
		pd = ProgressDialog.show(FoodoMap.this, "Working..", "Updating");
		Thread thread = new Thread(FoodoMap.this);
		thread.start();
	}

	@Override
	public void run() {
		if (mService.updateAll())
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
	
}