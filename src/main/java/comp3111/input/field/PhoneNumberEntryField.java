package comp3111.input.field;

import com.vaadin.ui.*;

/**
 * A custom Vaadin entry field for phone numbers
 * 
 * @author Forsythe
 *
 */
public class PhoneNumberEntryField extends CustomField<String> {
	private final TextField countryCode = new TextField();
	private final TextField phoneNumber = new TextField();

	/**
	 * Construct without Caption nor default country code.
	 */
	public PhoneNumberEntryField() {

	}

	/**
	 * Construct with caption
	 * 
	 * @param caption:
	 *            Caption text
	 */
	public PhoneNumberEntryField(String caption) {
		this.setCaption(caption);
	}

	/**
	 * Construct with caption and default country code
	 * 
	 * @param caption:
	 *            Caption text
	 * @param defaultCountryCode:
	 *            Country code, e.g. 852
	 */
	public PhoneNumberEntryField(String caption, String defaultCountryCode) {
		this.setCaption(caption);
		countryCode.setValue(defaultCountryCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent() {
		HorizontalLayout layout = new HorizontalLayout();
		countryCode.setWidth("100px");

		// Sample format: 852-12345678
		layout.addComponent(countryCode);
		layout.addComponent(new Label("-"));
		layout.addComponent(phoneNumber);

		return layout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.HasValue#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// One can live without country code
		return phoneNumber.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.data.HasValue#getValue()
	 */
	@Override
	public String getValue() {
		if (countryCode.isEmpty()) {
			return phoneNumber.getValue();
		} else {
			if (phoneNumber.isEmpty()) {
				return "";
			} else {
				return countryCode.getValue() + "-" + phoneNumber.getValue();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.AbstractField#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(String value) {
		if (value != null) {
			String[] parts = value.split("-");
			countryCode.setValue(parts[0]);
			if (parts.length >= 2) {
				phoneNumber.setValue(parts[1]);
			}
		} else {
			countryCode.setValue("");
			phoneNumber.setValue("");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String value) {
		doSetValue(value);
	}
}
