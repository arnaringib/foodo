package is.hi.foodo.user;

public interface UserManager {

	public static final int E_LOGIN = 1;
	public static final int E_USER_EXISTS = 2;

	public boolean isAuthenticated();

	public boolean authenticate(String email, String password);
	public boolean deauthenticate();

	public boolean signup(String firstName, String lastName, String email, String password);
	public boolean userEditInfo(String password, String newFirstName, String newLastName, String newEmail);
	public boolean userEditPassword(String currentPassword, String newPassword);

	public String getFirstName();
	public String getLastName();
	public String getEmail();
	public String getApiKey();
	public int getNrReviews();
	public int getNrOrders();

	public int getErrorCode();
	public String getError();
}
