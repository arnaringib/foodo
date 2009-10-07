package is.hi.foodo;

import java.util.ArrayList;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public interface OverlayProvider {
	public ArrayList<OverlayItem> getAllOverlays();
	public ArrayList<OverlayItem> getNearbyOverlays(GeoPoint p, int zoom);
}
