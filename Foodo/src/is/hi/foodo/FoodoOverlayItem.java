package is.hi.foodo;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class FoodoOverlayItem extends OverlayItem {
	
	public long id;

	public FoodoOverlayItem(GeoPoint point, String title, String snippet, long id) {
		super(point, title, snippet);
		this.id = id;
	}

}
