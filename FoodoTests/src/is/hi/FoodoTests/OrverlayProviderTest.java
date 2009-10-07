package is.hi.FoodoTests;

import is.hi.foodo.OverlayProvider;
import is.hi.foodo.SimpleOverlayProvider;

import java.util.ArrayList;

import junit.framework.TestCase;

import com.google.android.maps.OverlayItem;

public class OrverlayProviderTest extends TestCase {
	
	public void testSimpleProvider() {
		
		OverlayProvider op;
		OverlayItem item;
		ArrayList<OverlayItem> list;
		
		op = new SimpleOverlayProvider();
		list = op.getAllOverlays();
		
		assertTrue(list.size() > 0);
		
		item = list.get(0);
		assertEquals(item.getTitle(), "Burger Joint");
		assertEquals(item.getPoint().getLatitudeE6(), 64139603);
		assertEquals(item.getPoint().getLongitudeE6(), -21955812);
		
		item = list.get(1);
		assertEquals(item.getTitle(), "Pizza Joint");
		assertEquals(item.getPoint().getLatitudeE6(), 64135603);
		assertEquals(item.getPoint().getLongitudeE6(), -21954812);
	}

}
