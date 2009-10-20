package is.hi.foodo;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class SimpleOverlayProvider implements OverlayProvider {
	
	public ArrayList<OverlayItem> getAllOverlays() {
		
		ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
		
		GeoPoint point = new GeoPoint(64139603,-21955812);
		overlays.add(new OverlayItem(point, "Burger Joint", ""));
		
		GeoPoint point2 = new GeoPoint(64135603,-21954812);
		overlays.add(new OverlayItem(point2, "Pizza Joint", ""));
        	
		return overlays;
	}

	public ArrayList<OverlayItem> getNearbyOverlays(GeoPoint p, int zoom) {
		// TODO Auto-generated method stub
		return null;
	}

}
