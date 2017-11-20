package comp3111.input.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.GridCol;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.TourGuideRepository;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.GuidedByView;
import comp3111.view.NotificationFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Represents the tour guide editor in the TourGuideManagementView
 * 
 * @author Forsythe
 * 
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class TourGuidesEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(TourGuidesEditor.class);

	GuidedByViewer guidedByViewer;

	TextField tourGuideName;
	TextField tourGuideLineUsername;

	BinderValidationStatus<TourGuide> validationStatus;

	private Window subwindow;

	private HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourGuideButton = new Button("Create new tour guide");
	private Button editTourGuideButton = new Button("Edit tour guide");
	private Button viewGuidedToursButton = new Button("View guided offerings");

	/* subwindow action buttons */
	private Button subwindowConfirm;

	private Grid<TourGuide> tourGuideGrid = new Grid<TourGuide>(TourGuide.class);

	private TourGuide selectedTourGuide;

	TourGuideRepository tourGuideRepo;
	private final HashSet<TourGuide> tourGuideCollectionCached = new HashSet<TourGuide>();

	public HashSet<TourGuide> getTourGuideCollectionCached() {
		return tourGuideCollectionCached;
	}

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	/**
	 * Constructs the editor for creating/editing Tour Guides
	 * 
	 * @param tgr
	 *            The TourGuideRepository
	 * @param gbv
	 *            The GuidedByViewer
	 */
	@Autowired
	public TourGuidesEditor(TourGuideRepository tgr, GuidedByViewer gbv) {
		this.tourGuideRepo = tgr;
		this.guidedByViewer = gbv;

		// adding components
		rowOfButtons.addComponent(createTourGuideButton);
		rowOfButtons.addComponent(editTourGuideButton);
		rowOfButtons.addComponent(viewGuidedToursButton);
		createTourGuideButton.setId("btn_create_tour_guide");
		editTourGuideButton.setId("btn_edit_tour_guide");
		viewGuidedToursButton.setId("btn_view_guided_tours");

		// edit and manage shouldn't be enabled with no tour guide selected
		editTourGuideButton.setEnabled(false);
		viewGuidedToursButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		tourGuideGrid.setWidth("100%");
		tourGuideGrid.setSelectionMode(SelectionMode.SINGLE);

		tourGuideGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedTourGuide = event.getFirstSelectedItem().get();
				editTourGuideButton.setEnabled(true);
				viewGuidedToursButton.setEnabled(true);
				createTourGuideButton.setEnabled(false);
			} else {
				selectedTourGuide = null;
				editTourGuideButton.setEnabled(false);
				viewGuidedToursButton.setEnabled(false);
				createTourGuideButton.setEnabled(true);
			}
		});

		tourGuideGrid.setColumnOrder(GridCol.TOURGUIDE_ID, GridCol.TOURGUIDE_NAME, GridCol.TOURGUIDE_LINE_USERNAME);

		FilterFactory.addFilterInputBoxesToGridHeaderRow(TourGuide.class, tourGuideGrid, gridFilters);

		this.addComponent(tourGuideGrid);

		createTourGuideButton.addClickListener(event -> {
			getUI();
			UI.getCurrent().addWindow(getSubwindow(tourGuideRepo, tourGuideCollectionCached, new TourGuide()));

		});

		editTourGuideButton.addClickListener(event -> {
			getUI();
			UI.getCurrent().addWindow(getSubwindow(tourGuideRepo, tourGuideCollectionCached, selectedTourGuide));

		});

		viewGuidedToursButton.addClickListener(event -> {
			guidedByViewer.setSelectedTourGuide(selectedTourGuide);
			guidedByViewer.setTourGuidesEditor(this);
			getUI().getNavigator().navigateTo(GuidedByView.VIEW_NAME);
			refreshData();
		});
	}

	public Window getSubwindow(TourGuideRepository tourGuideRepo, Collection<TourGuide> tourGuideCollectionCached,
			TourGuide tourGuideToSave) {
		// Creating the confirm button
		subwindowConfirm = new Button("Confirm");
		getSubwindowConfirmButton().setId("btn_confirm_tour_guide");

		tourGuideName = new TextField("Name");
		tourGuideName.setId("tf_tour_guide_name");
		tourGuideLineUsername = new TextField("LINE Username");
		tourGuideLineUsername.setId("tf_tour_guide_line_id");

		if (tourGuideToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new tour guide");
		} else {
			subwindow = new Window("Edit a tour guide");
		}

		FormLayout form = new FormLayout();
		VerticalLayout formContainer = new VerticalLayout();
		formContainer.addComponent(form);

		subwindow.setWidth("400px");
		subwindow.setContent(formContainer);
		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		form.addComponent(tourGuideName);
		form.addComponent(tourGuideLineUsername);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(getSubwindowConfirmButton());
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		form.addComponent(buttonActions);

		Binder<TourGuide> binder = new Binder<>();
		binder.forField(tourGuideName).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(TourGuide::getName, TourGuide::setName);

		binder.forField(tourGuideLineUsername).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(TourGuide::getLineUsername, TourGuide::setLineUsername);

		binder.setBean(tourGuideToSave);

		getSubwindowConfirmButton().addClickListener(event -> {
			validationStatus = binder.validate();

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(tourGuideToSave);

				log.info("About to save tour guide [{}]", tourGuideName.getValue());

				tourGuideRepo.save(tourGuideToSave);
				this.refreshData();
				subwindow.close();
				log.info("Saved a new/edited tour guide [{}] successfully", tourGuideName.getValue());

				binder.removeBean();
				if (Page.getCurrent() != null)
					NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

			} else {
				String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);
				if (Page.getCurrent() != null)
					NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());
			}
		});

		return subwindow;
	}

	/**
	 * Refreshes the data in the vaadin grid
	 */
	public void refreshData() {
		Iterable<TourGuide> tourGuides = tourGuideRepo.findAll();
		if (tourGuides != null) {
			tourGuideCollectionCached.clear();
			tourGuides.forEach(tourGuideCollectionCached::add);
			ListDataProvider<TourGuide> provider = new ListDataProvider<TourGuide>(tourGuideCollectionCached);
			tourGuideGrid.setDataProvider(provider);
		}
		// tourGrid.setItems(tourCollectionCached);

	}

	public Button getSubwindowConfirmButton() {
		return subwindowConfirm;
	}

	public TextField getTourGuideName() {
		return tourGuideName;
	}

	public TextField getTourGuideLineUsername() {
		return tourGuideLineUsername;
	}

	public BinderValidationStatus<TourGuide> getValidationStatus() {
		return validationStatus;
	}

}
