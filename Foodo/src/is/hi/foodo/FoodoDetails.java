package is.hi.foodo;

import is.hi.foodo.net.FoodoService;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class FoodoDetails extends Activity {
	
	private static final String TAG = "FoodoDetails";
	private static final int MENU_VIEW = 2;
	
	static final int RATING_DIALOG = 0;
	private RatingBar showRatingbar;
	public RatingBar giveRatingbar;
	private Long mRowId;
	private float mRating;
	
	RestaurantDbAdapter mDbHelper;
	RestaurantLoader mService;
	Cursor restaurant;
	Cursor types;
	
	//View items
	private Button btnRate, btnReviews, btnCall, btnViewOnMap, btnLog;
	private Button btnMenu;
	private TextView mNameText;
	private TextView mInfo;
	private TextView mTypes;
	
	//Temporary stings for toasts
	static final CharSequence bTextDescr = "No description ..";
	static final CharSequence bTextReviews = "No reviews... :(";
	static final CharSequence bTextCall = "I cant call ..";
	static final CharSequence bTextViewOnMap = "Cant view on map ..";
	
	//private long user_id;
	
		
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
        
        mService = new RestaurantLoader(mDbHelper, ((FoodoApp)getApplicationContext()).getService());
        
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
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MENU_VIEW) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Your order is being processed!", Toast.LENGTH_LONG).show();
            }
        }
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
		                    	//long user_id = settings.getLong("user", 0);
		                    	
		                    	
		                    	//Get user api key from SharedPrefrences
		                    	String user_api_key = settings.getString("api_key", "");
		                    	
		                    	FoodoService service = ((FoodoApp)FoodoDetails.this.getApplicationContext()).getService();
		                    	
		                    	try {
			                    	float newrating = (float) service.submitRating(
			                    			mRowId, 
			                    			user_api_key, 
			                    			(int)rb.getRating()
			                    	).getDouble("rating");  	
			                    	showRatingbar.setRating(newrating);
		                    	}
		                    	catch (Exception e) {
		                    		//TODO should notify of this?
		                    		Log.d(TAG, "Not able to submit rating", e);
		                    	}
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
    	
    	//Initialize login/logout button
    	SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FoodoDetails.this);
    	CharSequence str = "";
		if(settings.getBoolean("access", true))
			str = getString(R.string.logout);
		else 
			str = getString(R.string.login);
		
		Button b = (Button)findViewById(R.id.bLog);
		b.setText(str);
    	
	}
	
	private String makeTypeString() {
		StringBuilder builder = new StringBuilder();
		
		types = mDbHelper.fetchRestaurantTypes(mRowId);
		
		if (types.moveToFirst()) {
			do {
				builder.append(types.getString(types.getColumnIndex(RestaurantDbAdapter.KEY_TYPE)));
				
				if (!types.isLast())
					builder.append(", ");
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

	public void setupButtons() {
		this.btnMenu = (Button)this.findViewById(R.id.bMenu);
		this.btnRate = (Button)this.findViewById(R.id.bRate);
		this.btnReviews = (Button)this.findViewById(R.id.bReviews);
		this.btnCall = (Button)this.findViewById(R.id.bCall);
		this.btnLog = (Button)this.findViewById(R.id.bLog);

	//	this.btnViewOnMap = (Button)this.findViewById(R.id.bViewOnMap);
		
		btnMenu.setOnClickListener(new clicker());
		btnRate.setOnClickListener(new clicker());
  		btnReviews.setOnClickListener(new clicker());
		btnCall.setOnClickListener(new clicker()); 
		btnLog.setOnClickListener(new clicker()); 

	//	btnViewOnMap.setOnClickListener(new clicker());
			
	}
	/*case MENU_LOGIN:
			Intent login = new Intent(this, FoodoLogin.class);
			startActivityForResult(login, 1);
			return true;
		case MENU_LOGOUT:
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
			SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("access", false);
	        editor.commit();
	        Context context = getApplicationContext();
			Toast.makeText(context, "You have been logged out", Toast.LENGTH_SHORT).show();

			return true;*/
	
	public void reviews() {
		Intent i = new Intent(this, ReadReviews.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i, 1);
	}
	
	public void menu(){
		Intent i = new Intent(this,FoodoMenu.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i,MENU_VIEW);
	}
	
	public void login(){
		Intent login = new Intent(this, FoodoLogin.class);
		startActivityForResult(login, 1);
		CharSequence str = getString(R.string.logout);
		Button b = (Button)findViewById(R.id.bLog);
		b.setText(str);
	}
	
	public void logout(){
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("access", false);
        editor.commit();
        Context context = getApplicationContext();
		Toast.makeText(context, "You have been logged out", Toast.LENGTH_SHORT).show();
		CharSequence str = getString(R.string.login);
		Button b = (Button)findViewById(R.id.bLog);
		b.setText(str);
	}
	
	// button click listener
	class clicker implements Button.OnClickListener
    {     
		
		public void onClick(View v)
		{
		// TODO: Put the following code in a Case!
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
			else if(v == btnViewOnMap){
				Toast.makeText(context, bTextViewOnMap, Toast.LENGTH_SHORT).show();
			}
			else if (v == btnLog){
				SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(FoodoDetails.this);
				if(settings.getBoolean("access", true)){
					logout();
				}
				else{					
					login();
				}
			}
			else if(v == btnMenu){
				menu();				
			}
		
		}
    }	
    
}
