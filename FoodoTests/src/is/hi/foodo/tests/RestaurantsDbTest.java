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
	}
	
    protected void tearDown() throws Exception {
        super.tearDown();

        // Do some clean up here
        Log.d(TAG, "In tearDown");
        mDb.close();
    }

	public void testCreateRestaurant() {
		long id = mDb.createRestaurant("Test", 10, 20);
		assertTrue("Failed to create node", id > 0);
	}
	
	public void testReadSingleNode() {
		Cursor r = mDb.fetchRestaurant(1);
		assertEquals(r.getString(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)), "Test");
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)), 10);
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)), 20);
		r.deactivate();
	}
	
	public void testFetchAll() {
		Cursor rs = mDb.fetchAllRestaurants();
		assertTrue("no restaurants found", rs.getCount() > 0);
	}

}
