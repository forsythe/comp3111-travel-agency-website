package comp3111.input.editors;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.GridCol;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.PromoEventRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;

/**
 * Represents the promo event editor in PromoEventManagementView
 * 
 * @author Forsythe
 *
 */
@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class PromoEventEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(PromoEventEditor.class);

	private Grid<PromoEvent> eventGrid = new Grid<>(PromoEvent.class);

	private PromoEvent selectedEvent;

	// subwindow fields
	private ComboBox<Offering> offering;
	private DateTimeField triggerDate;
	private TextField discountMultiplier;
	private TextField maxReservationsPerCustomer;
	private TextField promoCode;
	private TextField promoCodeUses;
	private TextArea customMessage;

	private Button confirmButton;
	private BinderValidationStatus<PromoEvent> validationStatus;

	private BookingRepository bookingRepo;
	private CustomerRepository customerRepo;
	private OfferingRepository offeringRepo;
	private PromoEventRepository promoEventRepo;
	private DBManager dbManager;

	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();

	/**
	 * Constructs the editor for creating/editing PromoEvents
	 * 
	 * @param br
	 *            The BookingRepository
	 * @param cr
	 *            The CustomerRepository
	 * @param or
	 *            The OfferingRepository
	 * @param per
	 *            The PromoEventRepository
	 * @param dbm
	 *            The DBManager
	 */
	@Autowired
	public PromoEventEditor(BookingRepository br, CustomerRepository cr, OfferingRepository or,
			PromoEventRepository per, DBManager dbm) {
		this.bookingRepo = br;
		this.customerRepo = cr;
		this.offeringRepo = or;
		this.promoEventRepo = per;
		this.dbManager = dbm;

		Button createEventButton = new Button("Create new promotional event");
		Button editEventButton = new Button("Edit promotional event");

		HorizontalLayout rowOfButtons = new HorizontalLayout();
		rowOfButtons.addComponent(createEventButton);
		rowOfButtons.addComponent(editEventButton);

		// disable edit
		editEventButton.setEnabled(false);

		// add component to view
		this.addComponent(rowOfButtons);

		// get from GridCol
		if (promoEventRepo.findAll() != null) // mockito
			eventGrid.setItems(Utils.iterableToCollection(promoEventRepo.findAll()).stream()
					.sorted((b1, b2) -> b1.getId().compareTo(b2.getId())));

		eventGrid.setWidth("100%");
		eventGrid.setSelectionMode(SelectionMode.SINGLE);

		eventGrid.addSelectionListener(event -> {
			if (event.getFirstSelectedItem().isPresent()) {
				selectedEvent = event.getFirstSelectedItem().get();
				editEventButton.setEnabled(true);
				createEventButton.setEnabled(false);
			} else {
				selectedEvent = null;
				editEventButton.setEnabled(false);
				createEventButton.setEnabled(true);
			}
		});

		eventGrid.setColumnOrder(GridCol.PROMOEVENT_IS_TRIGGERED, GridCol.PROMOEVENT_TRIGGER_DATE,
				GridCol.PROMOEVENT_ID, GridCol.PROMOEVENT_OFFERING, GridCol.PROMOEVENT_CUSTOM_MESSAGE,
				GridCol.PROMOEVENT_MAX_RESERVATIONS_PER_CUSTOMER, GridCol.PROMOEVENT_PROMO_CODE,
				GridCol.PROMOEVENT_PROMO_CODE_USES_LEFT, GridCol.PROMOEVENT_DISCOUNT);

		HeaderRow filterRow = eventGrid.appendHeaderRow();

		for (Column<PromoEvent, ?> col : eventGrid.getColumns()) {
			col.setMinimumWidth(160);
			col.setHidable(true);
			col.setExpandRatio(1);
			col.setHidingToggleCaption(col.getCaption());
			HeaderCell cell = filterRow.getCell(col.getId());

			// Have an input field to use for filter
			TextField filterField = new TextField();
			filterField.setWidth(130, Unit.PIXELS);
			filterField.setHeight(30, Unit.PIXELS);

			filterField.addValueChangeListener(change -> {
				String searchVal = change.getValue();
				String colId = col.getId();

				log.info("Value change in col [{}], val=[{}]", colId, searchVal);
				ListDataProvider<PromoEvent> dataProvider = (ListDataProvider<PromoEvent>) eventGrid.getDataProvider();

				if (!filterField.isEmpty()) {
					try {
						// note: if we keep typing into same textfield, we will overwrite the old filter
						// for this column, which is desirable (rather than having filters for "h",
						// "he", "hel", etc
						gridFilters.put(colId, FilterFactory.getFilterForPromoEvent(colId, searchVal));
						log.info("updated filter on attribute [{}]", colId);

					} catch (Exception e) {
						log.info("ignoring val=[{}], col=[{}] is invalid", searchVal, colId);
					}
				} else {
					// the filter field was empty, so try
					// removing the filter associated with this col
					gridFilters.remove(colId);
					log.info("removed filter on attribute [{}]", colId);

				}
				dataProvider.clearFilters();
				for (String colFilter : gridFilters.keySet()) {
					ProviderAndPredicate paf = gridFilters.get(colFilter);
					dataProvider.addFilter(paf.provider, paf.predicate);
				}
				dataProvider.refreshAll();
			});
			cell.setComponent(filterField);

		}

		this.addComponent(eventGrid);

		createEventButton.addClickListener(event -> {
			getUI();
			UI.getCurrent().addWindow(getSubwindow(new PromoEvent()));
		});

		editEventButton.addClickListener(event -> {
			if (canEditEvent(selectedEvent)) {
				getUI();
				UI.getCurrent().addWindow(getSubwindow(selectedEvent));
			}
		});
	}

	/**
	 * Check whether a promo event is editable or not based on offering start date
	 * and current date
	 * 
	 * @param event
	 *            The promoevent
	 * @return Whether it is editable
	 */
	public boolean canEditEvent(PromoEvent event) {
		if (event == null)
			return false;

		Date today = Date.from(Instant.now());
		Date triggerDate = event.getTriggerDate();

		if (today.after(triggerDate)) {
			if (Page.getCurrent() != null) // mockito
				NotificationFactory
						.getTopBarWarningNotification(
								"It's too late to edit this promotion, it triggered on: " + triggerDate, 5)
						.show(Page.getCurrent());
			return false;
		}
		return true;
	}

	/**
	 * Gets the popup window for creating/editing PromoEvents
	 * 
	 * @param promoEvent
	 *            A transient or detached PromoEvent object
	 * @return The window
	 */
	public Window getSubwindow(PromoEvent promoEvent) {
		Window subwindow;

		// Creating the confirm button
		confirmButton = new Button("Confirm");
		confirmButton.setId("confirm_event");

		offering = new ComboBox<Offering>("Offering");
		triggerDate = Utils.getDateTimeFieldWithOurLocale("Trigger Date");
		discountMultiplier = new TextField("Discount Multiplier");
		maxReservationsPerCustomer = new TextField("Max Reservations/Customer");
		promoCode = new TextField("Promo Code");
		promoCodeUses = new TextField("Promo Code Max Use Count");
		customMessage = new TextArea("Message");

		offering.setPopupWidth(null);

		if (promoEvent.getId() == null) { // passed in an unsaved object
			subwindow = new Window("Create new promotional event");
		} else {
			subwindow = new Window("Edit a promotional event");
		}

		FormLayout form = new FormLayout();
		VerticalLayout formContainer = new VerticalLayout();
		formContainer.addComponent(form);

		subwindow.setWidth("800px");
		subwindow.setContent(formContainer);
		subwindow.center();
		subwindow.setClosable(false);
		subwindow.setModal(true);
		subwindow.setResizable(false);
		subwindow.setDraggable(false);

		offering.setWidth(300, Unit.PIXELS);
		triggerDate.setWidth(300, Unit.PIXELS);
		discountMultiplier.setWidth(300, Unit.PIXELS);
		maxReservationsPerCustomer.setWidth(300, Unit.PIXELS);
		promoCode.setWidth(300, Unit.PIXELS);
		promoCodeUses.setWidth(300, Unit.PIXELS);
		customMessage.setWidth(300, Unit.PIXELS);

		form.addComponent(offering);
		form.addComponent(triggerDate);
		form.addComponent(discountMultiplier);
		form.addComponent(maxReservationsPerCustomer);
		form.addComponent(promoCode);
		form.addComponent(promoCodeUses);
		form.addComponent(customMessage);

		if (promoEvent.getId() != null) {
			// Old promo code cannot be changed to make our life easier
			promoCode.setReadOnly(true);
		}

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirmButton);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		form.addComponent(buttonActions);

		if (offeringRepo.findAll() != null) // mockito
			offering.setItems(Utils.iterableToCollection(offeringRepo.findAll()).stream()
					.sorted((c1, c2) -> c1.getId().compareTo(c2.getId())));

		Binder<PromoEvent> binder = new Binder<PromoEvent>();

		binder.forField(offering).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getOfferingStillOpenValidator())
				.bind(PromoEvent::getOffering, PromoEvent::setOffering);

		binder.forField(triggerDate).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getLocalDateTimeToUtilDateTimeConverter())
				.withValidator(ValidatorFactory.getDateIsEarlierThanOfferingLastEditableDateValidator(offering))
				.withValidator(ValidatorFactory.getDateNotEarlierThanValidator(Date.from(Instant.now())))
				.bind(PromoEvent::getTriggerDate, PromoEvent::setTriggerDate);

		binder.forField(discountMultiplier).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToDoubleConverter())
				.withValidator(ValidatorFactory.getDoubleRangeValidator(0, 1))
				.bind(PromoEvent::getDiscount, PromoEvent::setDiscount);

		binder.forField(maxReservationsPerCustomer).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(1))
				.bind(PromoEvent::getMaxReservationsPerCustomer, PromoEvent::setMaxReservationsPerCustomer);

		// For old promo event, the code cannot be changed to make our life easier
		binder.forField(promoCode).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.withValidator(ValidatorFactory.getStringNotEqualsToIgnoreCaseValidator("none"))
				.bind(PromoEvent::getPromoCode, PromoEvent::setPromoCode);

		binder.forField(promoCodeUses).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(1))
				.bind(PromoEvent::getPromoCodeUsesLeft, PromoEvent::setPromoCodeUsesLeft);

		binder.forField(customMessage).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(PromoEvent::getCustomMessage, PromoEvent::setCustomMessage);

		binder.setBean(promoEvent);

		confirmButton.addClickListener(event -> {
			validationStatus = binder.validate();

			String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(promoEvent);
				log.info("About to save promo event [{}]", promoEvent);

				if (promoEvent.getId() == null
						&& promoEventRepo.findOneByPromoCode(promoEvent.getPromoCode()) != null) {
					errors += "Promo code already used!\n";
				} else {
					promoEventRepo.save(promoEvent);
					if (promoEvent.getId() == null) {
						log.info("Saved a new promo event [{}] successfully", promoEvent);
					} else {
						log.info("Saved an edited booking [{}] successfully", promoEvent);
					}

					binder.removeBean();
					this.refreshData();
					subwindow.close();
					if (Page.getCurrent() != null) // mockito
						NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

					return; // This return skip the error reporting procedure below
				}
			}
			if (Page.getCurrent() != null) // mockito
				NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());

		});

		return subwindow;
	}

	/**
	 * Refreshes the data in the vaadin grid
	 */
	public void refreshData() {

		if (promoEventRepo.findAll() != null) {// mockito
			ListDataProvider<PromoEvent> provider = new ListDataProvider<>(
					Utils.iterableToCollection(promoEventRepo.findAll()));
			eventGrid.setDataProvider(provider);
		}
	}

	/**
	 * @return the offering field
	 */
	public ComboBox<Offering> getOffering() {
		return offering;
	}

	/**
	 * @return the triggerDate field
	 */
	public DateTimeField getTriggerDate() {
		return triggerDate;
	}

	/**
	 * @return the discountMultiplier field
	 */
	public TextField getDiscountMultiplier() {
		return discountMultiplier;
	}

	/**
	 * @return the maxReservationsPerCustomer field
	 */
	public TextField getMaxReservationsPerCustomer() {
		return maxReservationsPerCustomer;
	}

	/**
	 * @return the promoCode field
	 */
	public TextField getPromoCode() {
		return promoCode;
	}

	/**
	 * @return the promoCodeUses field
	 */
	public TextField getPromoCodeUses() {
		return promoCodeUses;
	}

	/**
	 * @return the customMessage field
	 */
	public TextArea getCustomMessage() {
		return customMessage;
	}

	/**
	 * @return the confirmButton
	 */
	public Button getConfirmButton() {
		return confirmButton;
	}

	/**
	 * @return the validationStatus
	 */
	public BinderValidationStatus<PromoEvent> getValidationStatus() {
		return validationStatus;
	}

}
