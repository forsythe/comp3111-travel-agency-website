package unit.editors;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import comp3111.Utils;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.PromoEventRepository;
import comp3111.input.editors.PromoEventEditor;

/**
 * @author Forsythe
 *
 */
public class PromoEventEditorTests {
	@InjectMocks
	protected PromoEventEditor promoEditor;

	@Mock
	private BookingRepository brMock;
	@Mock
	private CustomerRepository crMock;
	@Mock
	private OfferingRepository orMock;
	@Mock
	private PromoEventRepository perMock;

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

	}

	@Test
	public void testCatchAllInvalidInputsWhenMakingPromoEvent() {
		promoEditor.getSubwindow(new PromoEvent());
		setEditorFormValues(null, null, "-1", "-2", "", "-1", OVERLY_LONG_STRING);

		promoEditor.getConfirmButton().click();
		assertFalse(promoEditor.getValidationStatus().isOk());
		// There should be 7 errors
		assertEquals(7, promoEditor.getValidationStatus().getFieldValidationErrors().size());

		// also test with a fake DB object (with an id)
		PromoEvent pe = new PromoEvent();
		pe.setId(123L);
		promoEditor.getSubwindow(pe);
		setEditorFormValues(null, null, "-1", "-2", "VAPE420", "-1", OVERLY_LONG_STRING);

		promoEditor.getConfirmButton().click();
		assertFalse(promoEditor.getValidationStatus().isOk());
		// There should be 6
		assertEquals(6, promoEditor.getValidationStatus().getFieldValidationErrors().size());
	}

	@Test
	public void testCatchDuplicatePromoCode() {
		promoEditor.getSubwindow(new PromoEvent());
		Offering o = new Offering();
		o.setStartDate(Utils.addDate(new Date(), 10));
		o.setStatus(Offering.STATUS_PENDING);
		setEditorFormValues(o, LocalDateTime.now(), "1", "2", "VAPE", "10", "Custom message");

		promoEditor.getConfirmButton().click();
		assertTrue(promoEditor.getValidationStatus().isOk());

		// also test with a fake DB object (with an id)
		PromoEvent pe = new PromoEvent();
		pe.setId(123L);
		promoEditor.getSubwindow(pe);
		Offering o2 = new Offering();
		o2.setStartDate(Utils.addDate(new Date(), 10));
		o2.setStatus(Offering.STATUS_PENDING);
		setEditorFormValues(o2, LocalDateTime.now(), "1", "2", "VAPE", "10", "Custom message");

		promoEditor.getConfirmButton().click();
		assertTrue(promoEditor.getValidationStatus().isOk());
	}

	@Test
	public void testCanEditEvent() {
		assertTrue(promoEditor.canEditEvent(null));

		PromoEvent tooLate = new PromoEvent();
		tooLate.setTriggerDate(Utils.addDate(Utils.localDateTimeToDate(LocalDateTime.now()), -10));
		assertFalse(promoEditor.canEditEvent(tooLate));

		PromoEvent editable = new PromoEvent();
		editable.setTriggerDate(Utils.addDate(Utils.localDateTimeToDate(LocalDateTime.now()), 10));
		assertTrue(promoEditor.canEditEvent(editable));
	}

	private void setEditorFormValues(Offering o, LocalDateTime d, String discountMultiplier,
			String maxReservationsPerCustomer, String promoCode, String promoCodeUses, String customMessage) {
		promoEditor.getOffering().setValue(o);
		promoEditor.getTriggerDate().setValue(d);
		promoEditor.getDiscountMultiplier().setValue(discountMultiplier);
		promoEditor.getMaxReservationsPerCustomer().setValue(maxReservationsPerCustomer);
		promoEditor.getPromoCode().setValue(promoCode);
		promoEditor.getPromoCodeUses().setValue(promoCodeUses);
		promoEditor.getCustomMessage().setValue(customMessage);
	}

}
