package comp3111.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.input.auth.Authentication;

/**
 * Generates the UI elements for the front-end side of the Login page which is also the landing page. 
 * @author kristiansuhartono
 *
 */
@Theme("valo")
@SpringUI
@SpringViewDisplay
public class VaadinLoginUI extends UI implements ViewDisplay {

	private Panel springViewDisplay;

	@Autowired
	Authentication authentication;

	HorizontalLayout root;
	VerticalLayout navigationBar;

	@Override
	public void init(VaadinRequest request) {

		root = new HorizontalLayout();
		root.setSizeFull();
		setContent(root);
		springViewDisplay = new Panel();
		springViewDisplay.setSizeFull();
		root.addComponent(springViewDisplay);
		springViewDisplay.setVisible(false);

		drawLoginForm();

		getUI().getNavigator().setErrorView(HomeView.class);

		getUI().getNavigator().addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				View newView = event.getNewView();
				String newViewName = event.getViewName();
				if (newViewName.equals(OfferingManagementView.VIEW_NAME)) {
					// prevent a user from directly accessing offering management view without
					// selecting a tour for the offerings
					return ((OfferingManagementView) newView).userHasSelectedTour();
				}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				// NO-OP
			}
		});

		getUI().getNavigator().addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {
				View newView = event.getNewView();
				String newViewName = event.getViewName();
				if (newViewName.equals(GuidedByManagmentView.VIEW_NAME)) {
					// prevent a user from directly accessing
					return ((GuidedByManagmentView) newView).userHasSelectedTourGuide();
				}
				return true;
			}

			@Override
			public void afterViewChange(ViewChangeEvent event) {
				// NO-OP
			}
		});
	}

	private Button createNavigationButton(String caption, final String viewName) {
		Button button = new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_LINK);
		button.addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
		button.setId(caption);
		return button;
	}

	private Button createLogoutButton() {
		Button button = new Button("Logout");
		button.addStyleName(ValoTheme.BUTTON_LINK);

		button.addClickListener(event -> {
			navigationBar.setVisible(false);
			springViewDisplay.setVisible(false);
			getUI().getNavigator().navigateTo(HomeView.VIEW_NAME);
			drawLoginForm();
		});
		return button;
	}

	private void drawLoginForm() {
		Panel loginPanel = new Panel("Login");
		loginPanel.setSizeUndefined();
		root.addComponent(loginPanel);
		root.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);

		FormLayout content = new FormLayout();
		TextField username = new TextField("Username");
		username.setId("tf_username");
		content.addComponent(username);
		PasswordField password = new PasswordField("Password");
		password.setId("tf_password");
		content.addComponent(password);

		Button submit = new Button("Enter");
		submit.setId("btn_submit");

		content.addComponent(submit);
		content.setSizeUndefined();
		content.setMargin(true);
		loginPanel.setContent(content);
		root.setComponentAlignment(loginPanel, Alignment.MIDDLE_CENTER);

		submit.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
			public void buttonClick(ClickEvent event) {
				if (authentication.authenticate(username.getValue(), password.getValue())) {
					drawLoggedInComponents();
					username.clear();
					password.clear();
					loginPanel.setVisible(false);
				} else {
					NotificationFactory.getTopBarErrorNotification("Invalid credentials.", null, 5)
							.show(Page.getCurrent());
					;

				}
			}

		});
	}

	private void drawLoggedInComponents() {
		navigationBar = new VerticalLayout();

		root.removeAllComponents();

		Label title = new Label("COMP3111 travel agency");
		title.setId("lbl_title");

		navigationBar.addComponent(title);
		navigationBar.addComponent(createNavigationButton("Home View", HomeView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Tour Management", TourManagementView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Bookings", BookingsManagementView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Customers", CustomerManagementView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Tour Guides", TourGuideManagementView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Customer Engagement", CustomerEngagementView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Promotion Management", PromoEventManagementView.VIEW_NAME));

		navigationBar.addComponent(createLogoutButton());

		navigationBar.setSizeUndefined();
		navigationBar.setSpacing(false);

		springViewDisplay.setSizeFull();
		root.addComponent(navigationBar);
		root.addComponent(springViewDisplay);

		navigationBar.setVisible(true);
		springViewDisplay.setVisible(true);

		root.setExpandRatio(navigationBar, 0);
		root.setExpandRatio(springViewDisplay, 1);

	}

	/** 
	 * Sets the UI to display an the passed view element
	 * @see com.vaadin.navigator.ViewDisplay#showView(com.vaadin.navigator.View)
	 */
	@Override
	public void showView(View view) {
		springViewDisplay.setContent((Component) view);
	}
}