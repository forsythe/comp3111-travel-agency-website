package comp3111.input.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValueProvider;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.DBManager;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.OfferingRepository;
import comp3111.input.converters.LocalDateToUtilDateConverter;
import comp3111.input.exceptions.OfferingDateUnsupportedException;
import comp3111.input.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.input.exceptions.TourGuideUnavailableException;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.TourGuidesManagementView;
import comp3111.view.TourManagementView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

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
	private final HashSet<Offering> offeringsCollectionCached = new HashSet<>();

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

		offeringGrid.removeColumn(DB.OFFERING_TOUR); // we'll combine days of week and dates
		offeringGrid.removeColumn(DB.OFFERING_TOUR_GUIDE);
		offeringGrid.removeColumn(DB.OFFERING_DATE);
		offeringGrid.removeColumn(DB.OFFERING_TOUR_GUIDE_NAME);
		offeringGrid.removeColumn(DB.OFFERING_TOUR_GUIDE_LINE_ID);
		offeringGrid.removeColumn(DB.OFFERING_MAX_CAPACITY);
		offeringGrid.removeColumn(DB.OFFERING_MIN_CAPACITY);
		offeringGrid.removeColumn(DB.OFFERING_LAST_EDITABLE_DATE);

		offeringGrid.addColumn(offering -> dbManager.countNumberOfPeopleInOffering(offering));

		offeringGrid.setColumnOrder(DB.OFFERING_ID, DB.OFFERING_START_DATE,
				DB.OFFERING_TOUR_NAME, DB.OFFERING_HOTEL_NAME);

		for (Column<Offering, ?> col : offeringGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
		}

		this.addComponent(offeringGrid);

		returnButton.addClickListener(event -> {
			getUI().getNavigator().navigateTo(TourGuidesManagementView.VIEW_NAME);
		});

	}

	public interface ChangeHandler {
		void onChange();
	}

	public void refreshData() {
		offeringsCollectionCached.clear();
		for (Offering o : offeringRepo.findAll()) {
			if (o.getTourGuide().equals(this.selectedTourGuide))
				offeringsCollectionCached.add(o);

		}
		ListDataProvider<Offering> provider = new ListDataProvider<>(offeringsCollectionCached);
		offeringGrid.setDataProvider(provider);
	}

	// Helpers for accessing stuff from tourGuidesEditor
	void setSelectedTourGuide(TourGuide selectedTourGuide) {
		this.selectedTourGuide = selectedTourGuide;
	}

	public TourGuide getSelectedTourGuide() {
		return this.selectedTourGuide;
	}

	void setTourGuidesEditor(TourGuidesEditor te) {
		this.tourGuidesEditor = te;
	}
}
