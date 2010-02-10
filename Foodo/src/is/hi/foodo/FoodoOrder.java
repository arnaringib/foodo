package is.hi.foodo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FoodoOrder extends Activity {
	private static final String TAG = "FoodoOrder";
	private static final String FULLORDER = "FullOrder";
	private static final String RESTNAME = "RestName";
	
	private Button btnConfOrder, btnChangeOrder;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		//Check if resuming from a saved instance state
        String order = (savedInstanceState != null ? savedInstanceState.getString(FULLORDER) : null);
        String restName = (savedInstanceState != null ? savedInstanceState.getString(RESTNAME) : null);
        //Get id from intent if not set
        if(order == null && restName == null)
        {
        	Bundle extras = getIntent().getExtras();
        	order = extras != null ? extras.getString(FULLORDER) : null;
        	restName = extras != null ? extras.getString(RESTNAME) : null;
        }
		
        setContentView(R.layout.order); 
        
       	TextView mOrderName = (TextView) this.findViewById(R.id.OrderPlace);
        mOrderName.setText(restName);
        TextView mOrder = (TextView) this.findViewById(R.id.totalOrder);
        mOrder.setText(order);
        
		setupButtons();
        Log.d(TAG, "FoodoOrder");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	
	}
	
	public void confOrder(){
		setResult(RESULT_OK);
		finish();
	}
	
	public void changeOrder(){
		finish();
	}
	
	public void setupButtons() {
		this.btnConfOrder = (Button)this.findViewById(R.id.bConfOrder);
		this.btnChangeOrder = (Button)this.findViewById(R.id.bChangeOrder);
		
		btnConfOrder.setOnClickListener(new clicker());
		btnChangeOrder.setOnClickListener(new clicker());
	}
	// button click listener
	class clicker implements Button.OnClickListener
    {     
		public void onClick(View v)
		{
			if(v == btnConfOrder){
				confOrder();
			}
			else if(v == btnChangeOrder){
				changeOrder();
			}
		}
    }	
}

