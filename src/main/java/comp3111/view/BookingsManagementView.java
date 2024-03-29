package comp3111.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import comp3111.input.editors.BookingEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Generates the UI elements for the front-end side of the Bookings Management page. 
 * @author kristiansuhartono
 *
 */
@SpringView(name = BookingsManagementView.VIEW_NAME)
public class BookingsManagementView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "bookingManagement";
	private static final Logger log = LoggerFactory.getLogger(BookingsManagementView.class);
	
	@Autowired
	BookingEditor bookingEditor;
	
	@PostConstruct
	void init() {
		Label titleLabel = new Label("<h1>Booking Management</h1>", ContentMode.HTML);
		
		VerticalLayout layout = new VerticalLayout();
		
		layout.addComponent(titleLabel);
		layout.addComponent(bookingEditor);
		
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
		bookingEditor.refreshData();
	}
}
