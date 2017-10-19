package comp3111.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = CustomerEngagementManagementView.VIEW_NAME)
public class CustomerEngagementManagementView extends VerticalLayout implements View{
	public static final String VIEW_NAME = "customerEngagementManagement";
	private static final Logger log = LoggerFactory.getLogger(CustomerEngagementManagementView.class);
	
	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Customer Engagement Management</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();

		// label will only take the space it needs
		layout.addComponent(titleLabel);

		this.addComponent(layout);
	}
	
	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}
