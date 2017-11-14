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
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
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

@SpringView(name = CustomerEngagementView.VIEW_NAME)
public class CustomerEngagementView extends VerticalLayout implements View {
	private static final String BY_ALL_LINE_CUSTOMERS = "All LINE Customers";
	private static final String BY_TOUR = "Tour";
	private static final String BY_OFFERING = "Offering";
	private static final String BY_SINGLE_LINE_CUSTOMER = "Single LINE Customer";

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

	private VerticalLayout getAdvertisingTab() {
		FormLayout layout = new FormLayout();
		final RadioButtonGroup<String> broadcastTarget = new RadioButtonGroup<String>("Recipient");
		ComboBox<Customer> customerBox = new ComboBox<Customer>();
		ComboBox<Offering> offeringBox = new ComboBox<Offering>();
		ComboBox<Tour> tourBox = new ComboBox<Tour>();
		TextArea message = new TextArea("Message");
		Button send = new Button("Send");

		layout.addComponent(broadcastTarget);
		layout.addComponent(customerBox);
		layout.addComponent(offeringBox);
		layout.addComponent(tourBox);
		layout.addComponent(message);
		layout.addComponent(send);

		broadcastTarget.setItems(BY_SINGLE_LINE_CUSTOMER, BY_OFFERING, BY_TOUR, BY_ALL_LINE_CUSTOMERS);
		broadcastTarget.setSelectedItem(BY_SINGLE_LINE_CUSTOMER);

		customerBox.setVisible(true);
		offeringBox.setVisible(false);
		tourBox.setVisible(false);

		customerBox.setItems(Utils.iterableToCollection(//
				cRepo.findAll()).stream().//
				filter(c -> !c.getLineId().isEmpty()).//
				sorted((c1, c2) -> c1.getId().compareTo(c2.getId())));//
		// only want LINE id enabled customers

		offeringBox.setItems(Utils.iterableToCollection(oRepo.findAll()).stream()
				.sorted((o1, o2) -> o1.getId().compareTo(o2.getId())));
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

		send.addClickListener(event -> {
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

			NotificationFactory.getTopBarNotification("Message delivery " + (status ? " succeeded!" : " failed!"),
					LineMessenger.getCounter() + " recepient(s)", 5).show(Page.getCurrent());

		});
		VerticalLayout container = new VerticalLayout();
		container.addComponent(layout);
		return container;
	}

	/**
	 * @return
	 */
	private VerticalLayout getQueryTab() {
		VerticalLayout layout = new VerticalLayout();

		Grid<NonFAQQuery> grid = new Grid<NonFAQQuery>(NonFAQQuery.class);
		TextArea replyBox = new TextArea("Response");
		Button submit = new Button("Send");

		layout.addComponent(grid);
		layout.addComponent(replyBox);
		layout.addComponent(submit);

		submit.setEnabled(false);

		grid.setDataProvider(new ListDataProvider<NonFAQQuery>(Utils.iterableToCollection(qRepo.findAll())));
		grid.removeColumn(GridCol.NONFAQQUERY_CUSTOMER_NAME);
		grid.setColumnOrder(GridCol.NONFAQQUERY_ID, GridCol.NONFAQQUERY_CUSTOMER, GridCol.NONFAQQUERY_QUERY,
				GridCol.NONFAQQUERY_ANSWER);
		grid.setHeight("90%");
		log.info("there are [{}] unresolved queries",
				Utils.iterableToCollection(qRepo.findAll()).stream().filter(q -> q.getAnswer().isEmpty()).count());

		grid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedQuery = event.getFirstSelectedItem().get();
				submit.setEnabled(true);
			} else {
				selectedQuery = null;
				submit.setEnabled(false);
			}
		});

		HeaderRow filterRow = grid.appendHeaderRow();

		for (Column<NonFAQQuery, ?> col : grid.getColumns()) {
			col.setMinimumWidth(160);
			col.setHidable(true);
			col.setExpandRatio(1);
			col.setHidingToggleCaption(col.getCaption());
			HeaderCell cell = filterRow.getCell(col.getId());

			// Have an input field to use for filter
			TextField filterField = new TextField();
			filterField.setWidth(130, Unit.PIXELS);
			filterField.setHeight(30, Unit.PIXELS);

			filterField.addValueChangeListener(change -> {
				String searchVal = change.getValue();
				String colId = col.getId();

				log.info("Value change in col [{}], val=[{}]", colId, searchVal);
				ListDataProvider<NonFAQQuery> dataProvider = (ListDataProvider<NonFAQQuery>) grid.getDataProvider();

				if (!filterField.isEmpty()) {
					try {
						// note: if we keep typing into same textfield, we will overwrite the old filter
						// for this column, which is desirable (rather than having filters for "h",
						// "he", "hel", etc
						gridFilters.put(colId, FilterFactory.getFilterForNonFAQQuery(colId, searchVal));
						log.info("updated filter on attribute [{}]", colId);

					} catch (Exception e) {
						log.info("ignoring val=[{}], col=[{}] is invalid", searchVal, colId);
					}
				} else {
					// the filter field was empty, so try
					// removing the filter associated with this col
					gridFilters.remove(colId);
					log.info("removed filter on attribute [{}]", colId);

				}
				dataProvider.clearFilters();
				for (String colFilter : gridFilters.keySet()) {
					ProviderAndPredicate paf = gridFilters.get(colFilter);
					dataProvider.addFilter(paf.provider, paf.predicate);
				}
				dataProvider.refreshAll();
			});
			cell.setComponent(filterField);

		}

		grid.setWidth("100%");
		// TODO: check that the lengths of query and answer are <=255

		submit.addClickListener(event -> {
			if (!replyBox.isEmpty()) {
				LineMessenger.resetCounter();
				boolean status = false;
				if (selectedQuery.getCustomer() != null) {
					status = lineMessenger.respondToQuery(selectedQuery.getCustomer().getLineId(),
							selectedQuery.getQuery(), replyBox.getValue());
				}
				NotificationFactory.getTopBarNotification("Message delivery " + (status ? " succeeded!" : " failed!"),
						LineMessenger.getCounter() + " recepient(s)", 5).show(Page.getCurrent());

				if (status) {
					selectedQuery.setAnswer(replyBox.getValue());
					qRepo.save(selectedQuery);
					grid.setDataProvider(
							new ListDataProvider<NonFAQQuery>(Utils.iterableToCollection(qRepo.findAll())));
				}
			}
		});

		return layout;

	}

	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}

}
