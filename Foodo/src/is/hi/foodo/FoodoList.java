package is.hi.foodo;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class FoodoList extends ListActivity {
	
	private RestaurantDbAdapter mDbHelper;
	private Cursor mRestaurantCursor;
	
	ArrayList<String> mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		
		mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();

		fillData();
		getListView().setTextFilterEnabled(true);
		registerForContextMenu(getListView());
	}
	
	private void fillData() {
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
		String[] from = new String[]{RestaurantDbAdapter.KEY_NAME, RestaurantDbAdapter.KEY_RATING};
		
		//Fields we want to bind to
		int[] to = new int[]{R.id.restaurant, R.id.ReRating};
		
		SimpleCursorAdapter restaurants = 
				new SimpleCursorAdapter(this, R.layout.listrow, mRestaurantCursor, from, to);
		setListAdapter(restaurants);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		Intent i = new Intent(this, FoodoDetails.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, id);
    	startActivity(i);
	}
	
}
