package is.hi.foodo;

import is.hi.foodo.net.FoodoServiceException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class FoodoOrder extends Activity implements Runnable {

	private static final String TAG = "FoodoOrder";

	public static final String ORDER_ID = "ORDER_ID";

	private Long order_id;
	private ProgressDialog pd;

	private JSONObject order;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.foodoorder);

		order_id = (savedInstanceState != null ? savedInstanceState.getLong(ORDER_ID) : null);
		if (order_id == null)
		{
			Bundle extras = getIntent().getExtras();
			order_id = extras != null ? extras.getLong(ORDER_ID) : null;
		}

		NotificationManager nManager = (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
		nManager.cancel(FoodoOrderService.FOODO_NOTIFICATION_ID);

		pd = ProgressDialog.show(FoodoOrder.this, "Working..", "Loading order");
		Thread thread = new Thread(FoodoOrder.this);
		thread.run();
	}

	private void populateView() {
		if (order != null)
		{	
			Log.d(TAG, "We haz order: " + order.toString());
			TextView orderline_view = (TextView) this.findViewById(R.id.listOrder);

			try {
				String orderline_str = "";
				JSONArray orderlines = order.getJSONArray("orderlines");
				for (int i = 0; i < orderlines.length(); i++)
				{
					JSONObject line = orderlines.getJSONObject(i);
					orderline_str = 
						line.getString("menuitem") +
						line.getInt("count") + " x " + line.getInt("price") + 
						" = " + line.getInt("count") * line.getInt("price") + 
						"\n";
				}
				orderline_str += "--------------------\n";
				orderline_str += "Total Price: " + order.getInt("totalprice");
				orderline_view.setText(orderline_str);
			}
			catch (Exception e)
			{
				Log.d(TAG, "Failure", e);
			}
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			FoodoApp app = (FoodoApp)this.getApplicationContext();
			order = app.getService().getOrder(order_id, app.getUserManager().getApiKey());
		} catch (FoodoServiceException e) {
			Log.d(TAG, "Exception", e);
		}
		handler.sendEmptyMessage(0);
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			populateView();
		}
	};
}
