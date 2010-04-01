package is.hi.foodo.user;

import is.hi.foodo.net.FoodoService;
import is.hi.foodo.net.FoodoServiceException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class FoodoUserManager implements UserManager {

	private static final String TAG = "FoodoUserManager";
	private final SharedPreferences app_preferences;

	private JSONObject user;
	private boolean isAuthenticated = false;

	private int errorCode;

	private final FoodoService mService;

	public FoodoUserManager(FoodoService service, Context c)
	{
		this.mService = service;
		this.app_preferences = PreferenceManager.getDefaultSharedPreferences(c);
		this.load();
	}


	private void save() {
		Log.d(TAG, "Login information saved!");
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putBoolean("access", this.isAuthenticated);
		if (user != null)
		{
			editor.putString("user_json", user.toString());
		}
		editor.commit();
	}

	private void load() {
		Log.d(TAG, "Login information loaded!");
		this.isAuthenticated = this.app_preferences.getBoolean("access", false);
		try {
			if (this.app_preferences.contains("user_json")) {
				this.user = new JSONObject(this.app_preferences.getString("user_json", ""));
			}
		} catch (JSONException e) {
			Log.d(TAG, "Could not load user", e);
		}
	}

	@Override
	public boolean authenticate(String email, String password) {
		try {
			this.user = mService.loginUser(email, password);
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

		this.save();

		return this.isAuthenticated;
	}

	@Override
	//What do we want to return true if success or isAuthenticated, or should this be void ??
	public boolean deauthenticate() {

		this.isAuthenticated = false;
		this.user = null;
		save();

		return this.isAuthenticated;
	}

	@Override
	public boolean getUserInfo(String apikey) {
		try {
			user = mService.getUserInfo(apikey);
			this.isAuthenticated = true;
		}
		catch (Exception e) {
			errorCode = E_LOGIN;
			this.isAuthenticated = false;
		}

		save();
		return this.isAuthenticated;
	}

	@Override
	public boolean signup(String firstName, String lastName, String email, String password) {

		try {
			user = mService.registerUser(email, password, firstName, lastName);
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

		save();
		return this.isAuthenticated;
	}


	@Override
	public boolean userEditInfo(String password, String newFirstName, String newLastName, String newEmail) {
		try {
			user = mService.editUser(this.getApiKey(), password, newEmail, newFirstName, newLastName);
			this.isAuthenticated = true;
		}
		catch (FoodoServiceException e)
		{
			errorCode = E_LOGIN;
			this.isAuthenticated = false;
		}
		catch (Exception e) {
			errorCode = -1;
			this.isAuthenticated = false;
		}

		save();
		return this.isAuthenticated;
	}

	@Override
	public boolean userEditPassword(String currentPassword, String newPassword){
		try {
			user = mService.editPassword(this.getApiKey(), currentPassword, newPassword);
			this.isAuthenticated = true;
		}
		catch (FoodoServiceException e)
		{
			errorCode = E_LOGIN;
			this.isAuthenticated = false;
		}
		catch (Exception e) {
			errorCode = -1;
			this.isAuthenticated = false;
		}

		save();
		return this.isAuthenticated;
	}

	@Override
	public String getApiKey() {
		try {
			return this.user.getString("apikey");
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public String getFirstName() {
		try {
			return this.user.getString("firstName");
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public String getLastName() {
		try {
			return this.user.getString("lastName");
		} catch (JSONException e) {
			return null;
		}
	}

	@Override
	public String getEmail() {
		try {
			return this.user.getString("email");
		} catch (JSONException e) {
			return null;
		}
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
		Log.d(TAG, "Checking for authentication!");
		return this.isAuthenticated;
	}

	@Override
	public int getNrOrders() {
		try {
			return this.user.getInt("orders");
		} catch (JSONException e) {
			return 0;
		}
	}

	@Override
	public int getNrReviews() {
		try {
			return this.user.getInt("reviews");
		} catch (JSONException e) {
			return 0;
		}
	}
}
