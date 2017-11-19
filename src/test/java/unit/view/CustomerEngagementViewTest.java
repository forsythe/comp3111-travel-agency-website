/**
 * 
 */
package unit.view;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.vaadin.data.provider.ListDataProvider;

import comp3111.LineMessenger;
import comp3111.data.model.Customer;
import comp3111.data.model.NonFAQQuery;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;
import comp3111.view.CustomerEngagementView;

/**
 * @author Forsythe
 *
 */
public class CustomerEngagementViewTest {
	@InjectMocks
	private CustomerEngagementView cev;

	// it's a spy, not a mockito object; mockito objects cant mock static functions,
	// so "spy" makes a real one!
	@Spy
	private LineMessenger lineMessenger;
	@Mock
	private CustomerRepository cRepoMock;
	@Mock
	private OfferingRepository oRepoMock;
	@Mock
	private TourRepository tRepoMock;
	@Mock
	private NonFAQQueryRepository qRepoMock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCorrectSelectionBoxBasedOnAudience() {

		cev.getAdvertisingTab();

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_OFFERING);
		assertFalse(cev.getCustomerBox().isVisible());
		assertTrue(cev.getOfferingBox().isVisible());
		assertFalse(cev.getTourBox().isVisible());

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_SINGLE_LINE_CUSTOMER);
		assertTrue(cev.getCustomerBox().isVisible());
		assertFalse(cev.getOfferingBox().isVisible());
		assertFalse(cev.getTourBox().isVisible());

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_TOUR);
		assertFalse(cev.getCustomerBox().isVisible());
		assertFalse(cev.getOfferingBox().isVisible());
		assertTrue(cev.getTourBox().isVisible());

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_ALL_LINE_CUSTOMERS);
		assertFalse(cev.getCustomerBox().isVisible());
		assertFalse(cev.getOfferingBox().isVisible());
		assertFalse(cev.getTourBox().isVisible());

	}

	@Test
	public void testCorrectRecipient() {

		cev.getAdvertisingTab();
		cev.getSendButton().click();
		cev.getMessage().setValue("test");

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_OFFERING);
		cev.getSendButton().click();
		cev.getOfferingBox().setValue(new Offering());
		cev.getSendButton().click();

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_SINGLE_LINE_CUSTOMER);
		cev.getSendButton().click();
		cev.getCustomerBox().setValue(new Customer());
		cev.getSendButton().click();

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_TOUR);
		cev.getSendButton().click();
		cev.getTourBox().setValue(new Tour());
		cev.getSendButton().click();

		cev.getBroadcastTarget().setValue(CustomerEngagementView.BY_ALL_LINE_CUSTOMERS);
		cev.getSendButton().click();

	}

	@Test
	public void testQueryTab() {
		cev.getQueryTab();

		Collection<NonFAQQuery> fakeData = new HashSet<NonFAQQuery>();
		NonFAQQuery x = new NonFAQQuery();
		x.setQuery("what is spaghet");
		x.setAnswer("the mockito is spaghet");
		Customer heng = new Customer("heng", "Uc8f613f85e41d93ed9ffa228188466d1", "12331233", 20, "A1234561");
		x.setCustomer(heng);
		fakeData.add(x);
		x.setId(123L);
		cev.getGrid().setDataProvider(new ListDataProvider<NonFAQQuery>(fakeData));

		assertFalse(cev.getSubmitAnswerButton().isEnabled());
		cev.getGrid().select(x);
		assertTrue(cev.getSubmitAnswerButton().isEnabled());

		cev.getGrid().deselectAll();
		cev.getSubmitAnswerButton().click();

		cev.getGrid().select(x);
		cev.getReplyBox().setValue("Mockito test");
		cev.getSubmitAnswerButton().click();
		assertEquals(1, LineMessenger.getCounter());

	}

}
