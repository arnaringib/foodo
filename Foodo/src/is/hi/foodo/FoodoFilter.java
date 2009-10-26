package is.hi.foodo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class FoodoFilter extends Activity {
	public boolean bResult = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);
		
		setup();
		listeners();
	}
	
	public void setup(){
		final EditText eRadiusText = (EditText) findViewById(R.id.fRadiusEdit);
		eRadiusText.setText("" + Filter.radius);
		
		final SeekBar bSeekBarFilter = (SeekBar) findViewById(R.id.fRadiusBar);
		bSeekBarFilter.setProgress( (int)( ( (double) Filter.radius / 20000.0) * 100.0));
		
		final EditText ePriceFrom = (EditText) findViewById(R.id.fPriceFrom);
		ePriceFrom.setText(Filter.priceFrom);
		
		final EditText ePriceTo = (EditText) findViewById(R.id.fPriceTo);
		ePriceTo.setText(Filter.priceTo);
		
		final EditText eRatingFrom = (EditText) findViewById(R.id.fRatingFrom);
		eRatingFrom.setText(Filter.ratingFrom);
		
		final EditText eRatingTo = (EditText) findViewById(R.id.fRatingTo);
		eRatingTo.setText(Filter.ratingTo);
	}
	
	public void listeners(){
		// ------------- Filter Button -----------------
		final Button bFilterTypes = (Button) findViewById(R.id.bFilterTypes);
		View.OnClickListener lFilterTypes = new View.OnClickListener(){
			public void onClick(View v){			
				AlertDialog.Builder builder = new AlertDialog.Builder(FoodoFilter.this)
					.setTitle("Restaurants types")
					.setMultiChoiceItems(Filter.types, Filter.checkedTypes, new DialogInterface.OnMultiChoiceClickListener() {
					    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					    	//nothing
					    }
					});

				builder.show();
			}
		} ;
		
		bFilterTypes.setOnClickListener(lFilterTypes);
		
		// ------------- Save Button -----------------
		final Button bSaveChanges = (Button) findViewById(R.id.bSave);
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
		final SeekBar bSeekBarFilter = (SeekBar) findViewById(R.id.fRadiusBar);
		SeekBar.OnSeekBarChangeListener lSeekBarChanged = new SeekBar.OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					Filter.radius = (int) ( ( (double) (progress + 1)/100.0 ) * 20000.0);
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
		final EditText eRadiusText = (EditText) findViewById(R.id.fRadiusEdit);
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
					bSeekBarFilter.setProgress( ((int)((double)((double)Filter.radius / 20000.0)*100.0))-1);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// Nothing			
			}};
			
		eRadiusText.addTextChangedListener(lRadiusText);
	}
	
	public void getFilterInfo(View v){
		EditText ratingFrom = (EditText) findViewById(R.id.fRatingFrom);
		EditText ratingTo = (EditText) findViewById(R.id.fRatingTo);
		EditText radiusText = (EditText) findViewById(R.id.fRadiusEdit);
		EditText priceFrom = (EditText) findViewById(R.id.fPriceFrom);
		EditText priceTo = (EditText) findViewById(R.id.fPriceTo);
		
		Filter.ratingFrom = ratingFrom.getText();
		Filter.ratingTo = ratingTo.getText();
		Filter.radius = Integer.parseInt(radiusText.getEditableText().toString());
		Filter.priceFrom = priceFrom.getText();
		Filter.priceTo = priceTo.getText();
	}
}

