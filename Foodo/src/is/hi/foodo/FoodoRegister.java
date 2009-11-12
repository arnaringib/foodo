package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FoodoRegister extends Activity {
	private Button btnReg;
	static final int PASSWORD_DIALOG = 0, REGISTER_DIALOG = 1;
	CharSequence firstName, lastName, email, password, rePassword;
	FoodoUserManager uManager;
	 
	@Override 
    public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
        setContentView(R.layout.register); 
        
       // setup();
        getRegistration();
        
        btnReg = (Button)this.findViewById(R.id.register_button);
        btnReg.setOnClickListener(new clicker());
        
       uManager = new FoodoUserManager();
	}
	
	/*public void setup(){
		final EditText rFirstName = (EditText) findViewById(R.id.txt_firstname);
		
		final EditText rLastName = (EditText) findViewById(R.id.txt_lastname);
		
		final EditText rEmail = (EditText) findViewById(R.id.txt_email);
		
		final EditText rPassword = (EditText) findViewById(R.id.txt_password);
		
	}*/
		
	
	public void getRegistration() {
		EditText eFirstName = (EditText) findViewById(R.id.txt_firstname);
		EditText eLastName = (EditText) findViewById(R.id.txt_lastname);
		EditText eEmail = (EditText) findViewById(R.id.txt_email);
		EditText ePassword = (EditText) findViewById(R.id.txt_newPassword);
		EditText eRePassword = (EditText) findViewById(R.id.txt_rePassword);
		
		firstName = eFirstName.getText();
		lastName = eLastName.getText();
		email = eEmail.getText();
		password = ePassword.getText();
		rePassword = eRePassword.getText();
		
	}
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
				if(TextUtils.isEmpty(firstName)|| TextUtils.isEmpty(lastName)|| TextUtils.isEmpty(email) || 
						 TextUtils.isEmpty(password)){
					showDialog(REGISTER_DIALOG);
				}
				 if (!TextUtils.equals(password, rePassword)) {
					showDialog(PASSWORD_DIALOG);
				}
				else{
					
					uManager.signup(firstName.toString(), lastName.toString(),email.toString(), password.toString());
				}
			
			}
		}
    }
}
