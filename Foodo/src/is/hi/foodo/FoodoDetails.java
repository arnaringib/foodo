package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class FoodoDetails extends Activity {
	
	static final int RATING_DIALOG = 0;
	
	private Long mRowId;
	RestaurantDbAdapter mDbHelper;
	
	//View items
	private Button btn1, btn2, btn3, btn4;
	private TextView mNameText;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.details);
		setupButtons();
		
		mNameText = (TextView) findViewById(R.id.ReName);
		
		//Open connection to DB adapter
		mDbHelper = new RestaurantDbAdapter(this);
        mDbHelper.open();
        
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
	// Frekar mikið tekið úr tutorial, sjáum til með þetta.
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case RATING_DIALOG:
		            LayoutInflater factory = LayoutInflater.from(this);
		            final View textEntryView = factory.inflate(R.layout.ratingdialog, null);
		            return new AlertDialog.Builder(FoodoDetails.this)
		                .setTitle("Rating")
		                .setView(textEntryView)
		                .setPositiveButton("Rate!", new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {
		    
		                        /* User clicked OK so do some stuff */
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
    		mNameText.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
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
		 return true;
	}
	
	public void setupButtons() {
		this.btn1 = (Button)this.findViewById(R.id.Button01);
		this.btn2 = (Button)this.findViewById(R.id.Button02);
		this.btn3 = (Button)this.findViewById(R.id.Button03);
		this.btn4 = (Button)this.findViewById(R.id.Button04);
		
		btn1.setOnClickListener(new clicker());
		btn2.setOnClickListener(new clicker());
		btn3.setOnClickListener(new clicker());
		btn4.setOnClickListener(new clicker());	
	}
	
	
	CharSequence text1 = "In progress";
	CharSequence textb4 = "Cant view on map ..";
	CharSequence textb1 = "No description ..";
	CharSequence textb3 = "I cant call ..";
	CharSequence textb2 = "No reviews... :(";
	int duration = Toast.LENGTH_SHORT;

	/* when menu button option selected */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		switch (item.getItemId()) {
		case 0:
			Toast toast1 = Toast.makeText(context, text1, duration);
			toast1.show();
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
			if(v==btn1){
				Toast toast2 = Toast.makeText(context, textb1, duration);
				toast2.show();
			}
			else if(v==btn2){
				Toast toast3 = Toast.makeText(context, textb2, duration);
				toast3.show();
			}
			else if(v==btn3){
				showDialog(RATING_DIALOG);
			}
			else if(v==btn4){
				Toast toast5 = Toast.makeText(context, textb4, duration);
				toast5.show();
			}
		}
    }

	
}
