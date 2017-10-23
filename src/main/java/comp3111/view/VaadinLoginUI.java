package comp3111.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import comp3111.auth.Authentication;

@Theme("valo")
@SpringUI
@SpringViewDisplay

// need this to find the repos, since in different package!
public class VaadinLoginUI extends UI implements ViewDisplay {

	private Panel springViewDisplay;

	@Autowired
	Authentication authentication;

	HorizontalLayout root;
	VerticalLayout navigationBar;

	@Override
	public void init(VaadinRequest request) {
		// getUI().getNavigator().setErrorView("");

		root = new HorizontalLayout();
		root.setSizeFull();
		setContent(root);
		springViewDisplay = new Panel();
		springViewDisplay.setSizeFull();
		root.addComponent(springViewDisplay);
		springViewDisplay.setVisible(false);

		drawLoginForm();
	}

	private Button createNavigationButton(String caption, final String viewName) {
		Button button = new Button(caption);
		button.addStyleName(ValoTheme.BUTTON_LINK);
		button.addClickListener(event -> getUI().getNavigator().navigateTo(viewName));
		return button;
	}

	private Button createLogoutButton() {
		Button button = new Button("Logout");
		button.addStyleName(ValoTheme.BUTTON_LINK);

		button.addClickListener(new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				navigationBar.setVisible(false);
				springViewDisplay.setVisible(false);
				getUI().getNavigator().navigateTo(HomeView.VIEW_NAME);
				drawLoginForm();
			}

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
					Notification.show("Invalid credentials", Notification.Type.WARNING_MESSAGE);
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
		navigationBar.addComponent(createNavigationButton("Customers", CustomersManagementView.VIEW_NAME));
		navigationBar.addComponent(createNavigationButton("Tour Guides", TourGuidesManagementView.VIEW_NAME));
		navigationBar.addComponent(
				createNavigationButton("Customer Engagement", CustomerEngagementManagementView.VIEW_NAME));
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

	@Override
	public void showView(View view) {
		springViewDisplay.setContent((Component) view);
	}
}