package is.hi.foodo.tests;

import is.hi.foodo.FoodoDetails;
import is.hi.foodo.R;
import is.hi.foodo.RestaurantDbAdapter;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.widget.TextView;

public class DetailsActivityTest extends ActivityUnitTestCase<FoodoDetails>{
	
	private static final Long TARGET_ID = new Long(1);
	
	private TextView mNameText;
	
	public DetailsActivityTest() {
		super(FoodoDetails.class);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.putExtra(RestaurantDbAdapter.KEY_ROWID, TARGET_ID);
		startActivity(i, null, null);
		
		mNameText = (TextView) getActivity().findViewById(R.id.ReName);
	}
	
	public void testTitle() {
		assertEquals(mNameText.getText(), "Argent’na");
	}

}
