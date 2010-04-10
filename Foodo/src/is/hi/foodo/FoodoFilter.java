package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class FoodoFilter extends Activity {
	private static String TAG = "FoodoFilter";

	public boolean bResult = true;
	private static AlertDialog.Builder buRestaurantTypes;
	private static AlertDialog aRestaurantTypes;
	private Button bSelectAll, bDeselectAll, bFilterTypes,
	bSaveChanges;
	private ImageButton bPriceLow, bPriceMedium, bPriceHigh;
	private EditText eRadiusText, eRatingFrom, eRatingTo;
	private SeekBar bSeekBarFilter;
	private LayoutInflater inflater;
	private View typesLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);

		restaurantTypesSetup();
		setup();
		listeners();
	}

	public void setup(){
		eRadiusText = (EditText) findViewById(R.id.fRadiusEdit);
		eRadiusText.setText("" + Filter.radius);

		bSeekBarFilter = (SeekBar) findViewById(R.id.fRadiusBar);
		bSeekBarFilter.setProgress( (int)( ( Filter.radius / 20000.0) * 100.0));

		// Pricing
		bPriceLow = (ImageButton) findViewById(R.id.bLowprice);
		if(Filter.lowprice) {
			bPriceLow.setImageResource(R.drawable.lowprice);
		} else {
			bPriceLow.setImageResource(R.drawable.lowpriceb);
		}

		bPriceMedium = (ImageButton) findViewById(R.id.bMediumprice);
		if(Filter.mediumprice) {
			bPriceMedium.setImageResource(R.drawable.mediumprice);
		} else {
			bPriceMedium.setImageResource(R.drawable.mediumpriceb);
		}

		bPriceHigh = (ImageButton) findViewById(R.id.bHighprice);
		if(Filter.highprice) {
			bPriceHigh.setImageResource(R.drawable.highprice);
		} else {
			bPriceHigh.setImageResource(R.drawable.highpriceb);
		}

		eRatingFrom = (EditText) findViewById(R.id.fRatingFrom);
		eRatingFrom.setText(Filter.ratingFrom);

		eRatingTo = (EditText) findViewById(R.id.fRatingTo);
		eRatingTo.setText(Filter.ratingTo);
	}

	/*
	 * A setup for the select restaurant types view.
	 */
	public void restaurantTypesSetup(){
		inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		typesLayout = inflater.inflate(R.layout.filtertypes,null);

		buRestaurantTypes = new AlertDialog.Builder(this)
		.setTitle(R.string.restaurant_types)
		.setMultiChoiceItems(Filter.types, Filter.checkedTypes, new DialogInterface.OnMultiChoiceClickListener() {
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// Nothing
			}
		});

		bSelectAll = (Button) typesLayout.findViewById(R.id.bSelectAll);
		bDeselectAll = (Button) typesLayout.findViewById(R.id.bDeselectAll);

		bSelectAll.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Selected");
				for(int i = 0; i < Filter.checkedTypes.length; i++)
				{
					Filter.checkedTypes[i] = true; // Change in the adapter
					aRestaurantTypes.getListView().setItemChecked(i, true); // To redraw
				}
			}
		});

		bDeselectAll.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Deselected");
				for(int i = 0; i < Filter.types.length; i++)
				{
					Filter.checkedTypes[i] = false; // Change in the adapter
					aRestaurantTypes.getListView().setItemChecked(i, false); // To redraw
				}
			}
		});

		buRestaurantTypes.setView(typesLayout);
		aRestaurantTypes = buRestaurantTypes.create();
	}

	public void listeners(){
		// ------------- Filter Button -----------------
		bFilterTypes = (Button) findViewById(R.id.bFilterTypes);

		View.OnClickListener lFilterTypes = new View.OnClickListener(){
			public void onClick(View v){
				aRestaurantTypes.show();
			}
		} ;

		bFilterTypes.setOnClickListener(lFilterTypes);

		// ------------- Save Button -----------------
		bSaveChanges = (Button) findViewById(R.id.bSave);
		View.OnClickListener lSaveChanges = new View.OnClickListener(){
			public void onClick(View v){
				getFilterInfo(v);
				if(bResult){
					setResult(RESULT_OK);
					finish();
				}
			}
		} ;

		bSaveChanges.setOnClickListener(lSaveChanges);

		// ------------- Radius Bar -----------------
		SeekBar.OnSeekBarChangeListener lSeekBarChanged = new SeekBar.OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					Filter.radius = (int) ( ( (progress + 1)/100.0 ) * 20000.0);
					final EditText radiusText = (EditText) findViewById(R.id.fRadiusEdit);
					radiusText.setText("" + Filter.radius);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				//Nothing
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//Nothing

			}

		} ;
		bSeekBarFilter.setOnSeekBarChangeListener(lSeekBarChanged);

		// ------------- Radius Text -----------------
		TextWatcher lRadiusText = new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Nothing
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() != 0){
					if(Integer.parseInt(s.toString()) <= 20000){
						Filter.radius = Integer.parseInt(s.toString());
					}
					else{
						Filter.radius = 20000;
					}
					final SeekBar bSeekBarFilter = (SeekBar) findViewById(R.id.fRadiusBar);
					bSeekBarFilter.setProgress( ((int)((Filter.radius / 20000.0)*100.0))-1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing			
			}};

			eRadiusText.addTextChangedListener(lRadiusText);

			// ------------- Rating From -----------------
			TextWatcher lRatingFrom = new TextWatcher(){
				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
					// Nothing
				}

				@Override
				public void afterTextChanged(Editable s) {
					if(s.length() != 0){
						if(Double.parseDouble(s.toString()) > 5.0){
							Filter.ratingFrom = "5.0";
							final EditText fRatingFrom = (EditText) findViewById(R.id.fRatingFrom);
							fRatingFrom.setText(Filter.ratingFrom);
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// Nothing			
				}};

				eRatingFrom.addTextChangedListener(lRatingFrom);

				// ------------- Rating To -----------------
				TextWatcher lRatingTo = new TextWatcher(){
					@Override
					public void onTextChanged(CharSequence s, int start, int before,
							int count) {
						// Nothing
					}

					@Override
					public void afterTextChanged(Editable s) {
						if(s.length() != 0){
							if(Double.parseDouble(s.toString()) > 5.0){
								Filter.ratingTo = "5.0";
								final EditText fRatingTo = (EditText) findViewById(R.id.fRatingTo);
								fRatingTo.setText(Filter.ratingTo);
							}
						}
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start, int count,
							int after) {
						// Nothing			
					}};

					eRatingTo.addTextChangedListener(lRatingTo);

					// ------------- Pricing -----------------
					View.OnClickListener lPriceLow = new View.OnClickListener(){
						public void onClick(View v){
							if(Filter.lowprice){
								Filter.lowprice = false;
								bPriceLow.setImageResource(R.drawable.lowpriceb);
							}
							else{
								Filter.lowprice = true;
								bPriceLow.setImageResource(R.drawable.lowprice);
							}
						}
					} ;

					bPriceLow.setOnClickListener(lPriceLow);

					View.OnClickListener lPriceMedium = new View.OnClickListener(){
						public void onClick(View v){
							if(Filter.mediumprice){
								Filter.mediumprice = false;
								bPriceMedium.setImageResource(R.drawable.mediumpriceb);
							}
							else{
								Filter.mediumprice = true;
								bPriceMedium.setImageResource(R.drawable.mediumprice);
							}
						}
					} ;

					bPriceMedium.setOnClickListener(lPriceMedium);

					View.OnClickListener lPriceHigh = new View.OnClickListener(){
						public void onClick(View v){
							if(Filter.highprice){
								Filter.highprice = false;
								bPriceHigh.setImageResource(R.drawable.highpriceb);
							}
							else{
								Filter.highprice = true;
								bPriceHigh.setImageResource(R.drawable.highprice);
							}
						}
					} ;

					bPriceHigh.setOnClickListener(lPriceHigh);

	}

	public void getFilterInfo(View v){
		Filter.ratingFrom = eRatingFrom.getText();
		Filter.ratingTo = eRatingTo.getText();
		Filter.radius = Integer.parseInt(eRadiusText.getEditableText().toString());
	}
}

