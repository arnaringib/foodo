package is.hi.foodo;

import is.hi.foodo.user.UserManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class FoodoLogin extends Activity implements Runnable {
	
	private static final String TAG = "FoodoLogin";
	private SharedPreferences  app_preferences;
	
	private UserManager uManager;
	private ProgressDialog pd;
	private long id;
	
	private CharSequence emailValue;
	private CharSequence passwordValue;
	
	@Override
    public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();
        
		//load up the layout 
        setContentView(R.layout.login); 
        
        // get the button resource in the XML file and assign it to a local variable of type Button 
        Button login = (Button)findViewById(R.id.login_button); 
        Button register = (Button)findViewById(R.id.register_button);
        
        //Bind form values
        EditText emailText = (EditText)findViewById(R.id.txt_email);
        EditText passwordText = (EditText)findViewById(R.id.txt_password);
        
        emailValue = emailText.getText();
        passwordValue = passwordText.getText();
        
       app_preferences = PreferenceManager.getDefaultSharedPreferences(this);
       
        // this is the action listener 
        login.setOnClickListener( new OnClickListener() 
        { 
        	public void onClick(View viewParam) 
        	{ 
        		pd = ProgressDialog.show(FoodoLogin.this, "Working..", "Logging in");
				Thread thread = new Thread(FoodoLogin.this);
				thread.start();
        	}
        });
        
        // this is the action listener 
        register.setOnClickListener( new OnClickListener() 
        { 
        	public void onClick(View view) 
        	{ 
        		Context context = getApplicationContext();
        		Intent intent = new Intent(context, FoodoRegister.class);
        		startActivityForResult(intent, 1);
        	}
        });
	
	}

	@Override
	public void run() {
		uManager.authenticate(emailValue.toString(), passwordValue.toString());
		id = uManager.getId();
		handler.sendEmptyMessage(0);
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			if (uManager.isAuthenticated()){
				Toast.makeText(FoodoLogin.this, "Successful login!", Toast.LENGTH_LONG).show();
		       // Save user preferences.
				SharedPreferences.Editor editor = app_preferences.edit();
	            editor.putBoolean("access", true);
	            editor.putLong("user",id);
		       // Don't forget to commit edits!!!
		       editor.commit();
		       FoodoLogin.this.finish();
		    }
			else{
				Log.d(TAG, "login failed!");
				Toast.makeText(FoodoLogin.this, "Login failed: " + uManager.getError(), Toast.LENGTH_LONG).show();
			}
		}
	};
}

