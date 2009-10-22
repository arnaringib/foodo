package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FoodoFilter extends Activity {
	final CharSequence[] types = {"Fast", "Fine dining",
			"Family", "Casual", "Sea", "Launch", "Mexican",
			"Asian", "Vegetarian", "Buffet", "Sandwiches",
			"Bistro", "Drive-in", "Take out", "Steakhouse",
			"Sushi"};
	final boolean[] bTypes = {false, false,
			false, false, false, false, false,
			false, false, false, false,
			false, false, false, false,
			false};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);
		
		buttons();
	}
	
	public void buttons(){
		final Button bFilterTypes = (Button) findViewById(R.id.bFilterTypes);
		View.OnClickListener lFilterTypes = new View.OnClickListener(){
			public void onClick(View v){			
				AlertDialog.Builder builder = new AlertDialog.Builder(FoodoFilter.this)
					.setTitle("Restaurants types")
					.setMultiChoiceItems(types, bTypes, new DialogInterface.OnMultiChoiceClickListener() {
					    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					    	//nothing
					    }
					});

				AlertDialog alert = builder.show();
			}
		} ;
		
		bFilterTypes.setOnClickListener(lFilterTypes);
		
		final Button bSaveChanges = (Button) findViewById(R.id.bSave);
		View.OnClickListener lSaveChanges = new View.OnClickListener(){
			public void onClick(View v){
				//Save changes
				setResult(RESULT_OK);
				finish();
			}
		} ;
		
		bSaveChanges.setOnClickListener(lSaveChanges);
	}
}
