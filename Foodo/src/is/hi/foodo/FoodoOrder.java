package is.hi.foodo;

import is.hi.foodo.net.FoodoServiceException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class FoodoOrder extends Activity implements Runnable {

	private static final String TAG = "FoodoOrder";

	public static final int DETAILS_VIEW = 0;
	public static final int FOODOORDER_VIEW = 1;

	public static final String FOODO_ORDER = "FOODO_ORDER";
	public static final String ORDER_ID = "ORDER_ID";
	public static final String RESTAURANT_NAME = "RESTAURANT_NAME";
	public static final String RESTAURANT_ID = "RESTAURANT_ID";

	private Long order_id, restaurant_id;
	private String restaurantName;
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
			restaurantName = extras != null ? extras.getString(RESTAURANT_NAME) : null;
			restaurant_id = extras != null ? extras.getLong(RESTAURANT_ID) : null;
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
		TextView tRestaurantName = (TextView) findViewById(R.id.tRestaurantName);
		if(restaurantName != null){
			tRestaurantName.setText(restaurantName);
		}
		else{
			tRestaurantName.setText("");
		}

		Button bGoToRestaurant = (Button) findViewById(R.id.goRestaurant);

		bGoToRestaurant.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent details = new Intent(getApplicationContext(), FoodoDetails.class);
				details.putExtra(RestaurantDbAdapter.KEY_ROWID, restaurant_id);
				details.putExtra(FOODO_ORDER, FOODOORDER_VIEW);
				startActivityForResult(details, DETAILS_VIEW);			
			}
		});
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
