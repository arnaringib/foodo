package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class FoodoDetails extends Activity {
	
	private static final String TAG = "FoodoDetails";
	
	static final int RATING_DIALOG = 0;
	private RatingBar showRatingbar;
	public RatingBar giveRatingbar;
	private Long mRowId;
	private float mRating;
	
	RestaurantDbAdapter mDbHelper;
	RestaurantWebService mService;
	
	//View items
	private Button btnDescr, btnReviews, btnCall, btnViewOnMap;
	private TextView mNameText;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.details);
		setupButtons();
		
		showRatingbar = (RatingBar) findViewById(R.id.indicator_ratingbar);
		
		mNameText = (TextView) findViewById(R.id.ReName);
		
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
	// Frekar mikid tekid ur tutorial sjaum til med thetta
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case RATING_DIALOG:
		            LayoutInflater factory = LayoutInflater.from(this);
		            final View layout = factory.inflate(R.layout.ratingdialog, null);
	            	
		            RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
	            	rb.setRating(mRating);
	            	
		            return new AlertDialog.Builder(FoodoDetails.this)
		                .setTitle("Rating")
		                .setView(layout)
		                .setPositiveButton("Rate!", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		                        /* User clicked OK, add new rating */
		                    	RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
		                    	Log.d(TAG, "Giving rating: " + rb.getRating());
		                    	showRatingbar.setRating(
		                    			(float) mService.addRating(mRowId, rb.getRating())
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
    		Cursor restaurant = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(restaurant);
    		
    		mRating = restaurant.getFloat(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING));
    		
    		mNameText.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    		showRatingbar.setRating(mRating);
    	}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(RestaurantDbAdapter.KEY_ROWID, mRowId);
	}
	
	/* create the menu items */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,0,0,"Info");
		 menu.add(0,1,1,"Rate!");
		 return true;
	}
	
	public void setupButtons() {
		this.btnDescr = (Button)this.findViewById(R.id.bDescription);
		this.btnReviews = (Button)this.findViewById(R.id.bReviews);
		this.btnCall = (Button)this.findViewById(R.id.bCall);
		this.btnViewOnMap = (Button)this.findViewById(R.id.bViewOnMap);
		
		btnDescr.setOnClickListener(new clicker());
		btnReviews.setOnClickListener(new clicker());
		btnCall.setOnClickListener(new clicker());
		btnViewOnMap.setOnClickListener(new clicker());	
	}
	
	
	CharSequence mText1 = "In progress";
	CharSequence bTextDescr = "Cant view on map ..";
	CharSequence bTextReviews = "No description ..";
	CharSequence bTextCall = "I cant call ..";
	CharSequence bTextViewOnMap = "No reviews... :(";
	int duration = Toast.LENGTH_SHORT;

	/* when menu button option selected */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		switch (item.getItemId()) {
		case 0:
			Toast toast1 = Toast.makeText(context, mText1, duration);
			toast1.show();
			return true;
		case 1:
			showDialog(RATING_DIALOG);
			return true;
		}
		return false;
	}
	
	// button click listener
	class clicker implements Button.OnClickListener
    {     

		public void onClick(View v)
		{
			Context context = getApplicationContext();
			if(v==btnDescr){
				Toast toast2 = Toast.makeText(context, bTextDescr, duration);
				toast2.show();
			}
			else if(v==btnReviews){
				Toast toast3 = Toast.makeText(context, bTextReviews, duration);
				toast3.show();
			}
			else if(v==btnCall){
				Toast toast4 = Toast.makeText(context, bTextCall, duration);
				toast4.show();
			}
			else if(v==btnViewOnMap){
				Toast toast5 = Toast.makeText(context, bTextViewOnMap, duration);
				toast5.show();
			}
		}
    }


	
}
