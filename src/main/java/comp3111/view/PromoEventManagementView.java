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

import comp3111.input.editors.PromoEventEditor;

/**
 * Generates the UI elements for the front-end side of the Promotion Management page.
 * @author kristiansuhartono
 *
 */
@SpringView(name = PromoEventManagementView.VIEW_NAME)
public class PromoEventManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "promoEventManagement";
	private static final Logger log = LoggerFactory.getLogger(PromoEventManagementView.class);

	@Autowired
	PromoEventEditor promoEventEditor;

	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Promotion Management</h1>", ContentMode.HTML);

		VerticalLayout layout = new VerticalLayout();

		layout.addComponent(titleLabel);
		layout.addComponent(promoEventEditor);

		this.addComponent(layout);
	}

	/** 
	 * Function is called when the view is loaded up in the browser, refreshes the data so that the tables
	 * are updated to the newest data contents.
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
		promoEventEditor.refreshData();
	}
}
