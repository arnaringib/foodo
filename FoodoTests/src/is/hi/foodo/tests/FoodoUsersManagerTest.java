package is.hi.foodo.tests;

import is.hi.foodo.FoodoUserManager;
import is.hi.foodo.UserManager;
import junit.framework.TestCase;

public class FoodoUsersManagerTest extends TestCase {
	
	private FoodoUserManager manager;
	
	public void setUp() {
		manager = new FoodoUserManager();
	}
	
	public void testAuthenticate() {
		
		assertTrue(manager.authenticate("sij16@hi.is", "a94a8fe5ccb19ba61c4c0873d391e987982fbbd3"));
		assertTrue(manager.isAuthenticated());
		assertEquals("sij16@hi.is", manager.getEmail());
		assertEquals("581bbd1735743510e82b098e776bd8abbc7e31ad", manager.getApiKey());
	}
	
	public void testBadPassword() {
		assertFalse(manager.authenticate("bad@bad.is", "bad"));
		assertFalse(manager.isAuthenticated());
		assertNull(manager.getEmail());
		assertNull(manager.getApiKey());
		assertEquals(UserManager.E_LOGIN, manager.getErrorCode());
	}
	
	
	public void testSignup() {
		String email = "test3@hi.is";
		
		assertTrue(manager.signup(email, "testari"));
		assertTrue(manager.authenticate(email, "00a79b7016e0fb4e0228b31409e10be141a3887f"));
		assertEquals(email, manager.getEmail());
	}
	
	public void testBadSignup() {
		assertFalse(manager.signup("sij16@hi.is", "blabla"));
	}

}