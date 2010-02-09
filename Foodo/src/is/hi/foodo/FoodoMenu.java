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
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class FoodoMenu extends ListActivity implements Runnable {
	
	private static final String TAG = "FoodoMap";
	
	private static final String TITLE = "Burger Joint";
	private static final String NUMBER = "1.";
	private static final String PRICE = "6.99$";
	private static final String ITEMNAME = "Hamburger";
	
	private ProgressDialog pd;
	private ReviewWebService mService; // TODO !
	private RestaurantDbAdapter mDbHelper;
	private Long mRowId;	
	
	private Button btnConfOrder;
	
	//private Cursor mRestaurantCursor;
	
	List< Map<String,String> > mMenu;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.menu); 
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
		
		mService = new ReviewWebService(); // TODO !
        
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
		
		mMenu = new ArrayList<Map<String,String>>();
		loadMenu();
	}
	
	
	private void populateView() {
		if (mRowId != null)
    	{
    		Cursor restaurant = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(restaurant);
    		
    		TextView mPlaceName = (TextView) this.findViewById(R.id.menuPlace);
    		mPlaceName.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    	}
	}
	
	public void setupList() {
	
        SimpleAdapter adapter = new SimpleAdapter(
        		this,
        		mMenu, 
        		R.layout.listmenu,
        		new String[] { TITLE , NUMBER, ITEMNAME, PRICE },
        		new int[] { R.id.menuPlace, R.id.nrMenu, R.id.nameMenu, R.id.priceMenu }
        );
        
        setListAdapter(adapter);
	}

	public void writeReviews(){	// TODO !
		Intent i = new Intent(this, WriteReviews.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i, 1);
	}
	public void setupButtons() {
		this.btnConfOrder = (Button)this.findViewById(R.id.bConfOrder);
		
		btnConfOrder.setOnClickListener(new clicker());
			
	}
	// button click listener
	class clicker implements Button.OnClickListener
    {     

		public void onClick(View v)
		{
			Context context = getApplicationContext();
			if(v==btnConfOrder){
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FoodoMenu.this);
				if(settings.getBoolean("access", true)){
					writeReviews();	// TODO !
				}
				else {
					Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
				}
			}
		
		}
    }	
	
	
	private void loadMenu() {
		pd = ProgressDialog.show(FoodoMenu.this, "Loading..", "Updating");
		Thread thread = new Thread(FoodoMenu.this);
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
				map.put(NUMBER, r.getString("review"));
				map.put(ITEMNAME, r.getString("created_at"));
				map.put(PRICE, r.getString("review"));
				mMenu.add(map);
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

