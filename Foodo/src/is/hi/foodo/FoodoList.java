package is.hi.foodo;

import java.util.ArrayList;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
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
		
		/*
		getList();
		
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				R.layout.listrow,
				R.id.restaurant,
				mList);
		setListAdapter(listAdapter);
		getListView().setTextFilterEnabled(true); //Let user search by typing the name.
		*/
	}
	
	private void fillData() {
		//Get all rows from database
		mRestaurantCursor = mDbHelper.fetchAllRestaurants();
		startManagingCursor(mRestaurantCursor);
		
		//Create an array of fields we want to display
		String[] from = new String[]{RestaurantDbAdapter.KEY_NAME, RestaurantDbAdapter.KEY_RATING};
		
		//Fields we want to bind to
		int[] to = new int[]{R.id.restaurant, R.id.ReRating};
		
		SimpleCursorAdapter restaurants = 
				new SimpleCursorAdapter(this, R.layout.listrow, mRestaurantCursor, from, to);
		setListAdapter(restaurants);
		
	}
	/*
	public void getList(){
		this.mList = new ArrayList<String>();
		try {
			JSONObject result = getResponse("http://foodo.siggijons.net/api/restaurants.json");
			
			JSONArray list = result.getJSONArray("Restaurants");
			for (int i = 0; i < list.length(); i++)
			{
				JSONObject o = list.getJSONObject(i);
				mList.add(o.getString("name"));
			}
		}
		catch(Exception e)
		{
			
		}
	}
	
	private JSONObject getResponse(String service_url) {
		try {
			URL url = new URL(service_url + "");
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			
			JSONObject json;
			
			json = new JSONObject(builder.toString());
			return json.getJSONObject("responseData");
		}
		catch (Exception e) {
			//TODO something
			return null;
		}
	}
	*/
	
}
