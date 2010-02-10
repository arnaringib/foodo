package is.hi.foodo;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FoodoOrder extends Activity {
	private static final String TAG = "FoodoOrder";
	
	private Button btnConfOrder, btnChangeOrder;
	
	@Override 
    protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

        setContentView(R.layout.order); 
        
		setupButtons();
		
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

