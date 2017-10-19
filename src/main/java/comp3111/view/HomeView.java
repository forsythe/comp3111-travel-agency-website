package comp3111.view;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = HomeView.VIEW_NAME)
@UIScope
public class HomeView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "";

	@PostConstruct
	void init() {
		addComponent(new Label("This is the home view"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}