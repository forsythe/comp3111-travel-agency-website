package comp3111.view;

import javax.annotation.PostConstruct;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SpringView(name = ThirdView.VIEW_NAME)
public class ThirdView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "third";

	@PostConstruct
	void init() {
		addComponent(new Label("This is the third view"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}
