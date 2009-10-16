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
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class FoodoMap extends MapActivity {
	
	private static final String TAG = "FoodoMap";
	
	MapView mapView;
	Drawable drawable;
	FoodoOverlays foodoRestaurantsOverlays;
	
	RestaurantDbAdapter mDbHelper;
	List<Overlay> mapRestaurantsOverlays;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mDbHelper = new RestaurantDbAdapter(this);
        mDbHelper.open();
        
        setupOverlays();
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void startDetails(int id) {
		Intent i = new Intent(this, FoodoDetails.class);
		//i.putExtra(Restaurant.ROW_ID, id);
    	startActivityForResult(i, 1);
	}
	
	/* Create the menu items */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,0,0, R.string.menu_listview);
		 menu.add(0,1,1, R.string.menu_filter);
		 menu.add(0,2,2, R.string.menu_update);
		 
		 return true;
	}
	
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
			Toast.makeText(context, "Sorry no filter available yet", Toast.LENGTH_SHORT).show();
			return true;
		case 2:
			Toast.makeText(context, "Update...", Toast.LENGTH_SHORT).show();
			
			mDbHelper.loadFromWebService();
			setupOverlays();
			Toast.makeText(context, "Update complete", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
	
	private void setupOverlays() {
		Cursor c = mDbHelper.fetchAllRestaurants();
		startManagingCursor(c);
		
		if (c.moveToFirst())
		{
			mapRestaurantsOverlays = mapView.getOverlays();
			
			drawable = getResources().getDrawable(R.drawable.bubble);
	        foodoRestaurantsOverlays = new FoodoOverlays(drawable);
			
			do {
				GeoPoint p = new GeoPoint(
						c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_LAT)), 
						c.getInt(c.getColumnIndex(RestaurantDbAdapter.KEY_LNG))
				);	
				OverlayItem item = new OverlayItem(p, c.getString(c.getColumnIndex(RestaurantDbAdapter.KEY_NAME)), "");
				foodoRestaurantsOverlays.addOverlay(item);
				Log.d(TAG, item.getTitle());
				
			} while (c.moveToNext());
			
			mapView.getOverlays().add(foodoRestaurantsOverlays);
		}
		else {
			Log.d(TAG, "Failed to move to first!");
		}
		c.close();
	}
 
}