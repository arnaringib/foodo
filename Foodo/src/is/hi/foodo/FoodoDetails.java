package is.hi.foodo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class FoodoDetails extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
	}
	/* create the menu items */
	@Override 
	public boolean onCreateOptionsMenu(Menu menu) {
		 menu.add(0,0,0,"Info");		 
		 return true;
	}
	
	CharSequence text1 = "In progress";
	int duration = Toast.LENGTH_SHORT;

	/* when menu button option selected */
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) {
		Context context = getApplicationContext();
		switch (item.getItemId()) {
		case 0:
			Toast toast1 = Toast.makeText(context, text1, duration);
			toast1.show();
			return true;
		}
		return false;
	}
  

}
