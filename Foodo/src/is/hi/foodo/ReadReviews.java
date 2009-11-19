package is.hi.foodo;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;



public class ReadReviews extends ListActivity  {
	
	private static final String TAG = "FoodoReviews";

	private RestaurantDbAdapter mDbHelper;
	private Cursor mRestaurantCursor;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.reviews); 
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
        
		gatherList();
		getListView().setTextFilterEnabled(true);
		registerForContextMenu(getListView());

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
			Intent writer = new Intent(this, WriteReviews.class);
			startActivityForResult(writer, 1);
			return true;
		
		}
		return false;
	}
}

