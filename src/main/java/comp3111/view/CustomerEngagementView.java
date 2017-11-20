package comp3111.view;

import java.util.HashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.LineMessenger;
import comp3111.Utils;
import comp3111.data.GridCol;
import comp3111.data.model.Customer;
import comp3111.data.model.NonFAQQuery;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;
import comp3111.input.editors.FilterFactory;
import comp3111.input.editors.ProviderAndPredicate;

/**
 * Generates the UI elements for the front-end side of the Customer Engagement
 * page.
 * 
 * @author kristiansuhartono
 *
 */
@SpringView(name = CustomerEngagementView.VIEW_NAME)
public class CustomerEngagementView extends VerticalLayout implements View {
	public static final String BY_ALL_LINE_CUSTOMERS = "All LINE Customers";
	public static final String BY_TOUR = "Tour";
	public static final String BY_OFFERING = "Offering";
	public static final String BY_SINGLE_LINE_CUSTOMER = "Single LINE Customer";

	public static final String VIEW_NAME = "customerEngagement";
	private static final Logger log = LoggerFactory.getLogger(CustomerEngagementView.class);

	@Autowired
	private LineMessenger lineMessenger;
	@Autowired
	private CustomerRepository cRepo;
	@Autowired
	private OfferingRepository oRepo;
	@Autowired
	private TourRepository tRepo;
	@Autowired
	private NonFAQQueryRepository qRepo;

	private NonFAQQuery selectedQuery;

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	// the advertising tab fields
	private RadioButtonGroup<String> broadcastTarget;
	private ComboBox<Customer> customerBox;
	private ComboBox<Offering> offeringBox;
	private ComboBox<Tour> tourBox;
	private TextArea message;
	private Button sendButton;

	// the query tab fields
	private Grid<NonFAQQuery> nonfaqGrid;
	private TextArea replyBox;
	private Button submitAnswerButton;

	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Customer Engagement</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();
		layout.setSizeFull();

		// label will only take the space it needs
		layout.addComponent(titleLabel);

		TabSheet tabsheet = new TabSheet();
		VerticalLayout tab1 = getAdvertisingTab();
		VerticalLayout tab2 = getQueryTab();

		tabsheet.addTab(tab1, "Broadcast a message");
		tabsheet.addTab(tab2, "Reply to queries");

		layout.addComponent(tabsheet);
		this.addComponent(layout);
	}

	/**
	 * @return The advertising tab in the CustomerEngagementView
	 */
	public VerticalLayout getAdvertisingTab() {
		FormLayout layout = new FormLayout();

		broadcastTarget = new RadioButtonGroup<String>("Recipient");
		customerBox = new ComboBox<Customer>();
		offeringBox = new ComboBox<Offering>();
		tourBox = new ComboBox<Tour>();

		message = new TextArea("Message");
		sendButton = new Button("Send");

		layout.addComponent(broadcastTarget);
		layout.addComponent(customerBox);
		layout.addComponent(offeringBox);
		layout.addComponent(tourBox);
		layout.addComponent(message);
		layout.addComponent(sendButton);

		broadcastTarget.setItems(BY_SINGLE_LINE_CUSTOMER, BY_OFFERING, BY_TOUR, BY_ALL_LINE_CUSTOMERS);
		broadcastTarget.setSelectedItem(BY_SINGLE_LINE_CUSTOMER);

		customerBox.setVisible(true);
		offeringBox.setVisible(false);
		tourBox.setVisible(false);

		if (cRepo.findAll() != null)// mockito
			customerBox.setItems(Utils.iterableToCollection(//
					cRepo.findAll()).stream().//
					filter(c -> !c.getLineId().isEmpty()).//
					sorted((c1, c2) -> c1.getId().compareTo(c2.getId())));//
		// only want LINE id enabled customers

		if (oRepo.findAll() != null)// mockito
			offeringBox.setItems(Utils.iterableToCollection(oRepo.findAll()).stream()
					.sorted((o1, o2) -> o1.getId().compareTo(o2.getId())));

		if (tRepo.findAll() != null)// mockito
			tourBox.setItems(Utils.iterableToCollection(tRepo.findAll()).stream()
					.sorted((t1, t2) -> t1.getId().compareTo(t2.getId())));

		customerBox.setPopupWidth(null);
		offeringBox.setPopupWidth(null);
		tourBox.setPopupWidth(null);

		broadcastTarget.addValueChangeListener(event -> {
			switch (event.getValue()) {
			case BY_SINGLE_LINE_CUSTOMER:
				customerBox.setVisible(true);
				offeringBox.setVisible(false);
				tourBox.setVisible(false);
				break;
			case BY_OFFERING:
				customerBox.setVisible(false);
				offeringBox.setVisible(true);
				tourBox.setVisible(false);
				break;
			case BY_TOUR:
				customerBox.setVisible(false);
				offeringBox.setVisible(false);
				tourBox.setVisible(true);
				break;
			case BY_ALL_LINE_CUSTOMERS:
				customerBox.setVisible(false);
				offeringBox.setVisible(false);
				tourBox.setVisible(false);
				break;
			}
		});

		sendButton.addClickListener(event -> {

			if (message.isEmpty())
				return;
			boolean status = false;
			LineMessenger.resetCounter();

			switch (broadcastTarget.getValue()) {

			case BY_SINGLE_LINE_CUSTOMER:
				if (customerBox.isEmpty())
					return;
				status = lineMessenger.sendToUser(customerBox.getValue().getLineId(), message.getValue(), true);
				break;
			case BY_OFFERING:
				if (offeringBox.isEmpty())
					return;
				status = lineMessenger.sendToOffering(offeringBox.getValue(), message.getValue());
				break;
			case BY_TOUR:
				if (tourBox.isEmpty())
					return;
				status = lineMessenger.sendToTour(tourBox.getValue(), message.getValue());
				break;
			case BY_ALL_LINE_CUSTOMERS:
				status = lineMessenger.sendToAll(message.getValue());
				break;
			}

			if (Page.getCurrent() != null)// mockito
				NotificationFactory
						.getTopBarWarningNotification("Message delivery " + (status ? " succeeded!" : " failed!"),
								LineMessenger.getCounter() + " recepient(s)", 5)
						.show(Page.getCurrent());

		});
		VerticalLayout container = new VerticalLayout();
		container.addComponent(layout);
		return container;
	}

	/**
	 * @return the broadcastTarget field
	 */
	public RadioButtonGroup<String> getBroadcastTarget() {
		return broadcastTarget;
	}

	/**
	 * @return the customerBox field
	 */
	public ComboBox<Customer> getCustomerBox() {
		return customerBox;
	}

	/**
	 * @return the offeringBox field
	 */
	public ComboBox<Offering> getOfferingBox() {
		return offeringBox;
	}

	/**
	 * @return the tourBox field
	 */
	public ComboBox<Tour> getTourBox() {
		return tourBox;
	}

	/**
	 * @return the sendButton field
	 */
	public Button getSendButton() {
		return sendButton;
	}

	/**
	 * @return the message
	 */
	public TextArea getMessage() {
		return message;
	}

	/**
	 * @return The query tab, holding the nonfaqqueries
	 */
	public VerticalLayout getQueryTab() {
		VerticalLayout layout = new VerticalLayout();

		nonfaqGrid = new Grid<NonFAQQuery>(NonFAQQuery.class);
		replyBox = new TextArea("Response");
		submitAnswerButton = new Button("Send");

		layout.addComponent(nonfaqGrid);
		layout.addComponent(replyBox);
		layout.addComponent(submitAnswerButton);

		submitAnswerButton.setEnabled(false);

		if (qRepo.findAll() != null)// mockito
			nonfaqGrid.setDataProvider(new ListDataProvider<NonFAQQuery>(Utils.iterableToCollection(qRepo.findAll())));
		nonfaqGrid.removeColumn(GridCol.NONFAQQUERY_CUSTOMER_NAME);
		nonfaqGrid.setColumnOrder(GridCol.NONFAQQUERY_ID, GridCol.NONFAQQUERY_CUSTOMER, GridCol.NONFAQQUERY_QUERY,
				GridCol.NONFAQQUERY_ANSWER);
		nonfaqGrid.setHeight("90%");
		if (qRepo.findAll() != null)// mockito
			log.info("there are [{}] unresolved queries",
					Utils.iterableToCollection(qRepo.findAll()).stream().filter(q -> q.getAnswer().isEmpty()).count());

		nonfaqGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedQuery = event.getFirstSelectedItem().get();
				submitAnswerButton.setEnabled(true);
			} else {
				selectedQuery = null;
				submitAnswerButton.setEnabled(false);
			}
		});


		FilterFactory.addFilterInputBoxesToGridHeaderRow(NonFAQQuery.class, nonfaqGrid, gridFilters);

		

		nonfaqGrid.setWidth("100%");
		// TODO: check that the lengths of query and answer are <=255

		submitAnswerButton.addClickListener(event -> {
			if (!replyBox.isEmpty()) {
				LineMessenger.resetCounter();
				boolean status = false;
				if (selectedQuery.getCustomer() != null) {
					status = lineMessenger.respondToQuery(selectedQuery.getCustomer().getLineId(),
							selectedQuery.getQuery(), replyBox.getValue());
				}
				if (Page.getCurrent() != null)// mockito
					NotificationFactory
							.getTopBarWarningNotification("Message delivery " + (status ? " succeeded!" : " failed!"),
									LineMessenger.getCounter() + " recepient(s)", 5)
							.show(Page.getCurrent());

				if (status) {
					selectedQuery.setAnswer(replyBox.getValue());
					qRepo.save(selectedQuery);
					if (qRepo.findAll() != null) // mockito
						nonfaqGrid.setDataProvider(
								new ListDataProvider<NonFAQQuery>(Utils.iterableToCollection(qRepo.findAll())));
				}
			}
		});

		return layout;

	}

	/**
	 * @return the NonFAQQuery nonfaqGrid
	 */
	public Grid<NonFAQQuery> getGrid() {
		return nonfaqGrid;
	}

	/**
	 * @return the replyBox field
	 */
	public TextArea getReplyBox() {
		return replyBox;
	}

	/**
	 * @return the submitAnswerButton button
	 */
	public Button getSubmitAnswerButton() {
		return submitAnswerButton;
	}

	/**
	 * Function is called when the view is loaded up in the browser, refreshes the
	 * data so that the tables are updated to the newest data contents.
	 * 
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}

}
