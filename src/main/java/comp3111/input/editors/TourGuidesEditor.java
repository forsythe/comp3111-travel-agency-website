package comp3111.input.editors;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.event.selection.SelectionEvent;
import com.vaadin.event.selection.SelectionListener;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.DB;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.TourGuideRepository;
import comp3111.input.validators.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class TourGuidesEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(TourGuidesEditor.class);

	private Window subwindow;

	// Editable fields
	private TextField tourGuideName;
	private TextField tourGuideLineUsername;

	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourGuideButton = new Button("Create new tour guide");
	private Button editTourGuideButton = new Button("Edit tour guide");
	private Button viewGuidedToursButton = new Button("View guided tours");

	/* subwindow action buttons */
	private Button subwindowConfirm;

	Grid<TourGuide> tourGuideGrid = new Grid<TourGuide>(TourGuide.class);

	TourGuide selectedTourGuide;

	private TourGuideRepository tourGuideRepo;
	private final HashSet<TourGuide> tourGuideCollectionCached = new HashSet<TourGuide>();

	@Autowired
	public TourGuidesEditor(TourGuideRepository tgr) {
		this.tourGuideRepo = tgr;
		// adding components
		rowOfButtons.addComponent(createTourGuideButton);
		rowOfButtons.addComponent(editTourGuideButton);
		rowOfButtons.addComponent(viewGuidedToursButton);
		createTourGuideButton.setId("button_create_tour_guide");
		editTourGuideButton.setId("button_edit_tour_guide");
		viewGuidedToursButton.setId("button_view_guided_tours");

		// edit and manage shouldn't be enabled with no tour guide selected
		editTourGuideButton.setEnabled(false);
		viewGuidedToursButton.setEnabled(false);

		this.addComponent(rowOfButtons);

		tourGuideGrid.setWidth("100%");
		tourGuideGrid.setSelectionMode(SelectionMode.SINGLE);

		tourGuideGrid.addSelectionListener(new SelectionListener<TourGuide>() {
			@Override
			public void selectionChange(SelectionEvent event) {
				Collection<TourGuide> selectedItems = tourGuideGrid.getSelectionModel().getSelectedItems();
				selectedTourGuide = null;
				for (TourGuide rt : selectedItems) { // easy way to get first element of set
					selectedTourGuide = rt;
					break;
				}
				if (selectedTourGuide != null) {
					editTourGuideButton.setEnabled(true);
					viewGuidedToursButton.setEnabled(true);
					createTourGuideButton.setEnabled(false);
				} else {
					editTourGuideButton.setEnabled(false);
					viewGuidedToursButton.setEnabled(false);
					createTourGuideButton.setEnabled(true);
				}
			}
		});

		tourGuideGrid.setColumnOrder(DB.TOURGUIDE_ID, DB.TOURGUIDE_NAME, DB.TOURGUIDE_LINE_USERNAME);

//		HeaderRow filterRow = tourGuideGrid.appendHeaderRow();
		
		for (Column<TourGuide, ?> col : tourGuideGrid.getColumns()) {
			col.setMinimumWidth(120);
			col.setHidable(true);
			col.setHidingToggleCaption(col.getCaption());
			col.setExpandRatio(1);
		}
		
		this.addComponent(tourGuideGrid);

		createTourGuideButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().addWindow(getSubwindow(tourGuideRepo, tourGuideCollectionCached, new TourGuide()));
			}

		});

		editTourGuideButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent()
						.addWindow(getSubwindow(tourGuideRepo, tourGuideCollectionCached, selectedTourGuide));
			}
		});
	}

	private Window getSubwindow(TourGuideRepository tourGuideRepo, Collection<TourGuide> tourGuideCollectionCached,
			TourGuide tourGuideToSave) {
		// Creating the confirm button
		subwindowConfirm = new Button("Confirm");
		subwindowConfirm.setId("button_confirm_tour_guide");

		tourGuideName = new TextField("Name");
		tourGuideName.setId("tf_tour_guide_name");
		tourGuideLineUsername = new TextField("LINE Username");
		tourGuideLineUsername.setId("tf_tour_guide_line_id");

		if (tourGuideToSave.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new tour guide");
		} else {
			subwindow = new Window("Edit a tour guide");
		}

		FormLayout subContent = new FormLayout();
		subwindow.setWidth("400px");
		subwindow.setContent(subContent);
		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		subContent.addComponent(tourGuideName);
		subContent.addComponent(tourGuideLineUsername);

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirm);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		subContent.addComponent(buttonActions);

		Binder<TourGuide> binder = new Binder<>();
		binder.forField(tourGuideName).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(TourGuide::getName, TourGuide::setName);

		binder.forField(tourGuideLineUsername).withValidator(ValidatorFactory.getStringLengthValidator(255))
				.asRequired(Utils.generateRequiredError()).bind(TourGuide::getLineUsername, TourGuide::setLineUsername);

		binder.setBean(tourGuideToSave);

		subwindowConfirm.addClickListener(event -> {
			BinderValidationStatus<TourGuide> validationStatus = binder.validate();

			if (validationStatus.isOk()) {
				// Customer must be created by Spring, otherwise it cannot be saved.
				// I do not have access to an empty constructor here
				binder.writeBeanIfValid(tourGuideToSave);

				log.info("About to save tour guide [{}]", tourGuideName.getValue());

				tourGuideRepo.save(tourGuideToSave);
				this.refreshData();
				subwindow.close();
				log.info("Saved a new/edited tour guide [{}] successfully", tourGuideName.getValue());

				binder.removeBean();
			} else {
				StringBuilder stringBuilder = new StringBuilder();

				for (BindingValidationStatus<?> result : validationStatus.getFieldValidationErrors()) {
					if (result.getField() instanceof AbstractField && result.getMessage().isPresent()) {
						stringBuilder.append(((AbstractField) result.getField()).getCaption()).append(" ")
								.append(result.getMessage().get()).append("\n");
					}
				}
				Notification.show("Could not create/edit tour guide!", stringBuilder.toString(),
						Notification.TYPE_ERROR_MESSAGE);
			}
		});

		return subwindow;
	}

	public interface ChangeHandler {
		void onChange();
	}

	public TextField getTourGuideName() {
		return tourGuideName;
	}

	public TextField getTourGuideLineId() {
		return tourGuideLineUsername;
	}

	public Button getCreateTourGuideButton() {
		return createTourGuideButton;
	}

	public Button getEditTourGuideButton() {
		return editTourGuideButton;
	}

	public Button getViewGuidedToursButton() {
		return viewGuidedToursButton;
	}

	public void refreshData() {
		Iterable<TourGuide> tourGuides = tourGuideRepo.findAll();
		tourGuideCollectionCached.clear();
		tourGuides.forEach(tourGuideCollectionCached::add);
		ListDataProvider<TourGuide> provider = new ListDataProvider<TourGuide>(tourGuideCollectionCached);
		tourGuideGrid.setDataProvider(provider);
		// tourGrid.setItems(tourCollectionCached);

	}

}
