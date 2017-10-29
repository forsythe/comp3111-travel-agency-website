package comp3111.view;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import comp3111.editors.TourEditor;
import comp3111.editors.TourOfferingEditor;

@SpringView(name = OfferingManagementView.VIEW_NAME)
public class OfferingManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "offeringManagement";
	private static final Logger log = LoggerFactory.getLogger(TourManagementView.class);

	@Autowired
	TourOfferingEditor tourOfferingEditor;
	
	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Tour Offering Management</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();

		// label will only take the space it needs
		layout.addComponent(titleLabel);
		layout.addComponent(tourOfferingEditor);

		this.addComponent(layout);
	}
	
	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
		//everytime we enter this page, we want to update the data in the grid
		this.tourOfferingEditor.refreshData();
	}
}
