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
import com.vaadin.server.Sizeable.Unit;
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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;

import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.GridCol;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.model.Tour;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.PromoEventRepository;
import comp3111.input.converters.ConverterFactory;
import comp3111.input.validators.ValidatorFactory;
import comp3111.view.NotificationFactory;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
public class PromoEventEditor extends VerticalLayout {
	private static final Logger log = LoggerFactory.getLogger(PromoEventEditor.class);

	private Grid<PromoEvent> eventGrid = new Grid<>(PromoEvent.class);

	private PromoEvent selectedEvent;

	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private PromoEventRepository promoEventRepo;
	@Autowired
	private DBManager actionManager;
	private final HashMap<String, ProviderAndPredicate<?, ?>> gridFilters = new HashMap<String, ProviderAndPredicate<?, ?>>();


	@Autowired
	public PromoEventEditor(PromoEventRepository per) {
		this.promoEventRepo = per;

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
		
		eventGrid.removeColumn(GridCol.PROMOEVENT_TRIGGER_DATE); 
		eventGrid.removeColumn(GridCol.PROMOEVENT_OFFERING);
		
		eventGrid.setColumnOrder(GridCol.PROMOEVENT_ID, GridCol.PROMOEVENT_OFFERING_ID, 
				GridCol.PROMOEVENT_CUSTOM_MESSAGE, GridCol.PROMOEVENT_MAX_RESERVATIONS_PER_CUSTOMER,
				GridCol.PROMOEVENT_PROMO_CODE, GridCol.PROMOEVENT_PROMO_CODE_USES_LEFT, 
				GridCol.PROMOEVENT_DISCOUNT, GridCol.PROMOEVENT_TRIGGER_DATE_STRING);

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
			getUI().getCurrent().addWindow(getSubwindow(new PromoEvent()));
		});

		editEventButton.addClickListener(event -> {
			if (canEditEvent(selectedEvent)) {
				getUI().getCurrent().addWindow(getSubwindow(selectedEvent));
			}
		});
	}

	// Check whether a promo event is editable or not based on offering start date
	// and current date
	private boolean canEditEvent(PromoEvent event) {
		if (event == null)
			return true;

		Date today = Date.from(Instant.now());
		Date triggerDate = event.getTriggerDate();

		if (today.after(triggerDate)) {
			NotificationFactory
					.getTopBarWarningNotification(
							"It's too late to edit this event, it the promotional event triggered on" + triggerDate, 5)
					.show(Page.getCurrent());
			return false;
		}
		return true;
	}

	private Window getSubwindow(PromoEvent promoEvent) {
		Window subwindow;

		// Creating the confirm button
		Button confirmButton = new Button("Confirm");
		confirmButton.setId("confirm_event");

		final ComboBox<Offering> offering = new ComboBox<Offering>("Offering");
		DateTimeField triggerDate = Utils.getDateTimeFieldWithOurLocale("Trigger Date");
		TextField discountMultiplier = new TextField("Discount Multiplier");
		TextField maxReservationsPerCustomer = new TextField("Max Reservations/Customer");
		TextField promoCode = new TextField("Promo Code");
		TextField promoCodeUses = new TextField("Promo Code Max Use Count");
		TextArea customMessage = new TextArea("Message");

		// customer.setId("cb_customer");
		// offering.setId("cb_offering");
		// numberAdults.setId("tf_number_of_adults");
		// numberChildren.setId("tf_number_of_children");
		// numberToddlers.setId("tf_number_of_toddlers");
		// amountPaid.setId("tf_amount_paid");
		// specialRequest.setId("tf_special_request");
		// paymentStatus.setId("cb_payment_status");

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

		form.addComponent(offering);
		form.addComponent(triggerDate);
		form.addComponent(discountMultiplier);
		form.addComponent(maxReservationsPerCustomer);
		form.addComponent(promoCode);
		form.addComponent(promoCodeUses);
		form.addComponent(customMessage);

		if (promoEvent.getId() != null) {
			//Old promo code cannot be changed to make our life easier
			promoCode.setReadOnly(true);
		}

		HorizontalLayout buttonActions = new HorizontalLayout();
		buttonActions.addComponent(confirmButton);
		buttonActions.addComponent(new Button("Cancel", event -> subwindow.close()));
		form.addComponent(buttonActions);

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
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(PromoEvent::getMaxReservationsPerCustomer, PromoEvent::setMaxReservationsPerCustomer);

		//For old promo event, the code cannot be changed to make our life easier
		binder.forField(promoCode).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(PromoEvent::getPromoCode, PromoEvent::setPromoCode);

		binder.forField(promoCodeUses).asRequired(Utils.generateRequiredError())
				.withConverter(ConverterFactory.getStringToIntegerConverter())
				.withValidator(ValidatorFactory.getIntegerRangeValidator(0))
				.bind(PromoEvent::getPromoCodeUsesLeft, PromoEvent::setPromoCodeUsesLeft);

		binder.forField(customMessage).asRequired(Utils.generateRequiredError())
				.withValidator(ValidatorFactory.getStringLengthValidator(255))
				.bind(PromoEvent::getCustomMessage, PromoEvent::setCustomMessage);

		binder.setBean(promoEvent);

		confirmButton.addClickListener(event -> {
			BinderValidationStatus<PromoEvent> validationStatus = binder.validate();

			String errors = ValidatorFactory.getValidatorErrorsString(validationStatus);

			if (validationStatus.isOk()) {
				binder.writeBeanIfValid(promoEvent);
				log.info("About to save promo event [{}]", promoEvent);

				if (promoEvent.getId() == null && promoEventRepo.findOneByPromoCode(promoEvent.getPromoCode()) != null) {
					errors += "Promo code already used!\n";
				}else{
					promoEventRepo.save(promoEvent);
					if (promoEvent.getId() == null) {
						log.info("Saved a new promo event [{}] successfully", promoEvent);
					} else {
						log.info("Saved an edited booking [{}] successfully", promoEvent);
					}


					binder.removeBean();
					this.refreshData();
					subwindow.close();
					NotificationFactory.getTopBarSuccessNotification().show(Page.getCurrent());

					return; // This return skip the error reporting procedure below
				}
			}
			NotificationFactory.getTopBarWarningNotification(errors, 5).show(Page.getCurrent());

		});

		return subwindow;
	}

	public void refreshData() {

		ListDataProvider<PromoEvent> provider = new ListDataProvider<>(
				Utils.iterableToCollection(promoEventRepo.findAll()));
		eventGrid.setDataProvider(provider);
	}

}
