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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

import comp3111.LineMessenger;
import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;

@SpringView(name = CustomerEngagementView.VIEW_NAME)
public class CustomerEngagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "customerEngagement";
	private static final Logger log = LoggerFactory.getLogger(CustomerEngagementView.class);

	@Autowired
	private LineMessenger lineMessenger;

	@Autowired
	private CustomerRepository customerRepo;

	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Customer Engagement</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();

		// label will only take the space it needs
		layout.addComponent(titleLabel);
		this.addComponent(layout);

		TabSheet tabsheet = new TabSheet();
		layout.addComponent(tabsheet);

		// Create the first tab
		VerticalLayout advertisingTab = new VerticalLayout();
		advertisingTab.addComponent(new Label("put a form here which sends messages out"));
		tabsheet.addTab(advertisingTab, "Broadcast a message");

		Button send = new Button("Send");
		advertisingTab.addComponent(send);
		send.addClickListener(event -> {
			Customer heng = customerRepo.findOneByName("Heng");
			boolean status = lineMessenger.sendText(heng, "hi this is vaadin speaking");
			Notification.show("message status: " + (status ? "success" : "failure"));
		});

		VerticalLayout queryTab = new VerticalLayout();
		queryTab.addComponent(new Label("put a grid here which shows unanswered queries"));
		tabsheet.addTab(queryTab, "Reply to queries");
	}

	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}
