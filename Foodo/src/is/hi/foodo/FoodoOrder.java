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
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FoodoOrder extends ListActivity implements Runnable {
	
	private static final String TAG = "FoodoOrder";
	
	private static final String TITLE = "TITLE"; // TODO
	private static final String REVIEW = "REVIEW";
	private static final String DATE = "DATE";
	
	private ProgressDialog pd;
	private ReviewWebService mService; // TODO
	private RestaurantDbAdapter mDbHelper;
	private Long mRowId;	
	
	private Button btnConfOrder, btnChangeOrder;
	
	//private Cursor mRestaurantCursor;
	
	List< Map<String,String> > mOrder;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.order); 
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
		
		mService = new ReviewWebService(); // TODO
        
		getListView().setTextFilterEnabled(true);
		getListView().setClickable(false);
		registerForContextMenu(getListView());
		
		setupButtons();
		
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
		
		mOrder = new ArrayList<Map<String,String>>();
		loadReviews();
	}
	
	
	private void populateView() {
		if (mRowId != null)
    	{
    		Cursor restaurant = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(restaurant);
    		
    		TextView mPlaceName = (TextView) this.findViewById(R.id.orderTitle);
    		mPlaceName.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    	}
	}
	
	public void setupList() {
	
        SimpleAdapter adapter = new SimpleAdapter(
        		this,
        		mOrder, 
        		R.layout.listreview, // TODO
        		new String[] { TITLE, REVIEW, DATE },
        		new int[] { R.id.reviewName, R.id.reviewText, R.id.reviewDate }
        );
        
        setListAdapter(adapter);
	}

	public void confOrder(){
		setResult(RESULT_OK);
		finish();
	}
	
	public void changeOrder(){
		finish();
	}
	
	public void setupButtons() {
		this.btnConfOrder = (Button)this.findViewById(R.id.bConfOrder);
		this.btnChangeOrder = (Button)this.findViewById(R.id.bChangeOrder);
		
		btnConfOrder.setOnClickListener(new clicker());
		btnChangeOrder.setOnClickListener(new clicker());
	}
	// button click listener
	class clicker implements Button.OnClickListener
    {     

		public void onClick(View v)
		{
			if(v == btnConfOrder){
				confOrder();
			}
			else if(v == btnChangeOrder){
				changeOrder();
			}
		}
    }	
	
	
	private void loadReviews() {
		pd = ProgressDialog.show(FoodoOrder.this, "Loading..", "Updating");
		Thread thread = new Thread(FoodoOrder.this);
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
				mOrder.add(map);
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

