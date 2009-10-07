package is.hi.foodo;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class FoodoMap extends MapActivity {
	
	LinearLayout linearLayout;
	MapView mapView;
	List<Overlay> mapRestaurantsOverlays;
	Drawable drawable;
	FoodoOverlays foodoRestaurantsOverlays; 
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        //Overlays
        mapRestaurantsOverlays = mapView.getOverlays();
        //Need some new icon
        drawable = this.getResources().getDrawable(R.drawable.minifork);
        foodoRestaurantsOverlays = new FoodoOverlays(drawable);
        
        // Test(Arnar) - Can be commented out!
        GeoPoint point = new GeoPoint(64139603,-21955812);
        OverlayItem overlayItem = new OverlayItem(point, "Burger Joint", "");
             
        foodoRestaurantsOverlays.addOverlay(overlayItem);
        mapRestaurantsOverlays.add(foodoRestaurantsOverlays);
        
    }

	/** Called when the activity is first created. */
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
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
		 menu.add(0,0,0,"List View");
		 menu.add(0,1,1,"Filter");
		 menu.add(0,2,2,"More?");
		 
		 return true;
	}
	
	CharSequence text1 = "Sorry no list view available yet";
	CharSequence text2 = "Sorry no filter available yet";
	CharSequence text3 = "Want more??";
	int duration = Toast.LENGTH_SHORT;

	
	/* when menu button option selected */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		switch (item.getItemId()) {
		case 0:
			Toast toast1 = Toast.makeText(context, text1, duration);
			toast1.show();
			return true;
		case 1:
			Toast toast2 = Toast.makeText(context, text2, duration);
			toast2.show();
			return true;
		case 2:
			Toast toast3 = Toast.makeText(context, text3, duration);
			toast3.show();
			return true;
		}
		return false;
	}
  
 
}