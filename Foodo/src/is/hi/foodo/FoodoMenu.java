package is.hi.foodo;


import is.hi.foodo.net.FoodoServiceException;
import is.hi.foodo.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



public class FoodoMenu extends ListActivity{

	private static final String TAG = "FoodoMenu";

	private static final int GETMENU = 0;
	private static final int SENDORDER = 1;

	public static final String ID = "ID";
	public static final String ITEMID = "ITEMID";
	public static final String ITEMNAME = "ITEMNAME";
	public static final String PRICE = "PRICE";
	public static final String AMOUNT = "AMOUNT";
	public static final String SELECTED = "SELECTED";

	private static final String TAB = "\t";
	private static final String TIMES = "x";
	private static final String NEWLINE = "\n";

	private static int item;

	private ProgressDialog pd; 
	private RestaurantDbAdapter mDbHelper;
	private Long mRowId;
	private String dialogItem;
	private static int amount = 0;
	private Button btnConfOrder, btnUp, btnDown;
	private View itemLayout, orderLayout;
	private TextView txtItemCounter, txtItemText;

	View listViewItem;
	private int tempPrice; 
	private UserManager uManager;

	static final int MENU_DIALOG = 0;
	static final int ORDER_DIALOG = 1;
	//private Cursor mRestaurantCursor;

	List< Map<String,String> > mMenu;
	List< Map<String,String> > mOrder;

	@Override 
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);

		setContentView(R.layout.menu); 

		mDbHelper = new RestaurantDbAdapter(this);
		mDbHelper.open();

		uManager = ((FoodoApp)this.getApplicationContext()).getUserManager();

		LayoutInflater factory = LayoutInflater.from(this);
		itemLayout = factory.inflate(R.layout.menudialog, null);
		LayoutInflater factoryOrder = LayoutInflater.from(this);
		orderLayout = factoryOrder.inflate(R.layout.orderdialog, null);

		getListView().setTextFilterEnabled(true);
		getListView().setClickable(true);
		registerForContextMenu(getListView());

		setupButtons();

		//Check if resuming from a saved instance state
		mRowId = (savedInstanceState != null ? savedInstanceState.getLong(RestaurantDbAdapter.KEY_ROWID) : null);
		//Get id from intent if not set
		if (mRowId == null)
		{
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(RestaurantDbAdapter.KEY_ROWID) : null;
		}

		Log.d(TAG, "ReId is: " + mRowId);

		populateView();
		mDbHelper.close();
	}


	@Override
	protected void onResume() {
		super.onResume();

		mMenu = new ArrayList<Map<String,String>>();
		mOrder = new ArrayList<Map<String, String>>();
		loadMenu();
		setupList();
	}

	private void populateView() {
		if (mRowId != null)
		{
			Cursor restaurant = mDbHelper.fetchRestaurant(mRowId);
			startManagingCursor(restaurant);

			TextView mPlaceName = (TextView) this.findViewById(R.id.menuPlace);
			mPlaceName.setText(restaurant.getString(restaurant.getColumnIndexOrThrow(RestaurantDbAdapter.KEY_NAME)));
		}
	}

	public void setupList() {
		SimpleAdapter adapter = new SimpleAdapter(
				this,
				mMenu, 
				R.layout.listmenu,
				new String[] {ITEMNAME, PRICE, SELECTED},
				new int[] {R.id.nameMenu, R.id.priceMenu, R.id.itemSelected}
		);
		setListAdapter(adapter);
	}

	public String createOrder(){
		String result = "";
		int sum = 0;
		for(int i = 0; i < mOrder.size(); i++){
			result += mOrder.get(i).get(ITEMNAME);
			result += NEWLINE;
			result += mOrder.get(i).get(AMOUNT);
			result += TIMES;
			result += mOrder.get(i).get(PRICE);
			result += TAB;
			result += "=";
			result += TAB;
			result += Integer.parseInt(mOrder.get(i).get(PRICE))*Integer.parseInt(mOrder.get(i).get(AMOUNT));
			result += NEWLINE;
			result += NEWLINE;
			sum += Integer.parseInt(mOrder.get(i).get(PRICE))*Integer.parseInt(mOrder.get(i).get(AMOUNT));
		}
		result += NEWLINE;
		result += "Total cost: "+ sum;
		return result;
	}

	/**
	 * Use: setupButtons()
	 * 
	 * Finds the buttonview for the confirm button in the view,
	 * and adds a click listener
	 */
	public void setupButtons() {
		this.btnConfOrder = (Button)this.findViewById(R.id.bConfOrder);
		btnConfOrder.setOnClickListener(new View.OnClickListener(){
			public void onClick(View v)
			{
				Context context = getApplicationContext();				
				if(v==btnConfOrder){
					if(uManager.isAuthenticated()){
						if(mOrder.size() > 0){
							TextView totalOrder = (TextView) orderLayout.findViewById(R.id.totalOrder);
							totalOrder.setText(createOrder());
							showDialog(ORDER_DIALOG);
						}
						else{
							Toast.makeText(context, "You haven't picked any items.", Toast.LENGTH_SHORT).show();
						}
					}
					else {
						Toast.makeText(context, "You have to be signed in", Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

	}

	@Override
	protected void onPrepareDialog (int id, Dialog dialog) {
		switch(id) {
		case MENU_DIALOG:
			dialog.setTitle(dialogItem);
			tempPrice = Integer.parseInt( mMenu.get(item).get(PRICE));
			txtItemText.setText("Total price: " + (tempPrice*amount));
		}
	}
	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
		case MENU_DIALOG:
			txtItemText = (TextView) itemLayout.findViewById(R.id.itemText);
			tempPrice = Integer.parseInt( mMenu.get(item).get(PRICE));
			txtItemText.setText("Total price: " + (tempPrice*amount));

			btnUp = (Button) itemLayout.findViewById(R.id.btnUp);
			btnUp.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					amount++;
					txtItemCounter.setText("" + amount);
					txtItemText.setText("Total price: " + (tempPrice*amount));

				}
			});
			btnDown = (Button) itemLayout.findViewById(R.id.btnDown);
			btnDown.setOnClickListener(new Button.OnClickListener(){
				@Override
				public void onClick(View v)
				{
					if(amount>0){
						amount--;
					}
					txtItemCounter.setText("" + amount);
					txtItemText.setText("Total price: " + (tempPrice*amount));

				}

			});
			return new AlertDialog.Builder(FoodoMenu.this)
			.setTitle(dialogItem)
			.setView(itemLayout)	
			.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					boolean itemNotFound = true;
					// Adds a * for selected item on the menu thats added to the order.
					if((amount > 0) && (mMenu.get(item).get(SELECTED).length() == 0)){
						mMenu.get(item).put(SELECTED, "*");
					}
					else if(amount == 0){
						mMenu.get(item).put(SELECTED, "");
					}
					for(int i = 0; i < mOrder.size(); i++){
						if(Integer.parseInt(mOrder.get(i).get(ID)) == item){
							if(amount > 0){
								mOrder.get(i).put(AMOUNT, Integer.toString(amount));

							}
							else{
								mOrder.remove(i);

							}
							itemNotFound = false;
							break;
						}
					}
					if(itemNotFound){
						// 	Map for the order information
						Map<String, String> map = new HashMap<String, String>();
						map.put(ID, mMenu.get(item).get(ID));
						map.put(ITEMID, mMenu.get(item).get(ITEMID));
						map.put(ITEMNAME, mMenu.get(item).get(ITEMNAME));
						map.put(AMOUNT, Integer.toString(amount));
						map.put(PRICE, mMenu.get(item).get(PRICE));
						mOrder.add(map);
					}
					setupList();
					getListView().setSelection(item);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					//amount = 0;
					dismissDialog(MENU_DIALOG);
					//	removeDialog(MENU_DIALOG);
				}
			})
			.create();
		case ORDER_DIALOG:
			//LayoutInflater factoryOrder = LayoutInflater.from(this);
			//final View layoutOrder = factoryOrder.inflate(R.layout.orderdialog, null);
			return new AlertDialog.Builder(FoodoMenu.this)
			.setTitle(R.string.your_order)
			.setView(orderLayout)
			.setPositiveButton(R.string.change_order, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dismissDialog(ORDER_DIALOG);
				}
			})
			.setNegativeButton(R.string.confirm_order, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					dismissDialog(ORDER_DIALOG);
					pd = ProgressDialog.show(FoodoMenu.this, "Sending...", "Sending order");
					Thread thread = new Thread( new Runnable(){
						public void run(){
							FoodoApp app = ((FoodoApp)FoodoMenu.this.getApplicationContext());
							String api_key = app.getUserManager().getApiKey();
							try {
								app.getService().submitOrder(
										mRowId, 
										api_key, 
										mOrder
								);
							} catch (FoodoServiceException e) {
								Log.d(TAG, "Not able to send order", e);
								Toast.makeText(FoodoMenu.this, "Order could not be processed! Please try again", Toast.LENGTH_LONG).show();
							}
							handler.sendEmptyMessage(SENDORDER);			
						}
					});
					thread.start();
					Log.d(TAG,"�r��ur stopp");
				}
			})
			.create();
		default:
			return null;
		}
	}
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		item = position;
		amount = 0;

		dialogItem = mMenu.get(item).get(ITEMNAME);
		Log.i(TAG, dialogItem);

		for(int i = 0; i < mOrder.size(); i++){
			if(Integer.parseInt(mOrder.get(i).get(ID)) == item){
				amount = Integer.parseInt(mOrder.get(i).get(AMOUNT));
				break;
			}
		}
		txtItemCounter = (TextView) itemLayout.findViewById(R.id.itemCounter);
		txtItemCounter.setText(Integer.toString(amount));

		showDialog(MENU_DIALOG);
	}

	private void loadMenu() {
		pd = ProgressDialog.show(FoodoMenu.this, "Loading..", "Getting menu");
		Thread thread = new Thread( new Runnable(){
			public void run(){
				try {
					JSONArray jMenu = ((FoodoApp)getApplicationContext()).getService().getRestaurantMenu(mRowId);
					int n = jMenu.length();

					for (int i = 0; i < n; i++)
					{
						JSONObject r = jMenu.getJSONObject(i);
						Map<String,String> map = new HashMap<String, String>();
						map.put(ID, Integer.toString(i));
						map.put(ITEMID, r.getString("id"));
						map.put(ITEMNAME, r.getString("name"));
						map.put(PRICE, r.getString("price"));
						map.put(SELECTED, "");
						mMenu.add(map);
					}
				}
				catch (JSONException e) {
					//TODO
					Log.d(TAG, "Menu", e);
				}
				catch (FoodoServiceException e)
				{
					Log.d(TAG, "Exception while getting menu", e);
				}

				handler.sendEmptyMessage(GETMENU);			
			}
		});
		thread.start();
	}

	private final Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == GETMENU){
				setupList();
				pd.dismiss();
			}
			else if(msg.what == SENDORDER){
				pd.dismiss();
				setResult(RESULT_OK);
				finish();
			}
		}
	};

}

