package is.hi.foodo;


import is.hi.foodo.net.FoodoService;
import is.hi.foodo.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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

public class FoodoUserOldReviews extends Activity {

	private static final String TAG = "FoodoReviews";

	private static final String RESTAURANT_ID = "restaurant_id";
	private static final String RESTAURANT = "restaurant";
	private static final String CREATED = "created";
	private static final String DESCRIPTION = "description";
	private static final String ID = "id";

	private ProgressDialog pd;
	private Dialog dViewOldReviews;
	private FoodoService mService;
	private RestaurantDbAdapter mDbHelper;
	private Cursor restaurant;
	private UserManager uManager;
	private ListView lReviews;
	private TextView tNoReviews, tOldReview, tEditOldReview;
	private JSONArray userReviews;
	private ArrayList<Map<String,String>> mReviews;
	private Button btnEditReview, btnDeleteReview;
	private int mCurSelectedItem;

	private static final int GETREVIEWS = 0;
	private static final int EDITREVIEW = 1;
	private static final int DELETEREVIEW = 2;

	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		setContentView(R.layout.useroldreviews); 

		mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();

		mService = ((FoodoApp)this.getApplicationContext()).getService();
		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();

		pd = ProgressDialog.show(FoodoUserOldReviews.this, "Working", "Getting reviews...");

		getReviews();

	}


	/*
	 * Get all reviews from this particular user.
	 */
	private void getReviews(){
		Thread thread = new Thread( new Runnable(){
			public void run(){
				try {
					userReviews = mService.getUserReviews(uManager.getApiKey());
				} catch (Exception e) {
					Log.d(TAG, "Exception in mService.getUserReviews");
					Log.d(TAG, e.toString());
				}
				hDialogs.sendEmptyMessage(GETREVIEWS);
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
	 * with a listener.
	 * When a review is clicked a dialog opens 
	 * with editable review.
	 */
	private void setupView(){
		lReviews = (ListView) findViewById(R.id.listoldreviews);
		tNoReviews = (TextView) findViewById(R.id.noReviews);

		lReviews.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int id,
					long arg3) {
				mCurSelectedItem = id;

				dViewOldReviews = new Dialog(view.getContext());
				dViewOldReviews.setContentView(R.layout.oldreviewdialog);
				dViewOldReviews.setTitle(mReviews.get(id).get(RESTAURANT));
				dViewOldReviews.setCanceledOnTouchOutside(true);
				dViewOldReviews.show();

				tOldReview = (TextView) dViewOldReviews.findViewById(R.id.oldReview);
				tEditOldReview = (TextView) dViewOldReviews.findViewById(R.id.eOldReview);
				try {
					JSONObject review = userReviews.getJSONObject(id);
					tOldReview.setText(review.getString(CREATED));
					tEditOldReview.setText(review.getString(DESCRIPTION));
				} 	
				catch (Exception e) {
					Log.d(TAG, "Exception in getUserReviews");
					Log.d(TAG, e.toString());
				}	

				btnEditReview = (Button) dViewOldReviews.findViewById(R.id.bEditReview);

				btnEditReview.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v) {
						editReview();
					}
				});	

				btnDeleteReview = (Button) dViewOldReviews.findViewById(R.id.bDeleteReview);

				btnDeleteReview.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick (View v){
						deleteReview();
					}
				});			
			}	
		});
	}

	/**
	 * Fills the list view with former reviews from user if any.
	 */
	private void fillList(){
		mReviews = new ArrayList<Map<String,String>>();
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
					mReview.put(ID, review.getString(ID));
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

	private void editReview(){
		pd = ProgressDialog.show(FoodoUserOldReviews.this, "Working", "Sending review...");

		Thread thread = new Thread( new Runnable(){
			public void run(){
				JSONObject review;
				try {
					review = userReviews.getJSONObject(mCurSelectedItem);
					mService.editUserReview(review.getLong(RESTAURANT_ID),
							review.getLong(ID),
							uManager.getApiKey(),
							tEditOldReview.getText().toString());
				} catch (Exception e) {
					Log.d(TAG, "Exception in editing review");
					Log.d(TAG, e.toString());
				}
				hDialogs.sendEmptyMessage(1);				
			}
		});
		thread.start();
		dViewOldReviews.dismiss();
	}
	private void deleteReview(){
		pd = ProgressDialog.show(FoodoUserOldReviews.this, "Working", "Deleting review...");

		Thread thread = new Thread( new Runnable(){
			public void run(){
				JSONObject review;
				try {
					review = userReviews.getJSONObject(mCurSelectedItem);
					mService.deleteUserReview(
							review.getLong(ID),
							uManager.getApiKey());
				} catch (Exception e) {
					Log.d(TAG, "Exception in deleting review");
					Log.d(TAG, e.toString());
				}
				hDialogs.sendEmptyMessage(DELETEREVIEW);				
			}
		});
		thread.start();
		dViewOldReviews.dismiss();
	}
	/**
	 * Thread handler, used to handle getting 
	 * all reviews from the server, setup the views
	 * and filling the list view.
	 */
	private final Handler hDialogs  = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == EDITREVIEW){
				getReviews();
				Toast.makeText(FoodoUserOldReviews.this.getBaseContext(), R.string.review_edited, Toast.LENGTH_SHORT).show();
			}else if(msg.what == DELETEREVIEW){
				getReviews();
				Toast.makeText(FoodoUserOldReviews.this.getBaseContext(), R.string.review_deleted, Toast.LENGTH_SHORT).show();
			}
			else if(msg.what == GETREVIEWS){
				setupView();
				fillList();
				pd.dismiss();
			}


		}
	};
}

