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

import comp3111.model.TourGuide;
import comp3111.repo.TourGuideRepository;
import comp3111.validators.Utils;
import comp3111.validators.ValidatorFactory;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class TourGuidesEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(TourGuidesEditor.class);
	
	//Editable fields
	private TextField tourGuideName;
	private TextField tourGuideLineId;
	
	Window createTourGuideSubwindow;
	
	HorizontalLayout rowOfButtons = new HorizontalLayout();
	private Button createTourGuideButton = new Button("Create new tour guide");
	private Button editTourGuideButton = new Button("Edit tour guide");
	private Button viewGuidedToursButton = new Button("View guided tours");
	
	/* subwindow action buttons */
	private Button subwindowConfirmCreateTourGuide;
	
	Grid<TourGuide> tourGuideGrid = new Grid<TourGuide>(TourGuide.class);
	
	TourGuide selectedTourGuide;
	
	@SuppressWarnings("unchecked")
	@Autowired
	public TourGuidesEditor(TourGuideRepository tourGuideRepo) {
		// adding components
		rowOfButtons.addComponent(createTourGuideButton);
		rowOfButtons.addComponent(editTourGuideButton);
		rowOfButtons.addComponent(viewGuidedToursButton);	
		
		// edit and manage shouldn't be enabled with no tour guide selected
		editTourGuideButton.setEnabled(false);
		viewGuidedToursButton.setEnabled(false);

		this.addComponent(rowOfButtons);
		
		//Get from db
		Iterable<TourGuide> tourGuides = tourGuideRepo.findAll();
		Collection<TourGuide> tourGuideCollectionCached = new HashSet<TourGuide>();
		tourGuides.forEach(tourGuideCollectionCached::add);
		tourGuideGrid.setItems(tourGuideCollectionCached);
		
		tourGuideGrid.setWidth("100%");
		tourGuideGrid.setSelectionMode(SelectionMode.SINGLE);

		tourGuideGrid.addSelectionListener(new SelectionListener<TourGuide>() {
			@Override
			public void selectionChange(SelectionEvent event) {
				Collection<TourGuide> selectedItems = 
						tourGuideGrid.getSelectionModel().getSelectedItems();
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
		
		tourGuideGrid.removeColumn("new"); // hibernate attributes, we don't care about it
		tourGuideGrid.removeColumn("guidedOfferings");
		
		tourGuideGrid.setColumnOrder("id", "name", "lineId");
		
		this.addComponent(tourGuideGrid);
		
		createTourGuideButton.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				getUI().getCurrent().addWindow(getCreateTourGuideWindow(tourGuideRepo, tourGuideCollectionCached));
			}

		});
	}
	
	private Window getCreateTourGuideWindow(TourGuideRepository tourGuideRepo, Collection<TourGuide> tourGuideCollectionCached) {
		subwindowConfirmCreateTourGuide = new Button("Confirm");
		
		tourGuideName = new TextField("Name");
		tourGuideLineId = new TextField("Line Id");
		
		
		createTourGuideSubwindow = new Window("Create new tour guide");
		
		FormLayout subContent = new FormLayout();
		createTourGuideSubwindow.setWidth("800px");
		createTourGuideSubwindow.setContent(subContent);
		createTourGuideSubwindow.center();
		createTourGuideSubwindow.setClosable(false);
		createTourGuideSubwindow.setModal(true);
		createTourGuideSubwindow.setResizable(false);
		createTourGuideSubwindow.setDraggable(false);
		
		subContent.addComponent(tourGuideName);
		subContent.addComponent(tourGuideLineId);
		
		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(subwindowConfirmCreateTourGuide);
		buttonActions.addComponent(new Button("Cancel", event -> createTourGuideSubwindow.close()));
		subContent.addComponent(buttonActions);
		
		tourGuideName.setRequiredIndicatorVisible(true);
		tourGuideLineId.setRequiredIndicatorVisible(true);
		
		Utils.addValidator(tourGuideName, ValidatorFactory.getStringLengthValidator(255));
		Utils.addValidator(tourGuideLineId, ValidatorFactory.getStringLengthValidator(255));
		
		subwindowConfirmCreateTourGuide.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				ArrayList<String> errorMsgs = new ArrayList<String>();
				ArrayList<TextField> nonNullableComponents = new ArrayList<TextField>();
				
				nonNullableComponents.addAll(
						Arrays.asList(tourGuideName, tourGuideLineId));
				
				for (TextField field : nonNullableComponents) {
					if (field.isEmpty()) {
						log.info(field.getCaption() + ": cannot be empty");
						errorMsgs.add(field.getCaption() + ": cannot be empty");
					}
				}
				
				ArrayList<TextField> fieldsWithValidators = new ArrayList<TextField>();
				fieldsWithValidators.addAll(
						Arrays.asList(tourGuideName, tourGuideLineId));
				
				for (TextField field : fieldsWithValidators) {
					if (field.getErrorMessage() != null) {
						log.info(field.getCaption() + ": " + field.getErrorMessage().toString());
						errorMsgs.add(field.getCaption() + ": " + field.getErrorMessage().toString());
					}
				}
				log.info("errorMsgs.size() is [{}]", errorMsgs.size());
				
				if (errorMsgs.size() == 0) {
					TourGuide newTourGuide = new TourGuide(tourGuideName.getValue(), tourGuideLineId.getValue());
				
					log.info("Saved a new tour guide [{}] successfully", tourGuideName.getValue());
					tourGuideName.clear();
					tourGuideLineId.clear();
					
					tourGuideCollectionCached.add(tourGuideRepo.save(newTourGuide));
					tourGuideGrid.setItems(tourGuideCollectionCached);
					createTourGuideSubwindow.close();
				} else {
					String errorString = "";
					for (String err : errorMsgs) {
						errorString += err + "\n";
					}
					Notification.show("Could not create tour guide!", errorString, Notification.TYPE_ERROR_MESSAGE);
				}			
			}
		});
		
		return createTourGuideSubwindow;
	}
	
	public interface ChangeHandler {
		void onChange();
	}
	
	public TextField getTourGuideName() {
		return tourGuideName;
	}

	public TextField getTourGuideLineId() {
		return tourGuideLineId;
	}

	public Button getSubwindowConfirmCreateTourGuide() {
		return subwindowConfirmCreateTourGuide;
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

}
