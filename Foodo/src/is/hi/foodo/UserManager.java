package is.hi.foodo;

public interface UserManager {
	
	public static final int E_LOGIN = 1;
	public static final int E_USER_EXISTS = 2;
	
	public boolean isAuthenticated();
	public boolean authenticate(String email, String password);
	public boolean signup(String firstName, String lastName, String email, String password);
	
	public String getFirstName();
	public String getLastName();
	public String getEmail();
	public String getApiKey();
	public int getId();
	
	public int getErrorCode();
	public String getError();
}
