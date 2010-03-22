package is.hi.foodo.user;

import is.hi.foodo.net.FoodoService;
import is.hi.foodo.net.FoodoServiceException;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class FoodoUserManager implements UserManager {

	private static final String TAG = "FoodoUserManager";
	private final SharedPreferences app_preferences;

	private boolean isAuthenticated = false;
	private final int id = 0;
	private String firstName, lastName, email;
	private String apikey, reviews, orders;

	private int errorCode;

	private final FoodoService mService;

	public FoodoUserManager(FoodoService service, Context c)
	{
		this.mService = service;
		app_preferences = PreferenceManager.getDefaultSharedPreferences(c);
	}

	@Override
	public boolean authenticate(String email, String password) {
		try {
			JSONObject user = mService.loginUser(email, password);
			this.firstName = user.getString("firstName");
			this.lastName = user.getString("lastName");
			this.email = user.getString("email");
			this.apikey = user.getString("apikey");
			this.orders = user.getString("orders");
			this.reviews = user.getString("reviews");
			this.isAuthenticated = true;
		}
		catch (FoodoServiceException e)
		{
			this.isAuthenticated = false;
			errorCode = E_LOGIN;
		}
		catch (Exception e) {
			Log.d(TAG, "Exception in authenticate", e);
			this.isAuthenticated = false;
			errorCode = -1;
		}

		return this.isAuthenticated;
	}

	@Override
	public boolean signup(String firstName, String lastName, String email, String password) {

		try {
			JSONObject user = mService.registerUser(email, password, firstName, lastName);

			this.firstName = user.getString("firstName");
			this.lastName = user.getString("lastName");
			this.email = user.getString("email");
			this.apikey = user.getString("apikey");

			SharedPreferences.Editor editor = app_preferences.edit();
			editor.putBoolean("access", true);
			editor.putLong("user",id);
			editor.putString("api_key", this.getApiKey());
			// Don't forget to commit edits!!!
			editor.commit();

			this.isAuthenticated = true;
		}
		catch (FoodoServiceException e)
		{
			errorCode = E_USER_EXISTS;
			this.isAuthenticated = false;
		}
		catch (Exception e) {
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
	public String getFirstName() {
		return this.firstName;
	}

	@Override
	public String getLastName() {
		return this.lastName;
	}

	@Override
	public String getEmail() {
		return this.email;
	}



	@Override
	public int getId() {
		return this.id;
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

	public boolean isNotAuthenticated() {
		isAuthenticated = false;
		return this.isAuthenticated;
	}

	@Override
	public String getNrOrders() {
		return this.orders;
	}

	@Override
	public String getNrReviews() {
		return this.reviews;
	}
}
