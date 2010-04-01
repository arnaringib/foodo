package is.hi.foodo;

import is.hi.foodo.net.FoodoService;
import is.hi.foodo.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class FoodoUserOldOrders extends Activity{

	private static final String TAG = "FoodoOldOrders";
	private static final String RESTAURANT_ID = "restaurant_id";
	private static final String RESTAURANT = "restaurant";
	private static final String CREATED = "created";
	private static final String AMOUNT = "amount";
	private static final String ORDERLINES = "orderlines";
	private static final String COUNT = "count";
	private static final String PRICE = "price";
	private static final String MENUITEM = "menuitem";
	private static final String MENUITEM_ID = "menuitem_id";

	private static final String NEWLINE = "\n";
	private static final String TAB = "\t";
	private static final String TIMES = "x";

	private static final int GETORDERS = 0;
	private static final int SENDORDER = 1;

	private RestaurantDbAdapter mDbHelper;
	private Cursor restaurant;
	private UserManager uManager;
	private FoodoService mService;

	private ListView lOrders;
	private TextView tHaventOrderd, tTotalOldOrder;
	private Dialog dUserViewOldOrder;
	private ProgressDialog dProgressDialog;
	private AlertDialog aAskSendOrder;
	private Button bUseOldOrder;

	private ArrayList<Map<String,String>> mOrders;
	private int mCurSelectedItem;
	private JSONArray userOrders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.useroldorders);

		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();
		mService = ((FoodoApp)this.getApplicationContext()).getService();

		//Open connection to DB adapter
		mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open(); 

		dProgressDialog = ProgressDialog.show(FoodoUserOldOrders.this, "Working", "Getting orders...");
		getUserOrders();

		AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
		alertBuild.setMessage(R.string.sure_send_this_order)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				sendOrder();
			}
		})
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				dialog.dismiss();
			}
		});
		aAskSendOrder = alertBuild.create();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
		restaurant.deactivate();
	}

	/**
	 * Setup for the view of old orders,
	 * also creates a listener for the view.
	 * When a order is clicked on a dialog opens 
	 * with the order.
	 */
	private void setupView(){
		lOrders = (ListView) findViewById(R.id.listuserorders);
		tHaventOrderd = (TextView) findViewById(R.id.tHaventOrdered);

		lOrders.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int id,
					long arg3) {
				mCurSelectedItem = id;

				dUserViewOldOrder = new Dialog(view.getContext());
				dUserViewOldOrder.setContentView(R.layout.userviewoldorderdialog);
				dUserViewOldOrder.setTitle(mOrders.get(id).get(RESTAURANT));
				dUserViewOldOrder.setCanceledOnTouchOutside(true);
				dUserViewOldOrder.show();

				tTotalOldOrder = (TextView) dUserViewOldOrder.findViewById(R.id.tTotalOldOrder);
				try {
					JSONObject order = userOrders.getJSONObject(id);
					tTotalOldOrder.setText(createOrder(order));
				} catch (Exception e) {
					Log.d(TAG, "Exception in setupView");
					Log.d(TAG, e.toString());
				}

				bUseOldOrder = (Button) dUserViewOldOrder.findViewById(R.id.bUseOldOrder);

				bUseOldOrder.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						aAskSendOrder.show();
					}
				});				
			}
		});
	}

	/**
	 * Fills the list view with the orders.
	 */
	private void fillList(){
		mOrders = new ArrayList<Map<String,String>>();

		try{
			if(userOrders.length() > 0){
				for(int i = 0; i < userOrders.length(); i++){
					JSONObject order = userOrders.getJSONObject(i);
					JSONArray orderlines = order.getJSONArray(ORDERLINES);
					restaurant = mDbHelper.fetchRestaurant(order.getInt(RESTAURANT_ID));

					int sum = 0;

					for(int j = 0; j < orderlines.length(); j++){
						// sum contains the sum of the order.
						sum += orderlines.getJSONObject(j).getInt(COUNT) * 
						orderlines.getJSONObject(j).getInt(PRICE);
					}

					Map<String,String> mOrder = new HashMap<String, String>();
					mOrder.put(RESTAURANT, 
							restaurant.getString(
									restaurant.getColumnIndex(
											RestaurantDbAdapter.KEY_NAME)));
					mOrder.put(CREATED, order.getString(CREATED));
					mOrder.put(AMOUNT, Integer.toString(sum));

					mOrders.add(mOrder);

					restaurant.close();

				}
			}
			else{
				tHaventOrderd.setVisibility(TextView.VISIBLE);
			}

			// Create an array of fields we want to display
			String[] from = new String[]{RESTAURANT, CREATED, AMOUNT};

			// Fields we want to bind to
			int[] to = new int[]{R.id.tRestaurant, R.id.tCreated, R.id.tAmount};

			SimpleAdapter adapter = new SimpleAdapter(this,
					mOrders,R.layout.listuserordersrow, from, to);
			lOrders.setAdapter(adapter);
		}
		catch (Exception e) {
			Log.d(TAG, "Exception in getUserOrders");
			Log.d(TAG, e.toString());
		}
	}

	/**
	 * Creates the order for the order dialog.
	 * @param order
	 * @return result
	 * @throws JSONException
	 */
	private String createOrder(JSONObject order) throws JSONException{
		String result = "";
		JSONArray orderlines = order.getJSONArray(ORDERLINES);

		int sum = 0;
		for(int i = 0; i < orderlines.length(); i++){

			result += orderlines.getJSONObject(i).getString(MENUITEM);
			result += NEWLINE;
			result += Integer.toString(orderlines.getJSONObject(i).getInt(COUNT));
			result += TIMES;
			result += Integer.toString(orderlines.getJSONObject(i).getInt(PRICE));
			result += TAB;
			result += "=";
			result += TAB;
			result += orderlines.getJSONObject(i).getInt(COUNT) * 
			orderlines.getJSONObject(i).getInt(PRICE);
			result += NEWLINE;
			result += NEWLINE;
			sum += orderlines.getJSONObject(i).getInt(COUNT) * 
			orderlines.getJSONObject(i).getInt(PRICE);
		}
		result += NEWLINE;
		result += "Total cost: "+ sum;
		Log.d(TAG, result);
		return result;
	}

	/**
	 * Creates an order from the old order to send.
	 * @param order
	 * @return items
	 * @throws JSONException
	 */
	private List<Map<String,String>> getItems(JSONObject order) throws JSONException{
		ArrayList<Map<String,String>> items = new ArrayList<Map<String,String>>();

		JSONArray orderlines = order.getJSONArray(ORDERLINES);
		for(int i = 0; i < orderlines.length(); i++){
			Map<String, String> map = new HashMap<String, String>();
			map.put(FoodoMenu.ITEMID, Integer.toString(orderlines.getJSONObject(i).getInt(MENUITEM_ID)));
			map.put(FoodoMenu.AMOUNT, Integer.toString(orderlines.getJSONObject(i).getInt(COUNT)));
			items.add(map);
		}
		return items;
	}

	/**
	 * Gets all the user orders.
	 */
	private void getUserOrders(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				try {
					userOrders = mService.getUserOrders(uManager.getApiKey());
				} catch (Exception e) {
					Log.d(TAG, "Exception in mService.getUserOrders");
					Log.d(TAG, e.toString());
				}
				hDialogs.sendEmptyMessage(GETORDERS);
			}
		});
		thread.start();
	}

	/**
	 * Sends the selected order.
	 */
	private void sendOrder(){
		dProgressDialog = ProgressDialog.show(FoodoUserOldOrders.this, "Working", "Sending order...");

		Thread thread = new Thread( new Runnable(){
			public void run(){
				JSONObject order;
				try {
					order = userOrders.getJSONObject(mCurSelectedItem);
					mService.submitOrder(order.getLong(RESTAURANT_ID),
							uManager.getApiKey(),
							getItems(order));
				} catch (Exception e) {
					Log.d(TAG, "Exception in sending Order");
					Log.d(TAG, e.toString());
				}
				hDialogs.sendEmptyMessage(SENDORDER);				
			}
		});
		thread.start();
		dUserViewOldOrder.dismiss();
	}

	/**
	 * Thread handler, used to handle getting 
	 * all orders from the server and setup the views
	 * and filling the list view.
	 */
	private final Handler hDialogs  = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SENDORDER){
				getUserOrders();
				Toast.makeText(FoodoUserOldOrders.this.getBaseContext(), R.string.order_processed, Toast.LENGTH_LONG).show();
			}
			setupView();
			fillList();
			dProgressDialog.dismiss();
		}
	};

}
