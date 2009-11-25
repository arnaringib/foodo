package is.hi.foodo;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class ReadReviews extends ListActivity  {
	
	private static final String TAG = "FoodoReviews";

	private RestaurantDbAdapter mDbHelper;
	private Cursor mRestaurantCursor;
	
	private Long mRowId;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.reviews); 
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
        
		gatherList();
		getListView().setTextFilterEnabled(true);
		registerForContextMenu(getListView());
		
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
	
	private void populateView() {
		if (mRowId != null)
    	{
    		Cursor restaurant = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(restaurant);
    		
    		TextView mPlaceName = (TextView) this.findViewById(R.id.reviewPlace);
    		mPlaceName.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    	}
	}
	
	
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
	}
	
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
			if(settings.getBoolean("access", true)){
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
}

