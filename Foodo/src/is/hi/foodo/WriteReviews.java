package is.hi.foodo;

import is.hi.foodo.net.FoodoServiceException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class WriteReviews extends Activity implements View.OnClickListener, Runnable {
	
	//private static final String TAG = "WriteReviews";
	
	private static final int REVIEW_SUCCESSFUL = 1;
	private static final int REVIEW_FAILED = 2;
	
	private ProgressDialog pd;
	
	private RestaurantDbAdapter mDbHelper;
	
	private Long mRowId;
	
	private Cursor mCursor;
	
	private EditText mReview;
	private Button mButton;
	
	@Override 
    public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.writereviews);
        
        mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();
        
        //Check if resuming from a saved instance state
        mRowId = (savedInstanceState != null ? savedInstanceState.getLong(RestaurantDbAdapter.KEY_ROWID) : null);
        //Get id from intent if not set
        if (mRowId == null)
        {
        	Bundle extras = getIntent().getExtras();
        	mRowId = extras != null ? extras.getLong(RestaurantDbAdapter.KEY_ROWID) : null;
        }
        
        mReview = (EditText) findViewById(R.id.review);
        
        mButton = (Button) findViewById(R.id.send_review);
        mButton.setOnClickListener(this);
        
        populateView();
	}
	
	private void populateView() {
		if (mRowId != null)
    	{
    		mCursor = mDbHelper.fetchRestaurant(mRowId);
    		startManagingCursor(mCursor);
    		
    		TextView mPlaceName = (TextView) findViewById(R.id.name);
    		mPlaceName.setText(mCursor.getString(mCursor.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
    	}
	}

	@Override
	public void onClick(View v) {
		pd = ProgressDialog.show(WriteReviews.this, "Sending..", "Updating");
		Thread thread = new Thread(WriteReviews.this);
		thread.start();
		
		//Toast.makeText(this, mReview.getText().toString(), Toast.LENGTH_LONG).show();
	}

	@Override
	public void run() {
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		String user_api_key = settings.getString("api_key", "");
		
		try {
			((FoodoApp)getApplicationContext()).getService().submitReview(mRowId, user_api_key, mReview.getText().toString());
			handler.sendEmptyMessage(REVIEW_SUCCESSFUL);
		} catch (FoodoServiceException e) {
			handler.sendEmptyMessage(REVIEW_FAILED);
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pd.dismiss();
			if (msg.what == REVIEW_FAILED) {
				Toast.makeText(WriteReviews.this, "Could not send review", Toast.LENGTH_SHORT).show();
			}
			finish();
		}
	};
     

}

