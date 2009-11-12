package is.hi.foodo.tests;

import is.hi.foodo.FoodoUserManager;
import is.hi.foodo.UserManager;
import junit.framework.TestCase;

public class FoodoUsersManagerTest extends TestCase {
	
	private FoodoUserManager manager;
	
	public void setUp() {
		manager = new FoodoUserManager();
	}
	
	public void testBadPassword() {
		assertFalse(manager.authenticate("bad@bad.is", "bad"));
		assertFalse(manager.isAuthenticated());
		assertNull(manager.getEmail());
		assertNull(manager.getApiKey());
		assertEquals(UserManager.E_LOGIN, manager.getErrorCode());
	}
	
	
	public void testSignup() {
		String firstName = "Siggi";
		String lastName = "Jons";
		String email = "test4@hi.is";
		
		assertTrue(manager.signup(firstName, lastName, email, "testari"));
		assertTrue(manager.authenticate(email, "testari"));
		assertEquals(email, manager.getEmail());
		assertEquals(firstName, manager.getFirstName());
		assertEquals(lastName, manager.getLastName());
	}
	
	public void testBadSignup() {
		assertFalse(manager.signup("", "", "sij16@hi.is", "blabla"));
	}
	

	public void testAuthenticate() {
		assertTrue(manager.authenticate("sij16@hi.is", "test"));
		assertTrue(manager.isAuthenticated());
		assertEquals("sij16@hi.is", manager.getEmail());
		assertEquals("581bbd1735743510e82b098e776bd8abbc7e31ad", manager.getApiKey());
	}

}