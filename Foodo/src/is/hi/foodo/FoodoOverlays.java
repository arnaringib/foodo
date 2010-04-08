package is.hi.foodo;

import java.util.ArrayList;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class FoodoOverlays extends ItemizedOverlay<FoodoOverlayItem> {
	private ArrayList<FoodoOverlayItem> mRestaurantsOverlays = new ArrayList<FoodoOverlayItem>();
	private final MapView mapView;
	private int last;
	private Dialog dialog;

	private final Paint text;
	private final Paint box;

	private final int textOffset = 10;

	public FoodoOverlays(Drawable defaultMarker, MapView mapView) {
		super(boundCenterBottom(defaultMarker));
		this.mapView = mapView;

		//Style for text
		text = new Paint();
		text.setAntiAlias(true);
		text.setColor(Color.WHITE);
		text.setTextSize(12);
		text.setTypeface(Typeface.DEFAULT_BOLD);

		//Style for text background
		box = new Paint();
		box.setColor(Color.BLACK);
		box.setAlpha(96);


		populate();
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

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		super.draw(canvas, mapView, shadow);

		Projection projection = mapView.getProjection();
		int n = this.size();
		for (int i = 0; i < n; i++)
		{
			FoodoOverlayItem r = getItem(i);

			Point point = new Point();
			projection.toPixels(r.getPoint(), point);

			String title = r.getTitle();

			Rect rect = new Rect();
			text.getTextBounds(title, 0, title.length(), rect);

			rect.bottom = rect.bottom+10;
			rect.right = rect.right+10;
			rect.offset(point.x+textOffset-5, point.y-5);

			canvas.drawRect(rect, box);
			canvas.drawText(r.getTitle(), point.x+textOffset, point.y, text);
		}

	}

}
