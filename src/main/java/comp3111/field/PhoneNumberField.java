package comp3111.field;

import com.vaadin.ui.CustomField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Label;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Component;

public class PhoneNumberField extends CustomField<String> {
	private final TextField countryCode = new TextField();
	private final TextField phoneNumber = new TextField();

	/**
	 * Construct without Caption nor default country code.
	 */
	public PhoneNumberField(){

	}

	/**
	 * Construct with caption
	 * @param caption: Caption text
	 */
	public PhoneNumberField(String caption){
		this.setCaption(caption);
	}

	/**
	 * Construct with caption and default country code
	 * @param caption: Caption text
	 * @param defaultCountryCode: Country code, e.g. 852
	 */
	public PhoneNumberField(String caption, String defaultCountryCode){
		this.setCaption(caption);
		countryCode.setValue(defaultCountryCode);
	}

	@Override
	protected Component initContent(){
		HorizontalLayout layout = new HorizontalLayout();
		countryCode.setWidth("100px");

		//Sample format: 852-12345678
		layout.addComponent(countryCode);
		layout.addComponent(new Label("-"));
		layout.addComponent(phoneNumber);

		return layout;
	}

	@Override
	public String getValue(){
		return countryCode.getValue() + "-" + phoneNumber.getValue();
	}

	@Override
    protected void doSetValue(String value) {
		String[] parts = value.split("-");
		if (parts.length < 2){
			countryCode.setValue(parts[0]);
		}else{
			countryCode.setValue(parts[0]);
			phoneNumber.setValue(parts[1]);
		}
    }
}
