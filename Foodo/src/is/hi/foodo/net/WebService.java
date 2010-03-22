package is.hi.foodo.net;

import is.hi.foodo.FoodoMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class WebService implements FoodoService {

	private static final String TAG = "WebService";
	private final String url;

	public WebService(String url) {
		this.url = url;
	}

	private String streamToString(InputStream is) {
		BufferedReader reader = new BufferedReader( new InputStreamReader(is));
		StringBuilder builder = new StringBuilder();
		String line;

		try {
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);	
			}
			return builder.toString();
		}
		catch (IOException e) {
			Log.d(TAG, "Error while converting input stream to string");
			e.printStackTrace();
		}
		finally {
			try {
				is.close();
			}
			catch (IOException e) {}
		}
		//Log.d(TAG, builder.toString());
		return builder.toString();
	}

	private JSONObject post(String path, List<NameValuePair> data) throws FoodoServiceException {
		HttpClient httpclient = new DefaultHttpClient();  
		HttpPost httppost = new HttpPost(this.url + path);

		//Log.d(TAG, "Post request at: " + this.url + path);

		try {
			//Add data
			httppost.setEntity(new UrlEncodedFormEntity(data));

			//Execute HTTP Post Request  
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();

			JSONObject result = new JSONObject(streamToString(entity.getContent()));			

			if (result.getInt("responseCode") == 200) {
				return result.getJSONObject("responseData");
			}
			else {
				throw new FoodoServiceException(result.getString("errorMessage"));
			}
		}
		catch (JSONException e) {
			Log.d(TAG, "There was a JSON exception", e);
		}
		catch (IOException e) {  
			Log.e(TAG, "There was an IO Stream related error", e);  
		}  

		return null;
	}

	private JSONObject get(String path) throws FoodoServiceException {
		HttpClient httpclient = new DefaultHttpClient();  
		HttpGet httpget = new HttpGet(this.url + path);

		//Log.d(TAG, "Get request at: " + this.url + path);

		try {
			//Execute HTTP Get Request  
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			JSONObject result = new JSONObject(streamToString(entity.getContent()));			

			if (result.getInt("responseCode") == 200) {
				return result.getJSONObject("responseData");
			}
			else {
				throw new FoodoServiceException(result.getString("errorMessage"));
			}
		}
		catch (JSONException e) {
			Log.d(TAG, "There was a JSON exception", e);
		}
		catch (IOException e) {  
			Log.e(TAG, "There was an IO Stream related error", e);  
		}  

		return null;
	}


	@Override
	public JSONArray getRestaurants() throws FoodoServiceException {
		try {
			JSONObject o = this.get("/restaurants/");
			return o.getJSONArray("Restaurants");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception", e);
			throw new FoodoServiceException("Error while fetching restaurants");
		}
	}

	@Override
	public JSONObject getRestaurantDetails(long restaurantId) throws FoodoServiceException {
		try {
			return this.get("/restaurants/" + restaurantId + "/").getJSONArray("Restaurants").getJSONObject(0);
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in details", e);
			throw new FoodoServiceException("Error while fetching details");
		}
	}

	@Override
	public JSONArray getRestaurantMenu(long restaurantId) throws FoodoServiceException {
		try {
			return this.get("/restaurants/" + restaurantId + "/menu/").getJSONArray("Menu");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in menu", e);
			throw new FoodoServiceException("Error while fetching menu");
		}
	}

	@Override
	public JSONArray getRestaurantReviews(long restaurantId) throws FoodoServiceException {
		try {
			return this.get("/restaurants/" + restaurantId + "/reviews/").getJSONArray("Reviews");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in reviews", e);
			throw new FoodoServiceException("Error while fetching reviews");
		}
	}

	@Override
	public JSONObject loginUser(String email, String password) throws FoodoServiceException {
		//Create list for request parameters
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("email", email));  
		nameValuePairs.add(new BasicNameValuePair("password", password));

		try {
			return this.post("/users/login/", nameValuePairs).getJSONObject("User");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in login", e);
			throw new FoodoServiceException("Error while logging in");
		}
	}

	@Override
	public JSONObject registerUser(String email, String password,
			String firstName, String lastName) throws FoodoServiceException {

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("email", email));  
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("firstname", firstName));
		nameValuePairs.add(new BasicNameValuePair("lastname", lastName));

		try {
			return this.post("/users/signup/", nameValuePairs).getJSONObject("User");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in register", e);
			throw new FoodoServiceException("Error while registering user");
		}
	}

	@Override
	public JSONObject submitRating(long restaurantId, String apikey, int rating) throws FoodoServiceException {
		try {
			return this.get("/restaurants/" + restaurantId + "/rate/" + rating + "/" + apikey).getJSONArray("Restaurants").getJSONObject(0);
		} 
		catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) { 
			Log.d(TAG, "Exception in submitRating", e);
			throw new FoodoServiceException("Unexcepted error while submitting rating");
		}
	}

	@Override
	public JSONArray submitReview(long restaurantId, String apikey, String review) throws FoodoServiceException {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("review", review));  

		try {
			return this.post("/restaurants/" + restaurantId + "/reviews/create/" + apikey + "/", nameValuePairs).getJSONArray("Reviews");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in submitReview", e);
			throw new FoodoServiceException("Unexpected error while submitting review");
		}
	}

	public JSONArray getTypes() throws FoodoServiceException {

		try {
			return this.get("/types/").getJSONArray("Types");
		}
		catch (FoodoServiceException e) {
			throw e;
		}
		catch (Exception e) {
			Log.d(TAG, "Exception in getTypes", e);
			throw new FoodoServiceException("Unexpected error while fetching restaurant types");
		}
	}

	@Override
	public JSONObject submitOrder(long restaurantId, String apiKey,
			List<Map<String, String>> items) throws FoodoServiceException {


		//Prepare JSON object
		JSONArray order = new JSONArray();
		try {
			for (int i = 0; i < items.size(); i++)
			{
				JSONObject item = new JSONObject();
				item.put("id", items.get(i).get(FoodoMenu.ITEMID));
				item.put("amount", items.get(i).get(FoodoMenu.AMOUNT));
				order.put(item);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d(TAG, "Order is: " + order.toString());

		//Prepare parameters for post request
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
		nameValuePairs.add(new BasicNameValuePair("order", order.toString()));
		nameValuePairs.add(new BasicNameValuePair("restaurant_id", String.valueOf(restaurantId)));
		nameValuePairs.add(new BasicNameValuePair("apikey", apiKey));

		try {
			return this.post("/order/", nameValuePairs).getJSONObject("Order");
		} catch (FoodoServiceException e) {
			throw e;
		} catch (Exception e) {
			Log.d(TAG, "Exception in order", e);
			throw new FoodoServiceException("Error while sending out order");
		}
	}

}
