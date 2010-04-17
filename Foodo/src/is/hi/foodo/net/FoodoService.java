package is.hi.foodo.net;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service interface for communication with a Foodo web service.
 * @author siggijons
 *
 */
public interface FoodoService {

	/**
	 * Get all available restaurants
	 * @throws FoodoServiceException 
	 */
	public JSONArray getRestaurants() throws FoodoServiceException;

	/**
	 * 
	 * @param lat latitude
	 * @param lon longitude
	 * @param distance_km Distance in kilometers
	 * @return Array of restaurants within distance_km of lat, lon
	 * @throws FoodoServiceException
	 */
	public JSONArray getNearByRestaurants(double lat, double lon, int distance_km) throws FoodoServiceException;

	/**
	 * Get restaurant details
	 * @throws FoodoServiceException 
	 */
	public JSONObject getRestaurantDetails(long restaurant_id) throws FoodoServiceException;

	/**
	 * Get restaurant reviews
	 * @throws FoodoServiceException 
	 */
	public JSONArray getRestaurantReviews(long restaurant_id) throws FoodoServiceException;

	/**
	 * Get restaurant menu
	 * @throws FoodoServiceException 
	 */
	public JSONArray getRestaurantMenu(long restaurant_id) throws FoodoServiceException;

	/**
	 * Submit rating for a restaurant
	 * @throws FoodoServiceException 
	 */
	public JSONObject submitRating(long restaurant_id, String apikey, int rating) throws FoodoServiceException;

	/**
	 * Submit review for restaurant
	 */
	public JSONArray submitReview(long restaurant_id, String apikey, String review) throws FoodoServiceException;

	/**
	 * Register new user
	 * @throws FoodoServiceException 
	 */
	public JSONObject registerUser(String email, String password, String firstName, String lastName) throws FoodoServiceException;

	/**
	 * Edits the name, email of an old user
	 * @throws FoodoServiceException 
	 */
	public JSONObject editUser(String apikey, String password, String newEmail, String newFirstName, String newLastName) throws FoodoServiceException;

	/**
	 * Edits the password of an old user
	 * @throws FoodoServiceException
	 */
	public JSONObject editPassword(String apikey, String currentPassword, String newPassword) throws FoodoServiceException;

	/**
	 * Login user
	 * @throws FoodoServiceException 
	 */
	public JSONObject loginUser(String email, String password) throws FoodoServiceException;

	/**
	 * Returns all the users orders.
	 * @param apikey
	 * @return JSONArray
	 * @throws FoodoServiceException
	 */
	public JSONArray getUserOrders(String apikey) throws FoodoServiceException;

	/**
	 * Returns all the user information.
	 * @param apikey
	 * @return JSONObject
	 * @throws FoodoServiceException
	 */
	public JSONObject getUserInfo(String apikey) throws FoodoServiceException;

	/**
	 * Get all available restaurant types
	 * 
	 * @return List of types
	 * @throws FoodoServiceException
	 */
	public JSONArray getTypes() throws FoodoServiceException;

	/**
	 * Submits an order
	 * 
	 * @param restaurant_id
	 * @param api_key
	 * @param items
	 * @return
	 * @throws FoodoServiceException
	 */
	public JSONObject submitOrder(long restaurant_id, String api_key, List<Map<String,String>> items) throws FoodoServiceException;

	/**
	 * Get user reviews
	 * @throws FoodoServiceException 
	 */
	public JSONArray getUserReviews(String apikey) throws FoodoServiceException;

	/**
	 * Edit user reviews
	 * @throws FoodoServiceException 
	 */
	public JSONArray editUserReview(long restaurantId, long reviewId, String apikey, String review) throws FoodoServiceException;

}
