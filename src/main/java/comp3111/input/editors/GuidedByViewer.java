package comp3111.input.editors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import comp3111.Utils;
import comp3111.data.GridCol;
import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.OfferingRepository;
import comp3111.view.TourGuideManagementView;

/**
 * Represents the view which shows all the offerings assigned to a tour guide
 * 
 * @author Forsythe
 *
 */
@SpringComponent
@UIScope
public class GuidedByViewer extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(GuidedByViewer.class);

	private TourGuidesEditor tourGuidesEditor;
	private TourGuide selectedTourGuide;

	private OfferingRepository offeringRepo;

	@Autowired
	private DBManager dbManager;

	private final Grid<Offering> offeringGrid = new Grid<Offering>(Offering.class);

	/**
	 * @param or
	 *            Autowired, construct injection
	 */
	@Autowired
	public GuidedByViewer(OfferingRepository or) {
		offeringRepo = or;

		Button returnButton = new Button("Return");
		HorizontalLayout rowOfButtons = new HorizontalLayout();

		rowOfButtons.addComponent(returnButton);
		returnButton.setId("btn_return_offering");

		this.addComponent(rowOfButtons);

		this.refreshData();

		offeringGrid.setWidth("100%");
		offeringGrid.setSelectionMode(SelectionMode.SINGLE);

		offeringGrid.removeColumn(GridCol.OFFERING_TOUR); // we'll combine days of week and dates
		offeringGrid.removeColumn(GridCol.OFFERING_TOUR_GUIDE);
		offeringGrid.removeColumn(GridCol.OFFERING_DATE);
		offeringGrid.removeColumn(GridCol.OFFERING_TOUR_GUIDE_NAME);
		offeringGrid.removeColumn(GridCol.OFFERING_TOUR_GUIDE_LINE_ID);
		offeringGrid.removeColumn(GridCol.OFFERING_MAX_CAPACITY);
		offeringGrid.removeColumn(GridCol.OFFERING_MIN_CAPACITY);
		offeringGrid.removeColumn(GridCol.OFFERING_LAST_EDITABLE_DATE);

		offeringGrid.addColumn(offering -> dbManager.countNumberOfPaidPeopleInOffering(offering))
				.setCaption("Total number of paying people");

		offeringGrid.setColumnOrder(GridCol.OFFERING_ID, GridCol.OFFERING_START_DATE, GridCol.OFFERING_TOUR_NAME,
				GridCol.OFFERING_HOTEL_NAME);
		offeringGrid.getColumn(GridCol.OFFERING_START_DATE).setCaption("Start Date");

		for (Column<Offering, ?> col : offeringGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
		}

		this.addComponent(offeringGrid);

		returnButton.addClickListener(event -> {
			getUI().getNavigator().navigateTo(TourGuideManagementView.VIEW_NAME);
		});

	}

	public void refreshData() {

		ListDataProvider<Offering> provider = new ListDataProvider<>(
				Utils.iterableToCollection(offeringRepo.findByTourGuide(this.selectedTourGuide)));
		offeringGrid.setDataProvider(provider);
	}

	/**
	 * Sets the selected tour guide (to show all their offerings)
	 * 
	 * @param selectedTourGuide
	 *            The selected tour guide
	 */
	void setSelectedTourGuide(TourGuide selectedTourGuide) {
		this.selectedTourGuide = selectedTourGuide;
	}

	/**
	 * @return The selected tour guide
	 */
	public TourGuide getSelectedTourGuide() {
		return this.selectedTourGuide;
	}

	/**
	 * @param te
	 *            Sets the tour guide editor (the parent view)
	 */
	void setTourGuidesEditor(TourGuidesEditor te) {
		this.tourGuidesEditor = te;
	}
}
