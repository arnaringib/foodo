package is.hi.foodo;

import is.hi.foodo.user.UserManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FoodoRegister extends Activity implements Runnable {
	
	private Button btnReg;
	static final int PASSWORD_DIALOG = 0, REGISTER_DIALOG = 1;
	private CharSequence firstName, lastName, email_1, email_2, password, rePassword;
	private UserManager uManager;
	
	private ProgressDialog pd;
	 
	@Override 
    public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
        setContentView(R.layout.register); 

        getRegistration();
        
        btnReg = (Button)this.findViewById(R.id.register_button);
        btnReg.setOnClickListener(new clicker());
        
        uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();
	}
	
	
	public void getRegistration() {
		EditText eFirstName = (EditText) findViewById(R.id.txt_firstname);
		EditText eLastName = (EditText) findViewById(R.id.txt_lastname);
		EditText eEmail_1 = (EditText) findViewById(R.id.txt_email_1);
		EditText eEmail_2 = (EditText) findViewById(R.id.txt_email_2);
		EditText ePassword = (EditText) findViewById(R.id.txt_newPassword);
		EditText eRePassword = (EditText) findViewById(R.id.txt_rePassword);
		
		firstName = eFirstName.getText();
		lastName = eLastName.getText();
		email_1 = eEmail_1.getText();
		email_2 = eEmail_2.getText();
		password = ePassword.getText();
		rePassword = eRePassword.getText();
		
	}
	
	// Dialogs in case passwords don't match and/or not all fields are filled out
	protected Dialog onCreateDialog(int id) {
		switch (id) {
			case PASSWORD_DIALOG:
				return new AlertDialog.Builder(FoodoRegister.this)
					.setMessage("Passwords don't match")
				    .setCancelable(false)
				    .setNeutralButton("Return", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.dismiss();
				           }
				       })
				       .create();
				case REGISTER_DIALOG:
				return new AlertDialog.Builder(FoodoRegister.this)
					.setMessage("Please fill out all fields")
				    .setCancelable(false)
				    .setNeutralButton("Return", new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   dialog.dismiss();
				           }
				       })
				       .create();
				
		}
		return null;
	}
	
	// button click listener
	class clicker implements Button.OnClickListener
    {     
		public void onClick(View v)
		{
			if (v==btnReg){
				if(TextUtils.isEmpty(firstName)|| TextUtils.isEmpty(lastName)|| TextUtils.isEmpty(email_1) || 
						TextUtils.isEmpty(email_2) || TextUtils.isEmpty(password)){
					showDialog(REGISTER_DIALOG);
				}
				else if (!TextUtils.equals(password, rePassword)) {
					showDialog(PASSWORD_DIALOG);
				}
				else{
					pd = ProgressDialog.show(FoodoRegister.this, "Working..", "Registering");
					Thread thread = new Thread(FoodoRegister.this);
					thread.start();
				}
			
			}
		}
    }

	@Override
	public void run() {
		uManager.signup(firstName.toString(), lastName.toString(),email_1.toString()+"@"+email_2.toString(), password.toString());
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			if (uManager.isAuthenticated()){
				Toast.makeText(FoodoRegister.this, "User registered!", Toast.LENGTH_LONG).show();
				FoodoRegister.this.finish();
			}
			else
				Toast.makeText(FoodoRegister.this, "Registration failed: " + uManager.getError(), Toast.LENGTH_LONG).show();
		}
	};
}