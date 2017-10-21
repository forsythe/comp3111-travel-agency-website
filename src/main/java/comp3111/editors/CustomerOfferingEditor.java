package comp3111.editors;

import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Grid.SelectionMode;

import comp3111.model.CustomerOffering;
import comp3111.repo.CustomerOfferingRepository;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerOfferingEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(CustomerOfferingEditor.class);
	
	/* Action buttons */
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createBookingButton = new Button("Create new booking");
	private Button editBookingButton = new Button("Edit booking");
	
	Grid<CustomerOffering> customerOfferingGrid = new Grid<CustomerOffering>(CustomerOffering.class);
	
	CustomerOffering selectedCustomerOffering;
	
	
	@SuppressWarnings("unchecked")
	@Autowired
	public CustomerOfferingEditor(CustomerOfferingRepository customerOfferingRepo) {
		rowOfButtons.addComponent(createBookingButton);
		rowOfButtons.addComponent(editBookingButton);
		
		// disable edit
		editBookingButton.setEnabled(false);
		
		//add component to view
		this.addComponent(rowOfButtons);
		
		//get from DB
		Iterable<CustomerOffering> customerOfferings = customerOfferingRepo.findAll();
		Collection<CustomerOffering> customerOfferingsCached = new HashSet<CustomerOffering>();
		customerOfferings.forEach(customerOfferingsCached::add);
		customerOfferingGrid.setItems(customerOfferingsCached);

		customerOfferingGrid.setWidth("100%");
		customerOfferingGrid.setSelectionMode(SelectionMode.SINGLE);
		
		customerOfferingGrid.addSelectionListener(new SelectionListener<CustomerOffering>() {
			@Override
			public void selectionChange(SelectionEvent event) {
				Collection<CustomerOffering> selectedItems = customerOfferingGrid.getSelectionModel().getSelectedItems();
				selectedCustomerOffering = null;
				for (CustomerOffering rc : selectedItems) { // easy way to get first element of set
					selectedCustomerOffering = rc;
					break;
				}
				if (selectedCustomerOffering != null) {
					editBookingButton.setEnabled(true);
					createBookingButton.setEnabled(false);
				} else {
					editBookingButton.setEnabled(false);
					createBookingButton.setEnabled(true);
				}
			}
		});
		
		customerOfferingGrid.removeColumn("new"); // hibernate attributes, we don't care about it
		customerOfferingGrid.removeColumn("numChildren");
		customerOfferingGrid.removeColumn("numAdults");
		customerOfferingGrid.removeColumn("numToddlers");
		customerOfferingGrid.removeColumn("customer");
		customerOfferingGrid.removeColumn("offering");
		customerOfferingGrid.removeColumn("id");
		
//		customerOfferingGrid.addColumn(CustomerOffering::getCustomerName).setCaption("customerName");
//		customerOfferingGrid.addColumn(CustomerOffering::getCustomerHkid).setCaption("customerHkid");
//		customerOfferingGrid.addColumn(CustomerOffering::getOfferingId).setCaption("offeringId");
//		customerOfferingGrid.addColumn(CustomerOffering::getTourId).setCaption("tourId");
//		customerOfferingGrid.addColumn(CustomerOffering::getTourName).setCaption("tourName");
//		customerOfferingGrid.addColumn(CustomerOffering::getPeople).setCaption("Number of Adults, Children, Toddlers");
		
		customerOfferingGrid.setColumnOrder("customerHkid", "customerName", "offeringId", "tourId", "tourName", "people", 
				"amountPaid", "totalCost", "specialRequests", "paymentStatus");
		customerOfferingGrid.getColumn("people").setCaption("Number of Adults, Children, Toddlers");
		
		this.addComponent(customerOfferingGrid);
		
		//TODO add button listeners
	}
}
