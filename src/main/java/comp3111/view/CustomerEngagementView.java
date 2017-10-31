package comp3111.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;

import comp3111.LineMessenger;
import comp3111.Utils;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;

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

	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Customer Engagement</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();
		layout.setHeightUndefined();

		// label will only take the space it needs
		layout.addComponent(titleLabel);

		TabSheet tabsheet = new TabSheet();
		VerticalLayout tab1 = getAdvertisingTab();
		VerticalLayout tab2 = getQueryTab();

		tabsheet.addTab(tab1, "Broadcast a message");
		tabsheet.addTab(tab2, "Reply to queries");

		tab1.setHeight("100%");
		tab2.setHeight("100%");

		layout.addComponent(tabsheet);
		this.addComponent(layout);
	}

	private VerticalLayout getAdvertisingTab() {
		VerticalLayout layout = new VerticalLayout();
		layout.setHeight("100%");

		final RadioButtonGroup<String> broadcastTarget = new RadioButtonGroup<String>("Recipient");
		ComboBox<Customer> customerBox = new ComboBox<Customer>(BY_SINGLE_LINE_CUSTOMER);
		ComboBox<Offering> offeringBox = new ComboBox<Offering>(BY_OFFERING);
		ComboBox<Tour> tourBox = new ComboBox<Tour>(BY_TOUR);
		TextArea message = new TextArea("Message");
		Button send = new Button("Send");

		layout.addComponent(broadcastTarget);
		layout.addComponent(customerBox);
		layout.addComponent(offeringBox);
		layout.addComponent(tourBox);
		layout.addComponent(message);
		layout.addComponent(send);

		broadcastTarget.setItems(BY_SINGLE_LINE_CUSTOMER, BY_OFFERING, BY_TOUR, BY_ALL_LINE_CUSTOMERS);

		layout.addComponent(send);
		customerBox.setVisible(false);
		offeringBox.setVisible(false);
		tourBox.setVisible(false);

		customerBox.setItems(Utils.iterableToCollection(//
				cRepo.findAll()).stream().//
				filter(c -> !c.getLineId().isEmpty()));//
		// only want LINE id enabled customers

		offeringBox.setItems(Utils.iterableToCollection(oRepo.findAll()));
		tourBox.setItems(Utils.iterableToCollection(tRepo.findAll()));

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
			switch (broadcastTarget.getValue()) {
			case BY_SINGLE_LINE_CUSTOMER:
				if (customerBox.isEmpty())
					return;
				status = lineMessenger.sendToUser(customerBox.getValue().getLineId(), message.getValue());
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
			Notification.show("Message delivery " + (status ? "succeeded!" : "failed!"),
					lineMessenger.getClass() + " recipient(s).", Notification.TYPE_HUMANIZED_MESSAGE);

		});
		return layout;
	}

	private VerticalLayout getQueryTab() {
		VerticalLayout layout = new VerticalLayout();
		layout.addComponent(new Label("put a grid here which shows unanswered queries"));

		return layout;
	}

	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}
