package is.hi.foodo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class ReviewWebService {
	
	private static final String TAG = "ReviewWebService";
    private static final String WEBSERVICE_URL = "http://foodo.nord.is/api";
	
    
    public JSONObject addReview(int uid, int rid, String review) {
    	
		// Create a new HttpClient and Post Header
	    HttpClient httpclient = new DefaultHttpClient();  
	    HttpPost httppost = new HttpPost(WEBSERVICE_URL + "/reviews/create");  
	  
	    try {  
	        // Add your data  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);  
	        nameValuePairs.add(new BasicNameValuePair("user_id", Integer.toString(uid)));  
	        nameValuePairs.add(new BasicNameValuePair("restaurant_id", Integer.toString(rid)));
	        nameValuePairs.add(new BasicNameValuePair("review", review));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));  
	  
	        // Execute HTTP Post Request  
	        HttpResponse response = httpclient.execute(httppost);
	        HttpEntity entity = response.getEntity();
 
	        BufferedReader reader = new BufferedReader( new InputStreamReader(entity.getContent()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);	
			}
			
			JSONObject result = new JSONObject(builder.toString());
			
			//Log.d(TAG, result.toString());
			
			//Log.d(TAG, result.getJSONObject("responseData").toString());
			if (result.getInt("responseCode") == 200)
			{
				return result.getJSONObject("responseData").getJSONObject("review");
			}
			else {
				return null;
			}
	    } 
	    catch (ClientProtocolException e) {
	    	return null;  
	    } 
	    catch (IOException e) {  
	    	return null;  
	    }  
	    catch (Exception e)
	    {
	    	return null;
	    }
	}
    
    public JSONArray getReviews(long restaurant_id) {
    	try {
    		URL url = new URL(WEBSERVICE_URL + "/restaurant/id/" + restaurant_id + "/reviews");
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);	
			}
			
			JSONObject o = new JSONObject(builder.toString());
			
			
			return o.getJSONObject("responseData").getJSONArray("reviews");
    	}
    	catch (Exception e) {
    		Log.d(TAG, "Exception in getReviews()");
    		Log.d(TAG, e.getMessage());
    		return null;
    	}
    }

}
