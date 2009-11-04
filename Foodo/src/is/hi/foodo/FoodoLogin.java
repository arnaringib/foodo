package is.hi.foodo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class FoodoLogin extends Activity {
	
	@Override 
    public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState); 
        
		//load up the layout 
        setContentView(R.layout.login); 
        
        // get the button resource in the xml file and assign it to a local variable of type Button 
        Button login = (Button)findViewById(R.id.login_button); 
        Button register = (Button)findViewById(R.id.register_button); 
        
        // this is the action listener 
        login.setOnClickListener( new OnClickListener() 
        { 
          
          public void onClick(View viewParam) 
          { 
        	  Context context = getApplicationContext();
        	  Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show();
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
}

