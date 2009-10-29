package is.hi.foodo.tests;

import is.hi.foodo.RestaurantDbAdapter;
import is.hi.foodo.RestaurantWebService;
import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;


public class RestaurantWebServiceTest extends AndroidTestCase {
	
	private static final String TAG = "RestaurantWebServiceTest";
	
	private RestaurantDbAdapter mDb;
	private RestaurantWebService mService;

	public void setUp() throws Exception {
		super.setUp();
		
		Log.d(TAG, "In setUp");
		mDb = new RestaurantDbAdapter(this.getContext());
		mDb.open();
		mDb.emptyDatabase();
		mService = new RestaurantWebService(mDb);
	}
	
    protected void tearDown() throws Exception {
        super.tearDown();
        
        Log.d(TAG, "In tearDown");
        mDb.close();
    }
    
    public void testUpdateAll() {
    	
    	assertTrue("not able to load from webservice", mService.updateAll());
		
    	/*
		Cursor r = mDb.fetchRestaurant(1);
		assertEquals(r.getString(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)), "Burger Joint");
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)), 64139078);
		assertEquals(r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)), -21955812);
		assertTrue(new Float(5.0).equals(r.getFloat(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING))));
		r.deactivate();
    	 */
    }
    
    public void testRating() {
    	
    	mService.updateAll();
    	
    	long id = 1;
    	Cursor r = mDb.fetchRestaurant(1);
    	
    	float currentRating = r.getFloat(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING));
    	int currentCount = r.getInt(r.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING_COUNT));
    	    	
    	double newRating = 2.0;
    	double resRating = mService.addRating(id, newRating);

    	assertEquals(((currentRating*currentCount)+newRating)/(currentCount+1), resRating);
    	
    }


}
