package is.hi.foodo;

import java.util.HashMap;
import java.util.Map;

public class SimpleUserManager implements UserManager {
	
	private boolean isAuthenticated = false;
	private String email;
	private String apikey;
	
	private int errorCode;
	
	private Map<String, String> users = new HashMap<String, String>();
	
	public SimpleUserManager() {
		users.put("test@test.is", "test");
	}

	@Override
	public boolean authenticate(String email, String password) {
		
		if (users.get(email) != null && users.get(email).equals(password)) {
			this.email = email;
			this.apikey = "apikey";
			this.isAuthenticated = true;
		} 
		else {
			this.errorCode = E_LOGIN;
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
	public boolean isAuthenticated() {
		return this.isAuthenticated;
	}

	@Override
	public boolean signup(String email, String password) {
		if (users.get(email) != null)
		{
			this.errorCode = E_USER_EXISTS;
			return false;
		}
		else {
			users.put(email, password);
			return true;
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

}
