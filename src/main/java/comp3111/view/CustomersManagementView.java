package comp3111.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import comp3111.input.editors.CustomerEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = CustomersManagementView.VIEW_NAME)
public class CustomersManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "customersManagement";
	private static final Logger log = LoggerFactory.getLogger(CustomersManagementView.class);

	@Autowired
	CustomerEditor customerEditor;

	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Customers Management</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();

		// label will only take the space it needs
		layout.addComponent(titleLabel);
		layout.addComponent(customerEditor);

		this.addComponent(layout);
	}

	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
		customerEditor.refreshData();
	}
}
