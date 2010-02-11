package is.hi.foodo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class FoodoMenu extends ListActivity implements Runnable {

	private static final String TAG = "FoodoMenu";
	private static final int ORDER_VIEW = 3;
	private ArrayList<String[]> order;
	
	private static final String FULLORDER = "FullOrder";
	private static final String RESTNAME = "RestName";
	
	private static final String TITLE = "Burger Joint";
	private static final String NUMBER = "number";
	private static final String PRICE = "price";
	private static final String ITEMNAME = "itemname";
	
	private static int item;
	
	private ProgressDialog pd;
	private ReviewWebService mService; // TODO !
	private RestaurantDbAdapter mDbHelper;
	private Long mRowId;	
	
	private Button btnConfOrder;
	
	static final int MENU_DIALOG = 0;
	//private Cursor mRestaurantCursor;
	
	List< Map<String,String> > mMenu;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.menu); 
        
        order = new ArrayList<String[]>();
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
		
		mService = new ReviewWebService(); // TODO !
        
		getListView().setTextFilterEnabled(true);
		getListView().setClickable(true);
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
        
        //populateView();
        
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		mMenu = new ArrayList<Map<String,String>>();
        // DUMMY DATA
		Map<String,String> map = new HashMap<String, String>();
		map.put(NUMBER, "1");
		map.put(ITEMNAME, "Grilled langoustine with chili-garlic butter and salad");
		map.put(PRICE, "5200");
		mMenu.add(map);
		map = new HashMap<String, String>();
		map.put(NUMBER, "2");
		map.put(ITEMNAME, "Turbot, cauliflower, grapes and almonds");
		map.put(PRICE, "5200");
		mMenu.add(map);
		map = new HashMap<String, String>();
		map.put(NUMBER, "3");
		map.put(ITEMNAME, "Fillet of lamb, shoulder and beatroot");
		map.put(PRICE, "5200");
		mMenu.add(map);   
		//loadMenu();
		setupList();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ORDER_VIEW) {
            if (resultCode == RESULT_OK) {
            	setResult(RESULT_OK);
                finish();
            }
        }
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
        		new String[] { NUMBER, ITEMNAME, PRICE },
        		new int[] { R.id.nrMenu, R.id.nameMenu, R.id.priceMenu }
        );
        
        setListAdapter(adapter);
	}
	
	public String createOrder(){
		//TODO: Dummy!
		String result = "";
		int total = 0;
		result += mMenu.get(item).get(ITEMNAME) + " \n " + mMenu.get(item).get(PRICE) + " kr. X 1\n";
		total += Integer.parseInt(mMenu.get(1).get(PRICE));
		result += "--------------------\n Total: " + total + " kr." ;
		return result;
	}

	public void order(){		
		String fullOrder = createOrder();
		Intent i = new Intent(this, FoodoOrder.class);
		i.putExtra(FULLORDER, fullOrder);
		i.putExtra(RESTNAME, TITLE);
		startActivityForResult(i, ORDER_VIEW);
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
					order();
				}
				else {
					Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
				}
			}
		
		}
    }	
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case MENU_DIALOG:
		            LayoutInflater factory = LayoutInflater.from(this);
		            final View layout = factory.inflate(R.layout.menudialog, null);
	            	
		       //   RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
	            	//final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		            return new AlertDialog.Builder(FoodoMenu.this)
		                .setTitle(mMenu.get(item).get(ITEMNAME))
		                .setView(layout)
		                .setPositiveButton("Add to Order", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		                    	/* User clicked OK, add new rating */
		                    }
		                })
		                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		                    	dismissDialog(MENU_DIALOG);
		                    }
		                })
		                .create();
		        }
		return null;
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		//Context context = getApplicationContext();
        //Toast.makeText(context,""+ position, Toast.LENGTH_SHORT).show();
		v.refreshDrawableState();
		item = position;
		showDialog(MENU_DIALOG);
	}
	
	/*private void loadMenu() {
		pd = ProgressDialog.show(FoodoMenu.this, "Loading..", "Updating");
		Thread thread = new Thread(FoodoMenu.this);
		thread.start();
	}*/

	@Override
	public void run() {
		
		JSONArray jReviews = mService.getReviews(mRowId);
		
		int n = jReviews.length();
		
		try {
			for (int i = 0; i < n; i++)
			{
				JSONObject r = jReviews.getJSONObject(i);
				/*Map<String,String> map = new HashMap<String, String>();
				map.put(TITLE, r.getString("user"));
				map.put(NUMBER, r.getString("review"));
				map.put(ITEMNAME, r.getString("created_at"));
				map.put(PRICE, r.getString("review"));
				mMenu.add(map);*/
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

