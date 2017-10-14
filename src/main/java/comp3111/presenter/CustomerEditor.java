package comp3111.presenter;

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

import comp3111.model.Customer;
import comp3111.repo.CustomerRepository;

/**
 * A simple example to introduce building forms. As your real application is
 * probably much more complicated than this example, you could re-use this form
 * in multiple places. This example component is only used in VaadinUI.
 * <p>
 * In a real world application you'll most likely using a common super class for
 * all your forms - less code, better UX. See e.g. AbstractForm in Viritin
 * (https://vaadin.com/addon/viritin).
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class CustomerEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(TourEditor.class);
	
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewCustomerButton = new Button("Create new customer");
	private Button editCustomerButton = new Button("Edit customer");
	private Button viewCustomerBookingsButton = new Button("View bookings made by customer");
	
	
	Grid<Customer> customersGrid = new Grid<Customer>(Customer.class);
	
	Customer selectedCustomer;
	
	@SuppressWarnings("unchecked")
	@Autowired
	public CustomerEditor(CustomerRepository customerRepo) {
		// adding components
		rowOfButtons.addComponent(createNewCustomerButton);
		rowOfButtons.addComponent(editCustomerButton);
		rowOfButtons.addComponent(viewCustomerBookingsButton);
		
		//Shouldn't be enabled unless selected
		editCustomerButton.setEnabled(false);
		viewCustomerBookingsButton.setEnabled(false);
		
		//Render component
		this.addComponent(rowOfButtons);
		
		//Get from DB
		Iterable<Customer> customers = customerRepo.findAll();
		Collection<Customer> customerCollectionCached = new HashSet<Customer>();
		customers.forEach(customerCollectionCached::add);
		customersGrid.setItems(customerCollectionCached);

		customersGrid.setWidth("100%");
		customersGrid.setSelectionMode(SelectionMode.SINGLE);
		
		customersGrid.addSelectionListener(new SelectionListener<Customer>() {
			@Override
			public void selectionChange(SelectionEvent event) {
				Collection<Customer> selectedItems = 
						customersGrid.getSelectionModel().getSelectedItems();
				selectedCustomer = null;
				for (Customer rt : selectedItems) { // easy way to get first element of set
					selectedCustomer = rt;
					break;
				}
				if (selectedCustomer != null) {
					editCustomerButton.setEnabled(true);
					viewCustomerBookingsButton.setEnabled(true);
					createNewCustomerButton.setEnabled(false);
				} else {
					selectedCustomer = null;
					editCustomerButton.setEnabled(false);
					viewCustomerBookingsButton.setEnabled(false);
					createNewCustomerButton.setEnabled(true);
				}
			}
		});
		
		customersGrid.removeColumn("new");
		customersGrid.removeColumn("customerOffering");
		
		customersGrid.setColumnOrder("id", "name", "lineId", "hkid", "phone", "age");
		
		this.addComponent(customersGrid);
	}
}
