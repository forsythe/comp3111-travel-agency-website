package comp3111.presenter;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.SelectionMode;

import comp3111.model.Customer;
import comp3111.model.Tour;
import comp3111.repo.CustomerRepository;
import comp3111.repo.TourRepository;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;

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
	private static final Logger log = LoggerFactory.getLogger(CustomerEditor.class);
	
	private Window createCustomerSubwindow;
	
	/* Fields to edit properties in Tour entity */
	private TextField customerName;
	private TextField customerLineId;
	private TextField customerHkid;
	private TextField customerPhone;
	private TextField customerAge;
	
	/* action buttons */
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createNewCustomerButton = new Button("Create new customer");
	private Button editCustomerButton = new Button("Edit customer");
	private Button viewCustomerBookingsButton = new Button("View bookings made by customer");
	
	/* subwindow action buttons */
	private Button subwindowConfirmCreateCustomer;
	
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
		
		createNewCustomerButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().addWindow(getCreateCustomerWindow(customerRepo, customerCollectionCached));
			}

		});
	}
	
	private Window getCreateCustomerWindow(CustomerRepository customerRepo, Collection<Customer> customerCollectionCached) {
		subwindowConfirmCreateCustomer = new Button("Confirm");
		
		customerName = new TextField("Customer Name");
		customerLineId = new TextField("Customer Line Id");
		customerHkid = new TextField("Customer HKID");
		customerPhone = new TextField("Customer Phone");
		customerAge = new TextField("Customer Age");
		
		createCustomerSubwindow = new Window("Create new customer");
		FormLayout subContent = new FormLayout();
		
		createCustomerSubwindow.setWidth("800px");
		
		createCustomerSubwindow.setContent(subContent);
		createCustomerSubwindow.center();
		createCustomerSubwindow.setClosable(false);
		createCustomerSubwindow.setModal(true);
		createCustomerSubwindow.setResizable(false);
		createCustomerSubwindow.setDraggable(false);
		
		subContent.addComponent(customerName);
		subContent.addComponent(customerLineId);
		subContent.addComponent(customerHkid);
		subContent.addComponent(customerPhone);
		subContent.addComponent(customerAge);
		
		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirmCreateCustomer);
		buttonActions.addComponent(new Button("Cancel", event -> createCustomerSubwindow.close()));
		subContent.addComponent(buttonActions);
		
		customerName.setRequiredIndicatorVisible(true);
		customerHkid.setRequiredIndicatorVisible(true);
		customerPhone.setRequiredIndicatorVisible(true);
		customerAge.setRequiredIndicatorVisible(true);
		
		Utils.addValidator(customerName, ValidatorFactory.getStringLengthValidator(255));
		Utils.addValidator(customerLineId, ValidatorFactory.getStringLengthValidator(255));
		Utils.addValidator(customerHkid, ValidatorFactory.getStringLengthValidator(255));
		Utils.addValidator(customerPhone, ValidatorFactory.getStringLengthValidator(255));
		Utils.addValidator(customerAge, ValidatorFactory.getIntegerLowerBoundValidator(0));

		subwindowConfirmCreateCustomer.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				ArrayList<String> errorMsgs = new ArrayList<String>();
				ArrayList<TextField> nonNullableComponents = new ArrayList<TextField>();
				nonNullableComponents.addAll(
						Arrays.asList(customerName, customerHkid, customerPhone, customerAge ));
				
				for (TextField field : nonNullableComponents) {
					if (field.isEmpty()) {
						log.info(field.getCaption() + ": cannot be empty");
						errorMsgs.add(field.getCaption() + ": cannot be empty");
					}
				}
				
				ArrayList<TextField> fieldsWithValidators = new ArrayList<TextField>();
				fieldsWithValidators.addAll(
						Arrays.asList(customerName, customerLineId, customerHkid, customerPhone, customerAge ));
				
				for (TextField field : fieldsWithValidators) {
					if (field.getErrorMessage() != null) {
						log.info(field.getCaption() + ": " + field.getErrorMessage().toString());
						errorMsgs.add(field.getCaption() + ": " + field.getErrorMessage().toString());
					}
				}
				
				log.info("errorMsgs.size() is [{}]", errorMsgs.size());

				if (errorMsgs.size() == 0) {
					Customer newCustomer = new Customer(customerName.getValue(), 
							customerLineId.getValue(), customerPhone.getValue(), 
							Integer.parseInt(customerAge.getValue()), customerHkid.getValue());
					
					log.info("Saved a new customer [{}] successfully", customerName.getValue());
					customerName.clear();
					customerLineId.clear();
					customerHkid.clear();
					customerAge.clear();
					customerPhone.clear();
					
					customerCollectionCached.add(customerRepo.save(newCustomer));
					customersGrid.setItems(customerCollectionCached);
					createCustomerSubwindow.close();
				} else {
					String errorString = "";
					for (String err : errorMsgs) {
						errorString += err + "\n";
					}
					Notification.show("Could not create customer!", errorString, Notification.TYPE_ERROR_MESSAGE);
				}
			}
		});
		return createCustomerSubwindow;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public Window getCreateCustomerSubwindow() {
		return createCustomerSubwindow;
	}

	public TextField getCustomerName() {
		return customerName;
	}

	public TextField getCustomerLineId() {
		return customerLineId;
	}

	public TextField getCustomerHkid() {
		return customerHkid;
	}

	public TextField getCustomerPhone() {
		return customerPhone;
	}

	public TextField getCustomerAge() {
		return customerAge;
	}

	public Button getCreateNewCustomerButton() {
		return createNewCustomerButton;
	}

	public Button getEditCustomerButton() {
		return editCustomerButton;
	}

	public Button getViewCustomerBookingsButton() {
		return viewCustomerBookingsButton;
	}

	public Button getSubwindowConfirmCreateCustomer() {
		return subwindowConfirmCreateCustomer;
	}

}
