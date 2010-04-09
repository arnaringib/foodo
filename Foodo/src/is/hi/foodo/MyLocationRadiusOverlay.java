package is.hi.foodo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;

public class MyLocationRadiusOverlay extends MyLocationOverlay {

	public static final String TAG = "MyLocationRadiusOverlay";

	private final Paint circlePaint;

	private float circleRadius;

	public MyLocationRadiusOverlay(Context context, MapView mapView) {
		super(context, mapView);

		circlePaint = new Paint();
		circlePaint.setColor(Color.CYAN);
		circlePaint.setAlpha(32);

	}
	@Override
	public synchronized void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		//TODO update bounding box here
	}
	@Override
	public synchronized boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {

		Projection projection = mapView.getProjection();

		GeoPoint gp = getMyLocation();

		if (gp != null)
		{
			Point p = new Point();
			projection.toPixels(gp, p);

			circleRadius = projection.metersToEquatorPixels(Filter.radius*2);
			canvas.drawCircle(p.x, p.y, circleRadius, circlePaint);
		}

		return super.draw(canvas, mapView, shadow, when);
	}





}
