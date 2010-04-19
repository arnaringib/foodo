package is.hi.foodo;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FoodoOrderService extends Service {

	public final static String TAG = "FoodoOrderService";

	public final static int FOODO_NOTIFICATION_ID = 314159265;

	private final Timer timer = new Timer();
	private final static int INTERVAL = 60000; //milliseconds

	private NotificationManager mNotificationManager;
	private FoodoApp app;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Staring Service");
		mNotificationManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		timer.scheduleAtFixedRate(task, 0, INTERVAL);
		app = ((FoodoApp)this.getApplicationContext());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (timer != null) {
			timer.cancel();
		}
	}

	public void makeNotification(String restaurantName, long orderId, long restaurantId) {
		Log.d(TAG, "Creating notification: " + restaurantName + ", order: " + orderId);
		Intent intent = new Intent(this, FoodoOrder.class);
		intent.putExtra(FoodoOrder.ORDER_ID, orderId);
		intent.putExtra(FoodoOrder.RESTAURANT_NAME, restaurantName);
		intent.putExtra(FoodoOrder.RESTAURANT_ID, restaurantId);

		Notification notification = new Notification(R.drawable.icon, "Your Foodo order!", System.currentTimeMillis());
		notification.setLatestEventInfo(
				FoodoOrderService.this, 
				"Foodo", 
				restaurantName + " order confirmed!", 
				PendingIntent.getActivity(this.getBaseContext(), 0, intent,	PendingIntent.FLAG_CANCEL_CURRENT)
		);
		mNotificationManager.notify(FOODO_NOTIFICATION_ID, notification);
	}

	public void cancelNotification() {
		mNotificationManager.cancel(FOODO_NOTIFICATION_ID);
	}

	private final TimerTask task = new TimerTask() {
		@Override
		public void run() {
			String api_key = app.getUserManager().getApiKey();
			try {
				JSONArray notifications = app.getService().getNotifications(api_key);
				Log.d(TAG, notifications.toString());
				int n = notifications.length();
				if (n > 0)
				{
					for (int i = 0; i < n; i++)
					{
						JSONObject not = notifications.getJSONObject(i);
						Log.d(TAG, not.toString());
						makeNotification(not.getString("restaurant"), not.getLong("order_id"), not.getLong("restaurant_id"));
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "Exception in FoodoOrderService", e);
			}
		}
	};
}
