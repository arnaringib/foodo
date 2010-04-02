package is.hi.foodo;


import is.hi.foodo.net.FoodoService;
import is.hi.foodo.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FoodoUserOldReviews extends Activity {

	private static final String TAG = "FoodoReviews";

	private static final String RESTAURANT_ID = "restaurant_id";
	private static final String RESTAURANT = "restaurant";
	private static final String CREATED = "created";
	private static final String DESCRIPTION = "description";

	private ProgressDialog pd;
	private FoodoService mService;
	private RestaurantDbAdapter mDbHelper;
	private Cursor restaurant;
	private UserManager uManager;
	private ListView lReviews;
	private TextView tNoReviews;
	private JSONArray userReviews;
	private ArrayList<Map<String,String>> mReviews;
	//private Button btnEditReview;

	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		setContentView(R.layout.useroldreviews); 

		mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();

		mService = ((FoodoApp)this.getApplicationContext()).getService();
		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();

		mReviews = new ArrayList<Map<String,String>>();

		pd = ProgressDialog.show(FoodoUserOldReviews.this, "Working", "Getting reviews...");


		// Get all reviews from this particular user.
		Thread thread = new Thread( new Runnable(){
			public void run(){
				try {
					userReviews = mService.getUserReviews(uManager.getApiKey());
				} catch (Exception e) {
					Log.d(TAG, "Exception in mService.getUserReviews");
					Log.d(TAG, e.toString());
				}
				hDialogs.sendEmptyMessage(0);
			}
		});
		thread.start();		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}

	/*@Override
	protected void onResume() {
		super.onResume();

		mReviews = new ArrayList<Map<String,String>>();
		//loadReviews();
	}*/

	/**
	 * Setup for the view of old orders,
	 * also creates a listener for the view.
	 * When a order is clicked on a dialog opens 
	 * with the order.
	 */
	private void setupView(){
		lReviews = (ListView) findViewById(R.id.listoldreviews);
		tNoReviews = (TextView) findViewById(R.id.noReviews);

		/*lReviews.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int id,
					long arg3) {
				dViewOldReviews = new Dialog(view.getContext());
				dViewOldReviews.setContentView(R.layout.userviewoldorderdialog);
				dViewOldReviews.setTitle(mReviews.get(id).get(RESTAURANT));
				dViewOldReviews.setCanceledOnTouchOutside(true);
				dViewOldReviews.show();

				tTotalReviews = (TextView) dViewOldReviews.findViewById(R.id.noReviews);
				try {
					JSONObject review = userReviews.getJSONObject(id);
					//tTotalReviews.setText(createReview(review));
				} 	
				catch (Exception e) {
					Log.d(TAG, "Exception in getUserReviews");
					Log.d(TAG, e.toString());
				}			
			}
		});*/
	}


	/**
	 * Fills the list view with former reviews from user if any.
	 */
	private void fillList(){
		try{
			if(userReviews.length() > 0){
				for(int i = 0; i < userReviews.length(); i++){

					JSONObject review = userReviews.getJSONObject(i);
					restaurant = mDbHelper.fetchRestaurant(review.getInt(RESTAURANT_ID));
					Map<String,String> mReview = new HashMap<String, String>();
					mReview.put(RESTAURANT, 
							restaurant.getString(
									restaurant.getColumnIndex(
											RestaurantDbAdapter.KEY_NAME)));
					mReview.put(CREATED, review.getString(CREATED));
					mReview.put(DESCRIPTION, review.getString(DESCRIPTION));
					mReviews.add(mReview);

				}
			}else{
				tNoReviews.setVisibility(TextView.VISIBLE);
			}

			// Create an array of fields we want to display
			String[] from = new String[]{RESTAURANT, CREATED, DESCRIPTION};

			// Fields we want to bind to
			int[] to = new int[]{R.id.tRestaurant, R.id.tCreated, R.id.tDescription};

			SimpleAdapter adapter = new SimpleAdapter(this,
					mReviews,R.layout.listreviewrow, from, to);
			lReviews.setAdapter(adapter);
		}
		catch (Exception e) {
			Log.d(TAG, "Exception in getUserReviews");
			Log.d(TAG, e.toString());
		}
	}

	/**
	 * Thread handler, used to handle getting 
	 * all reviews from the server and setup the views
	 * and filling the list view.
	 */
	private final Handler hDialogs  = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			setupView();
			fillList();
		}
	};
}

