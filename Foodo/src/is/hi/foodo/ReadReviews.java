package is.hi.foodo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class ReadReviews extends ListActivity implements Runnable {
	
	private static final String TAG = "FoodoReviews";
	
	private static final String TITLE = "TITLE";
	private static final String REVIEW = "REVIEW";
	private static final String DATE = "DATE";
	
	private ProgressDialog pd;
	private ReviewWebService mService;
	private RestaurantDbAdapter mDbHelper;
	private Long mRowId;	
	
	private Cursor mRestaurantCursor;
	
	List< Map<String,String> > mReviews;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.reviews); 
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
		
		mService = new ReviewWebService();
        
		//gatherList();
		getListView().setTextFilterEnabled(true);
		getListView().setClickable(false);
		registerForContextMenu(getListView());
		
		//Check if resuming from a saved instance state
        mRowId = (savedInstanceState != null ? savedInstanceState.getLong(RestaurantDbAdapter.KEY_ROWID) : null);
        //Get id from intent if not set
        if (mRowId == null)
        {
        	Bundle extras = getIntent().getExtras();
        	mRowId = extras != null ? extras.getLong(RestaurantDbAdapter.KEY_ROWID) : null;
        }
        
        Log.d(TAG, "ReId is: " + mRowId);
        
        populateView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mReviews = new ArrayList<Map<String,String>>();
		loadReviews();
	}
	
	
	private void populateView() {
		if (mRowId != null)
    	{
    		Cursor restaurant = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(restaurant);
    		
    		TextView mPlaceName = (TextView) this.findViewById(R.id.reviewPlace);
    		mPlaceName.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    	}
	}
	
	public void setupList() {
	
        SimpleAdapter adapter = new SimpleAdapter(
        		this,
        		mReviews, 
        		R.layout.listreview,
        		new String[] { TITLE, REVIEW, DATE },
        		new int[] { R.id.reviewName, R.id.reviewText, R.id.reviewDate }
        );
        
        setListAdapter(adapter);
	}

	
	/*
	public void gatherList() {
		
		//Get all rows from database
		mRestaurantCursor = mDbHelper.fetchAllRestaurants(Filter.ratingFrom,
				Filter.ratingTo,
				Filter.lowprice,
				Filter.mediumprice,
				Filter.highprice,
				Filter.checkedTypes,
				Filter.typesId);
		startManagingCursor(mRestaurantCursor);
		
		
        //Create an array of fields we want to display
		String[] from = new String[]{RestaurantDbAdapter.KEY_NAME, RestaurantDbAdapter.KEY_RATING };
		
		//Fields we want to bind to
		int[] to = new int[]{R.id.reviewName, R.id.reviewText};
        
        SimpleCursorAdapter adapt = new SimpleCursorAdapter(this, R.layout.listreview, mRestaurantCursor, from, to);

        
        setListAdapter(adapt);
	}*/
	
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,0,0,"Write a review");
		 return true;
	}
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 0:
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			if(settings.getBoolean("access", false)){
				Intent writer = new Intent(this, WriteReviews.class);
				writer.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
				startActivityForResult(writer, 1);
				return true;
			}
			else{
				Context context = getApplicationContext();
				Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
			}
		
		}
		return false;
	}
	
	private void loadReviews() {
		pd = ProgressDialog.show(ReadReviews.this, "Loading..", "Updating");
		Thread thread = new Thread(ReadReviews.this);
		thread.start();
	}

	@Override
	public void run() {
		
		JSONArray jReviews = mService.getReviews(mRowId);
		
		int n = jReviews.length();
		
		try {
			for (int i = 0; i < n; i++)
			{
				JSONObject r = jReviews.getJSONObject(i);
				Map<String,String> map = new HashMap<String, String>();
				map.put(TITLE, r.getString("user"));
				map.put(REVIEW, r.getString("review"));
				map.put(DATE, r.getString("created_at"));
				mReviews.add(map);
			}
		}
		catch (JSONException e) {
			//TODO 
		}
		
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			setupList();
		}
	};

}

