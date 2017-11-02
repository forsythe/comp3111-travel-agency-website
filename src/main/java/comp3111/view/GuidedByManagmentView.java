package comp3111.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import comp3111.input.editors.GuidedByViewer;
import comp3111.input.editors.OfferingEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

@SpringView(name = GuidedByManagmentView.VIEW_NAME)
public class GuidedByManagmentView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "guidedByManagement";
	private static final Logger log = LoggerFactory.getLogger(GuidedByManagmentView.class);

	@Autowired
	private GuidedByViewer guidedByViewer;

	private Label titleLabel;

	@PostConstruct
	void init() {
		titleLabel = new Label();

		// A container that is 100% wide by default
		VerticalLayout layout = new VerticalLayout();

		// label will only take the space it needs
		layout.addComponent(titleLabel);
		layout.addComponent(guidedByViewer);

		this.addComponent(layout);
	}

	@Override
	// called AFTER init()
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
		// everytime we enter this page, we want to update the data in the grid
		this.guidedByViewer.refreshData();
		this.titleLabel.setCaption(
				"<h1>All tours assigned to: <b>" + guidedByViewer.getSelectedTourGuide().getName()+ "</b></h1>");
		this.titleLabel.setCaptionAsHtml(true);
	}

	/**
	 * @return determines whether the offering editor has an associated tour or not.
	 *         If the user tries to manually navigate to the offering management
	 *         page without first selecting a tour through the tour management page,
	 *         then this function tells us so, and we prevent them from navigating
	 *         to it. Used in {@link VaadinLoginUI}
	 */
	public boolean userHasSelectedTourGuide() {
		return this.guidedByViewer.getSelectedTourGuide() != null;
	}
}
