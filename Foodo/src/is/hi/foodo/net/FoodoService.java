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
	 * Login user
	 * @throws FoodoServiceException 
	 */
	public JSONObject loginUser(String email, String password) throws FoodoServiceException;
	
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

}
