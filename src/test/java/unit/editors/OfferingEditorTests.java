package unit.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;
import comp3111.input.editors.OfferingEditor;
import comp3111.input.editors.TourEditor;

public class OfferingEditorTests {
	@InjectMocks
	protected OfferingEditor offeringEditor;
	
	@Mock
	OfferingRepository or;
	@Mock
	DBManager dbmMocked;
	@Mock
	TourEditor teMocked;
	@Mock
	TourRepository tr;
	@Mock
	TourGuideRepository tgr;
	
	private static final String OVERLY_LONG_STRING = "lllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll"
			+ "lllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll";
	private Date now;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		now = new Date();
	}
	
	@After
	public void tearDown() {
		// dbManager.deleteAll();
	}
	
	@Test
	@Ignore("buggy for now")
	public void testCanSaveValidOffering() {
		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
		offeringEditor.setSelectedTour(shimenTour);
		
		TourGuide tg1 = new TourGuide("Arthur", "arthur");
		
		Offering newOff = new Offering();
		newOff.setStartDate(Utils.addDate(now, 10));
		
		offeringEditor.getSubWindow(offeringEditor.getSelectedTour(), new Offering(), teMocked);
		
		setEditorFormValues(tg1, LocalDate.of(2017, 12, 21), "Shimen Forest Hotel", "1", "10");
		offeringEditor.getSubwindowConfirmButton().click();
		
		assertTrue(offeringEditor.getValidationStatus().isOk());	
	}
	
	@Test
	public void testAllInvalidInputsCaughtWhenMakingOffering() {
		Tour shimenTour = new Tour("Shimen Forest", "Color ponds...", 2, 0.8, 0, 499, 599);
		shimenTour.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
		offeringEditor.setSelectedTour(shimenTour);
		
		TourGuide tg1 = new TourGuide("Arthur", "arthur");
		
		offeringEditor.getSubWindow(offeringEditor.getSelectedTour(), new Offering(), teMocked);
		
		setEditorFormValues(tg1, LocalDate.of(2015, 12, 10), OVERLY_LONG_STRING, "-10", "-999999");
		offeringEditor.getSubwindowConfirmButton().click();
		
		assertFalse(offeringEditor.getValidationStatus().isOk());	
		assertEquals(5, offeringEditor.getValidationStatus().getFieldValidationErrors().size());
	}
	
	private void setEditorFormValues(TourGuide tourGuide, LocalDate startDate, String hotelName, String minCustomers,
			String maxCustomers) {
		offeringEditor.getTourGuide().setSelectedItem(tourGuide);
		offeringEditor.getStartDate().setValue(startDate);
		offeringEditor.getHotelName().setValue(hotelName);
		offeringEditor.getMinCustomer().setValue(minCustomers);
		offeringEditor.getMaxCustomer().setValue(maxCustomers);
	}
	
	
}
