package unit.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;
import comp3111.input.editors.CustomerEditor;


public class CustomerEditorTests {
	@InjectMocks
	protected CustomerEditor customerEditor;
	
	@Mock
	CustomerRepository crMocked;
	
	private static final String OVERLY_LONG_STRING = "lllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll";
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() {
		// dbManager.deleteAll();
	}
	
	@Test
	public void testAllInvalidInputsCaughtWhenMakingCustomer () {
		customerEditor.getSubwindow(crMocked, customerEditor.getCustomerCollectionCached(), new Customer());
		
		setEditorFormValues(OVERLY_LONG_STRING, OVERLY_LONG_STRING, "nasufgur", "9999999999", "-200");
		
		customerEditor.getSubwindowConfirmButton().click();
		
		assertFalse(customerEditor.getValidationStatus().isOk());
		assertEquals(5, customerEditor.getValidationStatus().getFieldValidationErrors().size());
	}
	
	@Test
	public void testCanSaveValidCustomer() {
		customerEditor.getSubwindow(crMocked, customerEditor.getCustomerCollectionCached(), new Customer());
		
		setEditorFormValues("Big Shaq", "bigshaq", "A123456(3)", "852-56187379", "12");
		
		customerEditor.getSubwindowConfirmButton().click();
		
		assertTrue(customerEditor.getValidationStatus().isOk());
	}
	
	@Test
	public void testCanLoadSavedCustomerIntoForm() {
		Customer customer1 = new Customer("Ding", "ding", "852-56187379", 12, "A123456(3)");
		long fakeId = 102;
		
		customer1.setId(fakeId);
		customerEditor.getSubwindow(crMocked, customerEditor.getCustomerCollectionCached(), customer1);
		assertEquals("Ding", customerEditor.getCustomerName().getValue());
	}
	
	private void setEditorFormValues(String customerName, String customerLineId,
			String customerHKID, String customerPhone, String age) {
		customerEditor.getCustomerName().setValue(customerName);
		customerEditor.getCustomerAge().setValue(age);
		customerEditor.getCustomerHKID().setValue(customerHKID);
		customerEditor.getCustomerLineId().setValue(customerLineId);
		customerEditor.getCustomerPhone().setValue(customerPhone);
	}

}
