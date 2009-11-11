package is.hi.foodo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONObject;

import android.util.Log;

public class FoodoUserManager implements UserManager {
	
	private static final String TAG = "FoodoUserManager";
    private static final String WEBSERVICE_URL = "http://foodo.nord.is/api/user";
    
    private boolean isAuthenticated = false;
	private String email;
	private String apikey;
	
	private int errorCode;
	

	@Override
	public boolean authenticate(String email, String password) {
		this.isAuthenticated = false;
		try {
			JSONObject json = loadData(WEBSERVICE_URL + "/login/"+email+"/"+password);
			
			if (json.getInt("responseCode") == 200) {
				JSONObject user = json.getJSONObject("responseData").getJSONObject("User");
				
				this.email = user.getString("email");
				this.apikey = user.getString("apikey");
				this.isAuthenticated = true;
			}
			else {
				Log.d(TAG, "ResponseCode: " + json.getInt("responseCode"));
				errorCode = E_LOGIN;
				this.isAuthenticated = false;
			}
		}
		catch(Exception e) {
			Log.d(TAG, "Exception: " + e.toString());
			errorCode = -1;
			this.isAuthenticated = false;
		}
		
		return this.isAuthenticated;
	}
	
	@Override
	public boolean signup(String email, String password) {
		this.isAuthenticated = false;
		try {
			JSONObject json = loadData(WEBSERVICE_URL + "/signup/"+email+"/"+password);
			
			if (json.getInt("responseCode") == 200) {
				JSONObject user = json.getJSONObject("responseData").getJSONObject("User");
				
				this.email = user.getString("email");
				this.apikey = user.getString("apikey");
				this.isAuthenticated = true;
			}
			else {
				Log.d(TAG, "ResponseCode: " + json.getInt("responseCode"));
				errorCode = E_LOGIN;
				this.isAuthenticated = false;
			}
		}
		catch(Exception e) {
			Log.d(TAG, "Exception: " + e.toString());
			errorCode = -1;
			this.isAuthenticated = false;
		}
		
		return this.isAuthenticated;
	}

	@Override
	public String getApiKey() {
		return this.apikey;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public String getError() {
		String error;
		switch(this.errorCode)
		{
		case E_LOGIN:
			error = "Incorrect username or password";
			break;
		case E_USER_EXISTS:
			error = "User exists";
			break;
		default:
			error = "Unknown error";
		}
		return error;
	}
	

	@Override
	public int getErrorCode() {
		return this.errorCode;
	}

	@Override
	public boolean isAuthenticated() {
		return this.isAuthenticated;
	}
	
	private JSONObject loadData(String path) {
    	try {
	    	URL url = new URL(path);
			URLConnection connection = url.openConnection();
			
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			StringBuilder builder = new StringBuilder();
			String line;
			while (( line = reader.readLine()) != null)
			{
				builder.append(line);	
			}
			return new JSONObject(builder.toString());
    	}
    	catch (MalformedURLException e) {
    		Log.d(TAG, "MalformedURLException");
    		return null;
    	}
    	catch (Exception e) {
    		Log.d(TAG, "Exception");
    		return null;
    	}
    }

}
