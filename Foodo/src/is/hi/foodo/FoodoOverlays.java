package is.hi.foodo;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class FoodoOverlays extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mRestaurantsOverlays = new ArrayList<OverlayItem>(); 
	
	public FoodoOverlays(Drawable defaultMarker) {
		super(boundCenter(defaultMarker));
	}
	
	/**
	 * 
	 * @param overlay
	 * Add a new restaurant overlay.
	 */
	public void addOverlay(OverlayItem overlay){
		mRestaurantsOverlays.add(overlay);
		populate(); //Every overlayitem is read and prepared to be drawned
	}	
	
	@Override
	protected OverlayItem createItem(int i) {
		return mRestaurantsOverlays.get(i);
	}

	@Override
	/**
	 * @return size()
	 * Returns the number of overlays
	 */
	public int size() {
		return mRestaurantsOverlays.size();
	}

}
