package is.hi.foodo.tests;
/* SimpleUserManager was killed
 
import is.hi.foodo.SimpleUserManager;
import is.hi.foodo.UserManager;
import junit.framework.TestCase;

public class SimpleUserManagerTest extends TestCase {
	
	private SimpleUserManager manager;
	
	public void setUp() {
		manager = new SimpleUserManager();
	}
	
	public void testAuthenticate() {
		
		assertTrue(manager.authenticate("test@test.is", "test"));
		assertTrue(manager.isAuthenticated());
		assertEquals("test@test.is", manager.getEmail());
		assertEquals("apikey", manager.getApiKey());
	}
	
	public void testBadPassword() {
		assertFalse(manager.authenticate("bad@bad.is", "bad"));
		assertFalse(manager.isAuthenticated());
		assertNull(manager.getEmail());
		assertNull(manager.getApiKey());
		assertEquals(UserManager.E_LOGIN, manager.getErrorCode());
	}
	
	public void testSignup() {
		assertTrue(manager.signup("sij16@hi.is", "testari"));
		assertTrue(manager.authenticate("sij16@hi.is", "testari"));
		assertEquals("sij16@hi.is", manager.getEmail());
	}
	
	public void testBadSignup() {
		assertFalse(manager.signup("test@test.is", "blabla"));
	}

}
*/