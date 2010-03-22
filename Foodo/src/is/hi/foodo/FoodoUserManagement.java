package is.hi.foodo;

import is.hi.foodo.user.UserManager;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class is the user management. In it the user
 * is able to view/edit his information and view old 
 * reviews/orders.
 * @author Arnar
 *
 */
public class FoodoUserManagement extends Activity {
	private static final String TAG = "FoodoUserManagment";

	private Button bEditInfo, bEditPassword,
	bViewOldOrders, bViewOldReviews, bLogout;
	private TextView tName, tEmail, tNumberOfReviews, tNumberofOrders;	
	private String sUserFirstName, sUserLastName, sUserEmail,
	sNumberOfOrders, sNumberOfReviews;;
	private UserManager uManager; // Handling the user information

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usermanagement);

		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();

		getUserInfo();

		setupTextView();
		setupButtons();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * Access the User manager and gets the information of the user. 
	 */
	private void getUserInfo(){
		sUserFirstName =  uManager.getFirstName();
		sUserLastName = uManager.getLastName();
		sUserEmail = uManager.getEmail();
		sNumberOfOrders = uManager.getNrOrders();
		sNumberOfReviews = uManager.getNrReviews();

		Log.d(TAG, "UserFirstName: " + sUserFirstName);
	}

	/**
	 * Adds the information on the user to the view.
	 * Currently the text views are:
	 * Name
	 * Email
	 * Number of orders
	 * Number of reviews
	 */
	private void setupTextView(){
		tName = (TextView) findViewById(R.id.tUser_name);
		tName.setText(sUserFirstName + " " + sUserLastName);

		tEmail = (TextView) findViewById(R.id.tUser_email);
		tEmail.setText(sUserEmail);

		tNumberofOrders = (TextView) findViewById(R.id.tNumberOfOrders);
		tNumberofOrders.setText(sNumberOfOrders);

		tNumberOfReviews = (TextView) findViewById(R.id.tNumberOfReviews);
		tNumberOfReviews.setText(sNumberOfReviews);
	}

	/**
	 * Adds functionality to each button in the view.
	 * Currently the buttons are:
	 * Edit Info
	 * Edit Password
	 * View Old Orders
	 * View Old Reviews
	 * Logout
	 */
	private void setupButtons(){
		bEditInfo = (Button) findViewById(R.id.bEditInfo);
		bEditInfo.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//TODO: Add an edit info intent or dialog
			}
		});

		bEditPassword = (Button) findViewById(R.id.bEditPassword);
		bEditPassword.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//TODO: Add an edit password intent or dialog
			}
		});

		bViewOldOrders = (Button) findViewById(R.id.bViewOldOrders);
		bViewOldOrders.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//TODO: Add an view old orders intent or dialog
			}			
		});

		bViewOldReviews = (Button) findViewById(R.id.bViewOldReviews);
		bViewOldReviews.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//TODO: Add a view old reviews intent or dialog			
			}
		});

		bLogout = (Button) findViewById(R.id.bLogout);
		bLogout.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				uManager.isNotAuthenticated();
				FoodoUserManagement.this.finish();
			}
		});
	}
}

