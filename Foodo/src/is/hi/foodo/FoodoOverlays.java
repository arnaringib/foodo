package is.hi.foodo;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;

public class FoodoOverlays extends ItemizedOverlay<FoodoOverlayItem> {
	private ArrayList<FoodoOverlayItem> mRestaurantsOverlays = new ArrayList<FoodoOverlayItem>();
	private MapView mapView;
	private int last;
	private Dialog dialog;
	
	public FoodoOverlays(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		this.mapView = mapView;
	}
		
	/**
	 * 
	 * @param overlay
	 * Add a new restaurant overlay.
	 */
	public void addOverlay(FoodoOverlayItem overlay){
		mRestaurantsOverlays.add(overlay);
		populate(); //Every overlayItem is read and prepared to be drawn
	}	
	
	@Override
	protected FoodoOverlayItem createItem(int i) {
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
	protected boolean onTap(int index) {
		last = index;		
		dialog = new Dialog(mapView.getContext());
		
		dialog.setContentView(R.layout.restaurantpicker);
		if(getItem(index).getTitle().length() < 19){
			dialog.setTitle(getItem(index).getTitle());
		}
		else{
			dialog.setTitle(getItem(index).getTitle().subSequence(0,19) + "...");
		}
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		final Button bYes = (Button) dialog.findViewById(R.id.pickRestaurantYes);
		View.OnClickListener lYes = new View.OnClickListener(){
			public void onClick(View v){			
				FoodoMap fMap = (FoodoMap) mapView.getContext();
				fMap.startDetails(mRestaurantsOverlays.get(last).id);
				dialog.cancel();
			}
		};
		bYes.setOnClickListener(lYes);
		
		final Button bNo = (Button) dialog.findViewById(R.id.pickRestaurantNo);
		View.OnClickListener lNo = new View.OnClickListener(){
			public void onClick(View v){
				dialog.cancel();
			}
		};
		bNo.setOnClickListener(lNo);
		
		
		return false;
	}
	
	public void setOverlays(ArrayList<FoodoOverlayItem> allOverlays) {
		mRestaurantsOverlays = allOverlays;
		populate();
	}
	
}
