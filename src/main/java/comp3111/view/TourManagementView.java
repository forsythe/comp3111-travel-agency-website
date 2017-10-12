package comp3111.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import comp3111.presenter.TourEditor;

@SpringView(name = TourManagementView.VIEW_NAME)
public class TourManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "tourManagement";

	@Autowired
	TourEditor tourEditor;
	
	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Tour Management</h1>", ContentMode.HTML);

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();

		// label will only take the space it needs
		layout.addComponent(titleLabel);
		layout.addComponent(tourEditor);

		this.addComponent(layout);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}
