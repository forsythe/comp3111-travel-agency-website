package comp3111.presenter;
//package springboot.presenter;
//
//import java.util.Collection;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
//
//import com.vaadin.navigator.Navigator;
//import com.vaadin.server.FontAwesome;
//import com.vaadin.server.VaadinRequest;
//import com.vaadin.shared.ui.ValueChangeMode;
//import com.vaadin.spring.annotation.SpringUI;
//import com.vaadin.ui.Button;
//import com.vaadin.ui.Grid;
//import com.vaadin.ui.HorizontalLayout;
//import com.vaadin.ui.TextField;
//import com.vaadin.ui.UI;
//import com.vaadin.ui.VerticalLayout;
//
//import springboot.model.Customer;
//import springboot.repo.CustomerRepository;
//
//@SpringUI
//public class EntryUI extends UI {
//
//	public Navigator navigator;
//	
//	@Override
//	protected void init(VaadinRequest request) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	
//	
//	
//	private final CustomerRepository repo;
//
//	private final CustomerEditor editor;
//
//	private final Grid<Customer> grid;
//
//	final TextField filter;
//
//	private final Button addNewBtn;
//
//	@Autowired
//	public EntryUI(CustomerRepository repo, CustomerEditor editor) {
//		this.repo = repo;
//		this.editor = editor;
//		this.grid = new Grid<>(Customer.class);
//		this.filter = new TextField();
//		this.addNewBtn = new Button("New customer", FontAwesome.PLUS);
//	}
//
//	@Override
//	public void init(VaadinRequest request) {
//		// build layout
//		HorizontalLayout actions = new HorizontalLayout(filter, addNewBtn);
//		VerticalLayout mainLayout = new VerticalLayout(actions, getGrid(), editor);
//		setContent(mainLayout);
//
//		getGrid().setHeight(300, Unit.PIXELS);
//		getGrid().setColumns("name", "age");
//
//		filter.setPlaceholder("Filter by name");
//
//		// Hook logic to components
//
//		// Replace listing with filtered content when user changes filter
//		filter.setValueChangeMode(ValueChangeMode.LAZY);
//		filter.addValueChangeListener(e -> listCustomers(e.getValue()));
//
//		// Connect selected Customer to editor or hide if none is selected
//		getGrid().asSingleSelect().addValueChangeListener(e -> {
//			editor.editCustomer(e.getValue());
//		});
//
//		// Instantiate and edit new Customer the new button is clicked
//		addNewBtn.addClickListener(e -> editor.editCustomer(new Customer("", 0)));
//
//		// Listen changes made by the editor, refresh data from backend
//		editor.setChangeHandler(() -> {
//			editor.setVisible(false);
//			listCustomers(filter.getValue());
//		});
//
//		// Initialize listing
//		listCustomers(null);
//	}
//
//	// tag::listCustomers[]
//	public void listCustomers(String filterText) {
//		if (StringUtils.isEmpty(filterText)) {
//			getGrid().setItems((Collection<Customer>) repo.findAll());
//		}
//		else {
//			getGrid().setItems(repo.findByName(filterText));
//		}
//	}
//	// end::listCustomers[]
//
//	public Grid<Customer> getGrid() {
//		return grid;
//	}
//
//}
