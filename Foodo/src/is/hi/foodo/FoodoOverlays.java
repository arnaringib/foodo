package is.hi.foodo;

import java.util.ArrayList;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class FoodoOverlays extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> mRestaurantsOverlays = new ArrayList<OverlayItem>();
	private long b = -1;
	
	public FoodoOverlays(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
		
	/**
	 * 
	 * @param overlay
	 * Add a new restaurant overlay.
	 */
	public void addOverlay(OverlayItem overlay){
		mRestaurantsOverlays.add(overlay);
		populate(); //Every overlayItem is read and prepared to be drawned
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
	
	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView) {
		GeoPoint p  = mapView.getProjection().fromPixels(
				(int) event.getX(),
				(int) event.getY());
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			b = event.getEventTime();
		}
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{
			if(event.getEventTime()-b >= 250){
				int count = 0;
				while(count < mRestaurantsOverlays.size()){
					if((mRestaurantsOverlays.get(count).getPoint().getLatitudeE6() + 1000 >= p.getLatitudeE6() &&
						mRestaurantsOverlays.get(count).getPoint().getLatitudeE6() - 1000 <= p.getLatitudeE6()) &&
						(mRestaurantsOverlays.get(count).getPoint().getLongitudeE6() + 1000 >= p.getLongitudeE6() &&
						mRestaurantsOverlays.get(count).getPoint().getLongitudeE6() - 1000 <= p.getLongitudeE6())){
					
						FoodoMap fMap = (FoodoMap) mapView.getContext();
						fMap.startDetails(1);
					
					}
					count++;
				}
			}
			else{
				int count = 0;
				while(count < mRestaurantsOverlays.size()){
					if((mRestaurantsOverlays.get(count).getPoint().getLatitudeE6() + 1000 >= p.getLatitudeE6() &&
						mRestaurantsOverlays.get(count).getPoint().getLatitudeE6() - 1000 <= p.getLatitudeE6()) &&
						(mRestaurantsOverlays.get(count).getPoint().getLongitudeE6() + 1000 >= p.getLongitudeE6() &&
						mRestaurantsOverlays.get(count).getPoint().getLongitudeE6() - 1000 <= p.getLongitudeE6())){
					
						Toast.makeText(mapView.getContext(), 
							mRestaurantsOverlays.get(count).getTitle(), 
							Toast.LENGTH_SHORT).show();
			
					}
					count++;
				}
			}
		}
		return false;
	}
}
