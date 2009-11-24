package is.hi.foodo.tests;

import is.hi.foodo.ReviewWebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;

public class ReviewWebServiceTest extends AndroidTestCase {
	
	private static final String TAG = "ReviewWebServiceTest";
	
	private ReviewWebService mService;
	
	
	public void setUp() {
		mService = new ReviewWebService();
	}
	
	public void testAddReview() throws JSONException {
		
		int uid = 1;
		int rid = 1;
		String review = "Tastes like feet!";
		
		JSONObject r = mService.addReview(uid, rid, review);
		Log.d(TAG, r.toString());
		assertEquals(r.getString("review"), review);
		assertEquals(r.getInt("restaurant_id"), rid);
		assertEquals(r.getInt("user_id"), uid);
		
	}
	
	public void testGetReviews() {
		int rid = 1;
		
		JSONArray reviews = mService.getReviews(rid);
		Log.d(TAG, reviews.toString());
		assertTrue(reviews != null);
		assertTrue(reviews.length() >= 0);
		
	}

}
