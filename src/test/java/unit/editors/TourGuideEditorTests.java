package unit.editors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.TourGuideRepository;
import comp3111.input.editors.GuidedByViewer;
import comp3111.input.editors.TourGuidesEditor;

public class TourGuideEditorTests {
	@InjectMocks
	protected TourGuidesEditor tourGuidesEditor;
	
	@Mock
	TourGuideRepository tgrMocked;
	@Mock
	GuidedByViewer gbvMocked;
	
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
	public void testAllInvalidInputsCaughtWhenMakingTourGuide() {
		tourGuidesEditor.getSubwindow(tgrMocked, tourGuidesEditor.getTourGuideCollectionCached(), new TourGuide());
		
		setEditorFormValues(OVERLY_LONG_STRING, OVERLY_LONG_STRING);
		
		tourGuidesEditor.getSubwindowConfirmButton().click();
		assertFalse(tourGuidesEditor.getValidationStatus().isOk());
		assertEquals(2, tourGuidesEditor.getValidationStatus().getFieldValidationErrors().size());
	}
	
	@Test
	public void testCanSaveValidTourGuide() {
		tourGuidesEditor.getSubwindow(tgrMocked, tourGuidesEditor.getTourGuideCollectionCached(), new TourGuide());
		
		setEditorFormValues("Jon Snow", "lordcommander");
		
		tourGuidesEditor.getSubwindowConfirmButton().click();
		assertTrue(tourGuidesEditor.getValidationStatus().isOk());
		
	}
	
	@Test
	public void testCanLoadSavedTourGuideToForm() {
		TourGuide tg1 = new TourGuide("Polly", "polly");
		long fakeId = 101;
		
		tg1.setId(fakeId);
		tourGuidesEditor.getSubwindow(tgrMocked, tourGuidesEditor.getTourGuideCollectionCached(), tg1);
		assertEquals("Polly", tourGuidesEditor.getTourGuideName().getValue());
	}
	
	private void setEditorFormValues(String tourGuideName, String tourGuideLineId) {
		tourGuidesEditor.getTourGuideName().setValue(tourGuideName);
		tourGuidesEditor.getTourGuideLineUsername().setValue(tourGuideLineId);
	}
	
	

}
