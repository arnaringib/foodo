package is.hi.foodo.tests;

import is.hi.foodo.RestaurantDbAdapter;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

public class RestaurantsDbTest extends AndroidTestCase {
	
	private static final String TAG = "RestaurantsDbAdapterTests";
	private RestaurantDbAdapter mDb;

	public void setUp() throws Exception {
		super.setUp();
		
		Log.d(TAG, "In setUp");
		mDb = new RestaurantDbAdapter(this.getContext());
		mDb.open();
		mDb.emptyDatabase();
	}
	
    protected void tearDown() throws Exception {
        super.tearDown();
        
        Log.d(TAG, "In tearDown");
        mDb.close();
    }

	public void testCreateRestaurant() 
	{		
		long id = 1;
		String name = "Test";
		int lat = 10;
		int lng = 20;
		double rating = 5.0;
		long rating_count = 1;
		
		//Create data
		mDb.createRestaurant(id, name, lat, lng, rating, rating_count);
		
		//Read data
		Cursor r = mDb.fetchRestaurant(id);
		assertEquals(r.getString(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)), name);
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)), lat);
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)), lng);
		assertTrue(new Double(rating).equals(r.getDouble(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING))));
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING_COUNT)), rating_count);
	}
	
	/*
	public void testWebService() {
		assertTrue("not able to load from webservice", mDb.loadFromWebService());
		
		Cursor r = mDb.fetchRestaurant(1);
		assertEquals(r.getString(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)), "Burger Joint");
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)), 64139603);
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)), -21955812);
		assertTrue(new Float(5.0).equals(r.getFloat(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING))));
		r.deactivate();
	}
	*/

}
