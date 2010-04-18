package is.hi.foodo;

import is.hi.foodo.net.FoodoService;
import is.hi.foodo.user.UserManager;

import java.io.BufferedInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


public class FoodoDetails extends Activity {

	private static final String TAG = "FoodoDetails";
	private static final int MENU_VIEW = 2;

	static final int RATING_DIALOG = 0;
	private RatingBar showRatingbar;
	public RatingBar giveRatingbar;
	private Long mRowId;
	private float mRating;

	RestaurantDbAdapter mDbHelper;
	RestaurantLoader mService;
	Cursor restaurant;
	Cursor types;
	private UserManager uManager;

	//View items
	private Button btnRate, btnReviews, btnCall, btnViewOnMap, btnLog;
	private Button btnMenu;
	private TextView mNameText;
	private TextView mInfo;
	private TextView mTypes;
	private ImageView mLogo;
	boolean closeOnViewMap = true;

	//Temporary stings for toasts
	static final CharSequence bTextDescr = "No description ..";
	static final CharSequence bTextReviews = "No reviews... :(";
	static final CharSequence bTextCall = "I cant call ..";
	static final CharSequence bTextViewOnMap = "Cant view on map ..";
	private CharSequence str;
	//private long user_id;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.details);
		setupButtons();

		showRatingbar = (RatingBar) findViewById(R.id.indicator_ratingbar);

		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();

		mNameText = (TextView) findViewById(R.id.ReName);
		mInfo = (TextView) findViewById(R.id.ReInfo);
		mTypes = (TextView) findViewById(R.id.ReTypes);
		mLogo = (ImageView) findViewById(R.id.ReLogo);

		//Open connection to DB adapter
		mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();

		mService = new RestaurantLoader(mDbHelper, ((FoodoApp)getApplicationContext()).getService());

		//Check if resuming from a saved instance state
		mRowId = (savedInstanceState != null ? savedInstanceState.getLong(RestaurantDbAdapter.KEY_ROWID) : null);
		//Get id from intent if not set
		if (mRowId == null)
		{
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(RestaurantDbAdapter.KEY_ROWID) : null;

			//Started from URI intent
			if (mRowId == null)
			{
				// There might be a more elegant way to do this
				mRowId = Long.valueOf(getIntent().getDataString().replace("foodo://restaurant/", "").replace("/",""));
				closeOnViewMap = false;
			}

		}
		try {
			populateView();
		} catch (MalformedURLException e) {
			Log.i(TAG, "No logo!");
			e.printStackTrace();
		}        

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		restaurant.deactivate();
		mDbHelper.close();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MENU_VIEW) {
			if (resultCode == RESULT_OK) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder
				.setTitle(R.string.your_order)
				.setMessage(R.string.order_processed)
				.setPositiveButton(R.string.ok, null)
				.show();
			}
		}
		// change login button to logout if user has logged in
		if(uManager.isAuthenticated()){
			CharSequence str = getString(R.string.logout);
			Button b = (Button)findViewById(R.id.bLog);
			b.setText(str);
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case RATING_DIALOG:
			LayoutInflater factory = LayoutInflater.from(this);
			final View layout = factory.inflate(R.layout.ratingdialog, null);

			RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
			rb.setRating(mRating);

			return new AlertDialog.Builder(FoodoDetails.this)
			.setTitle("Rating")
			.setView(layout)
			.setPositiveButton("Rate!", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK, add new rating */
					RatingBar rb = (RatingBar) layout.findViewById(R.id.ratingbar);
					Log.d(TAG, "Giving rating: " + rb.getRating());

					//Get user api key from userManager
					String user_api_key = uManager.getApiKey();

					FoodoService service = ((FoodoApp)FoodoDetails.this.getApplicationContext()).getService();

					try {
						float newrating = (float) service.submitRating(
								mRowId, 
								user_api_key, 
								(int)rb.getRating()
						).getDouble("rating");  	
						showRatingbar.setRating(newrating);

						//Update rating in database
						mDbHelper.updateRating(mRowId, newrating);
					}
					catch (Exception e) {
						//TODO should notify of this?
						Log.d(TAG, "Not able to submit rating", e);
					}
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					/* User clicked cancel so do some stuff */
				}
			})
			.create();
		}
		return null;
	}

	/**
	 * Fills data into the view
	 * @throws MalformedURLException 
	 */
	protected void populateView() throws MalformedURLException {

		if (mRowId != null)
		{
			if (!mDbHelper.hasRestaurant(mRowId))
			{
				Log.d(TAG, "Restaurant not found!");
				if (!mService.updateRestaurant(mRowId))
				{
					Log.d(TAG, "Failed to fetch restaurant");
					Toast.makeText(FoodoDetails.this, "Restaurant not found", Toast.LENGTH_LONG).show();
				}
			}
			restaurant = mDbHelper.fetchRestaurant(mRowId);
			//startManagingCursor(restaurant);

			String typeString = makeTypeString();
			Log.d(TAG, typeString);
			mTypes.setText(typeString);

			mRating = restaurant.getFloat(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_RATING));

			String title;
			if(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)).length() < 40){
				title = restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME));
			}
			else{
				title = (restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)).subSequence(0,40) + "...");
			}

			mNameText.setText(title);
			showRatingbar.setRating(mRating);


			URL aURL = new URL("http://media.foodo.morpho.nord.is/logo/" + restaurant.getInt(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_ROWID)) + ".jpg");
			mLogo.setImageBitmap(Bitmap.createScaledBitmap(getRemoteImage(aURL), 100, 100, false) );

			mInfo.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_ADDRESS))
					+ '\n'
					+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_ZIP))
					+ ' '
					+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_CITY))
					+ '\n'
					+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_EMAIL))
					+ '\n' 
					+ restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_WEBSITE))
			);
		}

		//Initialize login/logout button
		if(uManager.isAuthenticated()) {
			str = getString(R.string.logout);
		} else {
			str = getString(R.string.login);
		}
		btnLog.setText(str);

	}

	private String makeTypeString() {
		StringBuilder builder = new StringBuilder();

		types = mDbHelper.fetchRestaurantTypes(mRowId);

		if (types.moveToFirst()) {
			do {
				builder.append(types.getString(types.getColumnIndex(RestaurantDbAdapter.KEY_TYPE)));

				if (!types.isLast()) {
					builder.append(", ");
				}
			} 
			while (types.moveToNext());
		}
		else {
			builder.append("...");
		}

		return builder.toString();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(RestaurantDbAdapter.KEY_ROWID, mRowId);
	}

	public void setupButtons() {
		this.btnMenu = (Button)this.findViewById(R.id.bMenu);
		this.btnRate = (Button)this.findViewById(R.id.bRate);
		this.btnReviews = (Button)this.findViewById(R.id.bReviews);
		this.btnCall = (Button)this.findViewById(R.id.bCall);
		this.btnLog = (Button)this.findViewById(R.id.bLog);

		this.btnViewOnMap = (Button)this.findViewById(R.id.bViewOnMap);

		btnMenu.setOnClickListener(new clicker());
		btnRate.setOnClickListener(new clicker());
		btnReviews.setOnClickListener(new clicker());
		btnCall.setOnClickListener(new clicker()); 
		btnLog.setOnClickListener(new clicker()); 
		btnViewOnMap.setOnClickListener(new clicker());

	}

	public void reviews() {
		Intent i = new Intent(this, ReadReviews.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i, 1);
	}

	public void menu(){
		Intent i = new Intent(this,FoodoMenu.class);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, mRowId);
		startActivityForResult(i,MENU_VIEW);
	}
	public void viewOnMap(){
		if(closeOnViewMap) {
			getIntent().putExtra("Latitude", restaurant.getInt(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)));
			getIntent().putExtra("Longitude", restaurant.getInt(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)));
			setResult(RESULT_OK, getIntent());
			finish();
		}
		else {
			Intent i = new Intent(this, FoodoMap.class);
			i.putExtra("Latitude", restaurant.getInt(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LAT)));
			i.putExtra("Longitude", restaurant.getInt(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_LNG)));	
			startActivity(i);
		}
	}
	public void login(){
		Intent login = new Intent(this, FoodoLogin.class);
		startActivityForResult(login, 1);
	}

	public void logout(){
		uManager.deauthenticate();
		Context context = getApplicationContext();
		Toast.makeText(context, "You have been logged out", Toast.LENGTH_SHORT).show();
		CharSequence str = getString(R.string.login);
		Button b = (Button)findViewById(R.id.bLog);
		b.setText(str);
	}
	// Code snippet from http://www.anddev.org/
	public Bitmap getRemoteImage(final URL aURL) {
		Bitmap bm;
		try { 
			final URLConnection conn = aURL.openConnection(); 
			conn.connect(); 
			final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream()); 
			bm = BitmapFactory.decodeStream(bis); 
			bis.close(); 
			return bm; 
		} catch (Exception e) { 
			Log.d("DEBUGTAG", "Oh noooz an error...");
			bm = BitmapFactory.decodeResource(this.getResources(), R.drawable.sample_details);
		} 
		return bm; 
	}
	// button click listener
	class clicker implements Button.OnClickListener
	{     

		public void onClick(View v)
		{
			// TODO: Put the following code in a Case!
			Context context = getApplicationContext();
			if(v==btnRate){
				if(uManager.isAuthenticated()){
					showDialog(RATING_DIALOG);
				}
				else {
					Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
				}
			}
			else if(v==btnReviews){
				reviews();
			}
			else if(v==btnCall){
				try {
					Intent callIntent = new Intent(Intent.ACTION_CALL) ; 
					callIntent.setData(Uri.parse("tel:" + restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_PHONE))));
					startActivity(callIntent);
				} catch (Exception e) {
					Log.e(TAG, "Calling caused an exception: ", e);
				}

			}
			else if(v == btnViewOnMap){
				viewOnMap();
				//	Log.i(TAG, "View on map click!");
			}
			else if (v == btnLog){
				if(uManager.isAuthenticated()){
					logout();
				}
				else{					
					login();
				}
			}
			else if(v == btnMenu){
				menu();				
			}

		}
	}	

}
