package is.hi.foodo;

import is.hi.foodo.user.UserManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is the user management. In it the user
 * is able to view/edit his information and view old 
 * reviews/orders.
 * @author Arnar
 *
 */
public class FoodoUserManagement extends Activity {
	private static final String TAG = "FoodoUserManagment";

	private static int EDITINFO_DIALOG = 0;
	private static int EDITPASSWORD_DIALOG = 1;
	private static int OLDORDERS = 2;

	private Button bEditInfo, bEditPassword,
	bViewOldOrders, bViewOldReviews, bLogout,
	bUserEditInfo, bCancelEditInfo, bEditNewPassword,
	bCancelEditPassword;
	private EditText eUserFirstName, eUserLastName, eUserEmail, eUserPassword,
	eCurrentPassword, eNewPassword, eNewPasswordAgain;
	private TextView tName, tEmail, tNumberOfReviews, tNumberofOrders;	
	private String sUserFirstName, sUserLastName, sUserEmail,
	sNumberOfOrders, sNumberOfReviews;;
	private UserManager uManager; // Handling the user information
	private Dialog dEditInfo, dEditPassword;
	private ProgressDialog dProgressDialog; 

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
		sNumberOfOrders = String.valueOf(uManager.getNrOrders());
		sNumberOfReviews = String.valueOf(uManager.getNrReviews());

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
				dEditInfo = new Dialog(v.getContext());

				//Edit info dialog settings
				dEditInfo.setContentView(R.layout.usereditinfodialog);
				dEditInfo.setTitle(R.string.edit_info);
				dEditInfo.setCanceledOnTouchOutside(true);
				dEditInfo.show();

				//Edit info dialog view setup
				bUserEditInfo = (Button) dEditInfo.findViewById(R.id.bUserEditInfo);
				bCancelEditInfo = (Button) dEditInfo.findViewById(R.id.bCancelEditInfo);
				eUserFirstName = (EditText) dEditInfo.findViewById(R.id.eFirstName);
				eUserLastName = (EditText) dEditInfo.findViewById(R.id.eLastName);
				eUserEmail = (EditText) dEditInfo.findViewById(R.id.eEmail);
				eUserPassword = (EditText) dEditInfo.findViewById(R.id.ePassword);

				eUserFirstName.setText(sUserFirstName);
				eUserLastName.setText(sUserLastName);
				eUserEmail.setText(sUserEmail);

				bUserEditInfo.setOnClickListener( new View.OnClickListener(){
					public void onClick(View v){
						dProgressDialog = ProgressDialog.show(FoodoUserManagement.this, "Working", "Sending data...");
						Thread thread = new Thread( new Runnable(){
							public void run(){
								uManager.userEditInfo(eUserPassword.getText().toString(),
										eUserFirstName.getText().toString(), 
										eUserLastName.getText().toString(), 
										eUserEmail.getText().toString());
								hDialogs.sendEmptyMessage(EDITINFO_DIALOG);
							}
						});
						thread.start();	
					}
				});				

				bCancelEditInfo.setOnClickListener( new View.OnClickListener(){
					public void onClick(View v){
						dEditInfo.cancel();
					}
				});
			}
		});

		bEditPassword = (Button) findViewById(R.id.bEditPassword);
		bEditPassword.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				dEditPassword = new Dialog(v.getContext());

				//Edit Password dialog settings
				dEditPassword.setContentView(R.layout.usereditpassworddialog);
				dEditPassword.setTitle(R.string.edit_password);
				dEditPassword.setCanceledOnTouchOutside(true);
				dEditPassword.show();

				//Edit password dialog view setup
				bEditNewPassword = (Button) dEditPassword.findViewById(R.id.bEditNewPassword);
				bCancelEditPassword = (Button) dEditPassword.findViewById(R.id.bCancelEditPassword);
				eCurrentPassword = (EditText) dEditPassword.findViewById(R.id.eCurrentPassword);
				eNewPassword = (EditText) dEditPassword.findViewById(R.id.eNewPassword);
				eNewPasswordAgain = (EditText) dEditPassword.findViewById(R.id.eNewPasswordAgain);

				bEditNewPassword.setOnClickListener( new View.OnClickListener(){
					public void onClick(View v){
						if(eNewPassword.getText().toString().compareTo(eNewPasswordAgain.getText().toString()) == 0)
						{
							dProgressDialog = ProgressDialog.show(FoodoUserManagement.this, "Working", "Sending data...");
							Thread thread = new Thread( new Runnable(){
								public void run(){
									uManager.userEditPassword(
											eCurrentPassword.getText().toString(),
											eNewPassword.getText().toString());
									hDialogs.sendEmptyMessage(EDITPASSWORD_DIALOG);
								}
							});
							thread.start();							
						}
						else
						{
							Toast.makeText(v.getContext(), R.string.password_conflict, Toast.LENGTH_SHORT).show();
						}
					}
				});				

				bCancelEditPassword.setOnClickListener( new View.OnClickListener(){
					public void onClick(View v){
						dEditPassword.cancel();
					}
				});
			}
		});

		bViewOldOrders = (Button) findViewById(R.id.bViewOldOrders);
		bViewOldOrders.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Intent oldOrders = new Intent(v.getContext(), FoodoUserOldOrders.class);
				startActivityForResult(oldOrders, OLDORDERS);
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
				uManager.deauthenticate();
				FoodoUserManagement.this.finish();
			}
		});
	}

	/**
	 * Thread handler, used to handle sending 
	 * new information about the user to the server.
	 */
	private final Handler hDialogs  = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dProgressDialog.dismiss();
			if(uManager.isAuthenticated() && (msg.what == EDITINFO_DIALOG)){
				dEditInfo.dismiss();
				getUserInfo();
				setupTextView();
				Toast.makeText(FoodoUserManagement.this, R.string.confirm_edit, Toast.LENGTH_SHORT).show();
			}
			else if (uManager.isAuthenticated() && (msg.what == EDITPASSWORD_DIALOG)){
				dEditPassword.dismiss();
				Toast.makeText(FoodoUserManagement.this, R.string.confirm_edit, Toast.LENGTH_SHORT).show();
			}
			else{
				Toast.makeText(FoodoUserManagement.this, "Sending data failed: " + uManager.getError(), Toast.LENGTH_LONG).show();
			}
		}
	};
}

