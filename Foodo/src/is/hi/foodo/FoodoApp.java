package is.hi.foodo;

import is.hi.foodo.net.FoodoService;
import is.hi.foodo.net.WebService;
import is.hi.foodo.user.FoodoUserManager;
import is.hi.foodo.user.UserManager;
import android.app.Application;
import android.util.Log;

public class FoodoApp extends Application {
	private static final String TAG = "FoodoApp";

	private FoodoService service;
	private UserManager userManager;

	@Override
	public void onCreate() {
		this.service = new WebService(this.getResources().getString(R.string.api_path));
		this.userManager = new FoodoUserManager(this.service, FoodoApp.this);

		Log.d(TAG, this.getResources().getString(R.string.api_path));
		super.onCreate();
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public FoodoService getService() {
		return service;
	}

}
