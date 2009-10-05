package is.hi.foodo;

import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;

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
        OverlayItem overlayItem = new OverlayItem(point, "", "");
             
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
  
 
}