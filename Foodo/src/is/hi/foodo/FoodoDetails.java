package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class FoodoDetails extends Activity{
	
	private static final String TAG = "FoodoDetails";
	
	static final int RATING_DIALOG = 0;
	private RatingBar showRatingbar;
	public RatingBar giveRatingbar;
	private Long mRowId;
	private float mRating;
	
	RestaurantDbAdapter mDbHelper;
	RestaurantWebService mService;
	Cursor restaurant;
	Cursor types;
	
	//View items
	private Button btnRate, btnReviews, btnCall, btnViewOnMap;
	private TextView mNameText;
	private TextView mInfo;
	private TextView mTypes;
	
	//Temporary stings for toasts
	static final CharSequence bTextDescr = "No description ..";
	static final CharSequence bTextReviews = "No reviews... :(";
	static final CharSequence bTextCall = "I cant call ..";
	static final CharSequence bTextViewOnMap = "Cant view on map ..";
	
	private long user_id;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.details);
		setupButtons();
		
		showRatingbar = (RatingBar) findViewById(R.id.indicator_ratingbar);
		
		mNameText = (TextView) findViewById(R.id.ReName);
		mInfo = (TextView) findViewById(R.id.ReInfo);
		mTypes = (TextView) findViewById(R.id.ReTypes);
		
		//Open connection to DB adapter
		mDbHelper = new RestaurantDbAdapter(this);
        mDbHelper.open();
        
        mService = new RestaurantWebService(mDbHelper);
        
        //Check if resuming from a saved instance state
        mRowId = (savedInstanceState != null ? savedInstanceState.getLong(RestaurantDbAdapter.KEY_ROWID) : null);
        //Get id from intent if not set
        if (mRowId == null)
        {
        	Bundle extras = getIntent().getExtras();
        	mRowId = extras != null ? extras.getLong(RestaurantDbAdapter.KEY_ROWID) : null;
        }
    	
        
        
        populateView();
        
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDbHelper.close();
	}
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case RATING_DIALOG:
		            LayoutInflater factory = LayoutInflater.from(this);
		            final View layout = factory.inflate(R.layout.ratingdialog, null);
	            	
		            RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
	            	rb.setRating(mRating);
	            	final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		            return new AlertDialog.Builder(FoodoDetails.this)
		                .setTitle("Rating")
		                .setView(layout)
		                .setPositiveButton("Rate!", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		                    	/* User clicked OK, add new rating */
		                    	RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
		                    	Log.d(TAG, "Giving rating: " + rb.getRating());
		                    	// get UserId from SharedPreferences
		                    	user_id = settings.getLong("user", 0);
		                    	showRatingbar.setRating(
		                    			(float) mService.addRating(mRowId, rb.getRating(), user_id)
		                    	);
		                    }
		                })
		                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {

		                        /* User clicked cancel so do some stuff */
		                    }
		                })
		                .create();
		        }
		return null;
	}
	
	/**
	 * Fills data into the view
	 */
	protected void populateView() {
    	if (mRowId != null)
    	{
    		restaurant = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(restaurant);
    		
    		String typeString = makeTypeString();
    			Log.d(TAG, typeString);
    		mTypes.setText(typeString);
    		
    		mRating = restaurant.getFloat(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING));
    		
    		mNameText.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    		showRatingbar.setRating(mRating);
    		
    		mInfo.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_ADDRESS))
		    				+ '\n'
		    				+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_ZIP))
		    				+ ' '
		    				+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_CITY))
		    				+ '\n'
		    				+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_EMAIL))
		    				+ '\n' 
		    				+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_WEBSITE))
    		);
    	}
	}
	
	private String makeTypeString() {
		StringBuilder builder = new StringBuilder();
		
		types = mDbHelper.fetchRestaurantTypes(mRowId);
		
		if (types.moveToFirst()) {
			do {
				builder.append(types.getString(types.getColumnIndex(RestaurantDbAdapter.KEY_TYPE)));
			} 
			while (types.moveToNext());
		}
		else {
			builder.append("...");
		}

		return builder.toString();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(RestaurantDbAdapter.KEY_ROWID, mRowId);
	}
	
	/* create the menu items */
	/*
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,0,0,"Call");
		 menu.add(0,1,1,"Rate!");
		 menu.add(0,2,2,"Reviews");
		 
		 return true;
	}
	*/
	public void setupButtons() {
		this.btnRate = (Button)this.findViewById(R.id.bRate);
		this.btnReviews = (Button)this.findViewById(R.id.bReviews);
		this.btnCall = (Button)this.findViewById(R.id.bCall);

	//	this.btnViewOnMap = (Button)this.findViewById(R.id.bViewOnMap);
		
		btnRate.setOnClickListener(new clicker());
  		btnReviews.setOnClickListener(new clicker());
		btnCall.setOnClickListener(new clicker()); 

	//	btnViewOnMap.setOnClickListener(new clicker());
			
	}

	/* when menu button option selected */
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		switch (item.getItemId()) {
		case 0:
			try {
				   Intent callIntent = new Intent(Intent.ACTION_CALL) ; 
				   callIntent.setData(Uri.parse("tel:+" + restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_PHONE))));
				   startActivity(callIntent);
				} catch (Exception e) {
				   Log.e(TAG, "Calling caused an exception: ", e);
				}
			return true;
		case 1:
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			if(settings.getBoolean("access", true)){
				showDialog(RATING_DIALOG);
				return true;
			}
			else {
				Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
			}
			return true;
		
		case 2:
			reviews();
			return true;
		}
		return false;
	}
	
	public void reviews() {
		Intent i = new Intent(this, ReadReviews.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i, 1);
	}
	// button click listener
	class clicker implements Button.OnClickListener
    {     

		public void onClick(View v)
		{
			Context context = getApplicationContext();
			if(v==btnRate){
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FoodoDetails.this);
				if(settings.getBoolean("access", true)){
					showDialog(RATING_DIALOG);
				}
				else {
					Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
				}
			}
			else if(v==btnReviews){
					reviews();
			}
			else if(v==btnCall){
				try {
					   Intent callIntent = new Intent(Intent.ACTION_CALL) ; 
					   callIntent.setData(Uri.parse("tel:" + restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_PHONE))));
					   startActivity(callIntent);
					} catch (Exception e) {
					   Log.e(TAG, "Calling caused an exception: ", e);
					}
			
			}
			else if(v==btnViewOnMap){
				Toast.makeText(context, bTextViewOnMap, Toast.LENGTH_SHORT).show();
			}
		
		}
    }	
    
}
