package is.hi.foodo.tests;

import is.hi.foodo.net.FoodoServiceException;
import is.hi.foodo.net.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.test.AndroidTestCase;
import android.util.Log;

public class WebServiceTest extends AndroidTestCase {
	
	private static final String TAG = "WebServiceTest";
	WebService service;
	int testSubjectId;
	
	public void setUp() {
		service = new WebService("http://foodo.morpho.nord.is/api");
		testSubjectId = 19;
	}
	
	public void testGetRestaurants() throws FoodoServiceException {
		JSONArray restaurants = service.getRestaurants();
		assertTrue(restaurants != null);
		Log.d(TAG, "List: " + restaurants.toString());
		assertTrue(restaurants.length() >= 0);
	}
	
	public void testGetDetails() throws JSONException, FoodoServiceException {
		JSONObject restaurant = service.getRestaurantDetails(testSubjectId);
		assertTrue(restaurant != null);
		Log.d(TAG, "Details: " + restaurant.toString());
		assertTrue(restaurant.getString("name").length() > 0);
	}
	
	public void testGetMenu() throws FoodoServiceException {
		JSONArray menu = service.getRestaurantMenu(testSubjectId);
		assertTrue(menu != null);
		Log.d(TAG, "Menu: " + menu.toString());
		assertTrue(menu.length() >= 0);
	}
	
	public void testGetReviews() throws FoodoServiceException {
		JSONArray reviews = service.getRestaurantReviews(testSubjectId);
		assertTrue(reviews != null);
		Log.d(TAG, "Reviews: " + reviews.toString());
		assertTrue(reviews.length() >= 0);
	}
	
	public void testCorrectLogin() throws JSONException, FoodoServiceException {
		JSONObject user = service.loginUser("siggijons27@gmail.com", "1234");
		Log.d(TAG, "User: " + user.toString());
		assertTrue(user != null);
		assertTrue(user.getString("apikey").equals("TEST"));
	}
	
	public void testInvalidLogin() throws JSONException {
		int caught = 0;
		try {
			JSONObject user = service.loginUser("bad", "bad");
			assertFalse(user.get("apikey").equals("bad"));
		}
		catch (FoodoServiceException e)
		{
			caught++;
			Log.d(TAG, "Wanted exception: " + e.toString());
		}
		assertTrue(caught == 1);
	}
	
	
	/* This test is not repeatable */
	public void testRegister() throws FoodoServiceException {
		String email = "test@test.is";
		String password = "test";
		String firstName = "Testari";
		String lastName = "Testsson";
		
		int caught = 0;
		JSONObject user;
		
		try {
			user = service.registerUser(email, password, firstName, lastName);
		}
		catch (FoodoServiceException e)
		{
			caught++;
			Log.d(TAG, "Unwanted exception: ", e);
			throw e;
		}
		assertTrue(user != null);
		assertTrue(caught == 0);
	}
	
	/* The following tests submit data to the main server which we might not want to do */
	public void testRating() throws FoodoServiceException {
		String apikey = "TEST";
		int rating = 4;
		
		JSONObject restaurant = null;

		int caught = 0;
		
		try {
			restaurant = service.submitRating(testSubjectId, apikey, rating);
		}
		catch (FoodoServiceException e)
		{
			caught = 0;
			Log.d(TAG, "Exception in rating", e);
			throw e;
		}
		assertTrue(restaurant != null);
		Log.d(TAG, "Restaurant from rating: " + restaurant.toString());
		assertTrue(caught == 0);
	}
	
	public void testSubmitReview() throws FoodoServiceException {
		String review = "This is a test review";
		String apikey = "TEST";
		
		//Reviews pre
		JSONArray reviews_pre = service.getRestaurantReviews(testSubjectId);
		assertTrue(reviews_pre.length() >= 0);
		
		//Post new review
		JSONArray reviews = service.submitReview(testSubjectId, apikey, review);
		assertTrue(reviews.length() == reviews_pre.length() + 1);
	}
	
	public void testTypes() throws FoodoServiceException {
		
		JSONArray types = service.getTypes();
		Log.d(TAG, types.toString());
		assertTrue(types.length() >= 0);
	}

}
