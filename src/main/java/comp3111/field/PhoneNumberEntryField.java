package comp3111.field;

import com.vaadin.ui.*;

public class PhoneNumberEntryField extends CustomField<String> {
	private final TextField countryCode = new TextField();
	private final TextField phoneNumber = new TextField();

	/**
	 * Construct without Caption nor default country code.
	 */
	public PhoneNumberEntryField(){

	}

	/**
	 * Construct with caption
	 * @param caption: Caption text
	 */
	public PhoneNumberEntryField(String caption){
		this.setCaption(caption);
	}

	/**
	 * Construct with caption and default country code
	 * @param caption: Caption text
	 * @param defaultCountryCode: Country code, e.g. 852
	 */
	public PhoneNumberEntryField(String caption, String defaultCountryCode){
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
	public boolean isEmpty(){
		//One can live without country code
		return phoneNumber.isEmpty();
	}

	@Override
	public String getValue(){
		if (countryCode.isEmpty()){
			return phoneNumber.getValue();
		}else {
			if (phoneNumber.isEmpty()) {
				return "";
			}else {
				return countryCode.getValue() + "-" + phoneNumber.getValue();
			}
		}
	}

	@Override
    protected void doSetValue(String value) {
		if (value != null) {
			String[] parts = value.split("-");
			countryCode.setValue(parts[0]);
			if (parts.length >= 2) {
				phoneNumber.setValue(parts[1]);
			}
		}else{
			countryCode.setValue("");
			phoneNumber.setValue("");
		}
    }

	@Override
	public void setValue(String value) {
		doSetValue(value);
	}
}
