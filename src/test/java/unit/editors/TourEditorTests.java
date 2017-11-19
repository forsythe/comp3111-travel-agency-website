package unit.editors;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vaadin.data.BindingValidationStatus;
import com.vaadin.ui.TextField;

import comp3111.Application;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.Tour;
import comp3111.data.repo.TourRepository;
import comp3111.input.editors.OfferingEditor;
import comp3111.input.editors.TourEditor;

/**
 * Uncomment the RunWith and SpringBootTest to actually use autowired stuff.
 * Otherwise, this is way faster.
 */
// @RunWith(SpringRunner.class)
// @SpringBootTest(classes = Application.class, properties =
// "scheduling.enabled=false")

public class TourEditorTests {

	// @Autowired
	// private TourRepository tourRepo;
	// @Autowired
	// private DBManager dbManager;

	@InjectMocks
	protected TourEditor tourEditor;

	/*
	 * Even though we don't use these objects here, mockito needs them to fill in
	 * the autowired fields in TourEditor!
	 */
	@Mock
	TourRepository trMocked;
	@Mock
	DBManager dbmMocked;
	@Mock
	OfferingEditor ofeMocked;

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
	public void testAllInvalidInputsCaughtWhenMakingRepeatingTour() {
		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), new Tour());

		setEditorFormValues(OVERLY_LONG_STRING, "-5", Tour.REPEATING_TOUR_TYPE,
				Utils.getDaysOfWeek().toArray(new String[7]), null, "1.2", "1.5", "-200", "-400",
				new Boolean(true).toString(), OVERLY_LONG_STRING);

		tourEditor.getSubwindowConfirmButton().click();
		assertFalse(tourEditor.getValidationStatus().isOk());
		assertEquals(7, tourEditor.getValidationStatus().getFieldValidationErrors().size());
	}

	@Test
	public void testAllInvalidInputsCaughtWhenMakingLimitedTour() {
		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), new Tour());

		setEditorFormValues(OVERLY_LONG_STRING, "-5", Tour.LIMITED_TOUR_TYPE, null, "11/11/2017, 11/12/2017", "1.2",
				"1.5", "-200", "-400", new Boolean(true).toString(), OVERLY_LONG_STRING);

		tourEditor.getSubwindowConfirmButton().click();
		assertFalse(tourEditor.getValidationStatus().isOk());
		assertEquals(7, tourEditor.getValidationStatus().getFieldValidationErrors().size());
	}

	@Test
	public void testCanSaveValidLimitedTour() {
		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), new Tour());

		setEditorFormValues("LG7", "5", Tour.LIMITED_TOUR_TYPE, null, "11/11/2017", "1", "1", "200", "400",
				new Boolean(true).toString(), "a description");

		tourEditor.getSubwindowConfirmButton().click();

		assertTrue(tourEditor.getValidationStatus().isOk());
	}

	@Test
	public void testCanSaveValidRepeatingTour() {
		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), new Tour());

		setEditorFormValues("LG7", "5", Tour.REPEATING_TOUR_TYPE, Utils.getDaysOfWeek().toArray(new String[7]), null,
				"0.4", "0.4", "200", "400", new Boolean(true).toString(), "a description");

		tourEditor.getSubwindowConfirmButton().click();

		assertTrue(tourEditor.getValidationStatus().isOk());
	}

	@Test
	public void testCanLoadSavedLimitedTourIntoForm() {
		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDates(
				new HashSet<Date>(Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime())));

		long fakeDbId = 100;
		tour1.setId(fakeDbId);

		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), tour1);
		assertEquals("Yangshan", tourEditor.getTourName().getValue());
	}

	@Test
	public void testCanLoadSavedRepeatingTourIntoForm() {
		Tour tour1 = new Tour("Yangshan", "Many hotsprings", 3, 0.8, 0.0, 599, 699);
		tour1.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3)));

		long fakeDbId = 100;
		tour1.setId(fakeDbId);

		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), tour1);
		assertEquals("Yangshan", tourEditor.getTourName().getValue());
	}

	@Test
	public void testCaughtErrorWhenMissingAvailability() {
		tourEditor.getSubwindow(trMocked, tourEditor.getTourCollectionCached(), new Tour());

		setEditorFormValues("LG7", "5", Tour.LIMITED_TOUR_TYPE, new String[] {}, "", "1", "1", "200", "400",
				new Boolean(true).toString(), "a description");
		tourEditor.getSubwindowConfirmButton().click();
		assertNotNull(tourEditor.getAllowedDates().getComponentError());

		setEditorFormValues("LG7", "5", Tour.REPEATING_TOUR_TYPE, new String[] {}, "", "1", "1", "200", "400",
				new Boolean(true).toString(), "a description");
		tourEditor.getSubwindowConfirmButton().click();
		assertNotNull(tourEditor.getAllowedDaysOfWeek().getComponentError());
	}

	private void setEditorFormValues(String tourName, String days, String tourType, String[] allowedDaysOfWeek,
			String allowedDates, String childDiscount, String toddlerDiscount, String weekdayPrice, String weekendPrice,
			String isChildFriendly, String descrip) {
		tourEditor.getTourName().setValue(tourName);
		tourEditor.getDays().setValue(days);
		tourEditor.getTourType().setValue(tourType);
		if (tourType.equals(Tour.LIMITED_TOUR_TYPE))
			tourEditor.getAllowedDates().setValue(allowedDates);
		else
			tourEditor.getAllowedDaysOfWeek().select(allowedDaysOfWeek);
		tourEditor.getChildDiscount().setValue(childDiscount);
		tourEditor.getToddlerDiscount().setValue(toddlerDiscount);
		tourEditor.getWeekdayPrice().setValue(weekdayPrice);
		tourEditor.getWeekendPrice().setValue(weekendPrice);
		tourEditor.getDescrip().setValue(descrip);
	}

}