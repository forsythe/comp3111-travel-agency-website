package comp3111.field;

import com.vaadin.ui.*;

public class HKIDEntryField extends CustomField<String> {
	private final TextField mainPart = new TextField();
	private final TextField checkDigit = new TextField();

	public HKIDEntryField(){
	}


	public HKIDEntryField(String caption){
		this.setCaption(caption);
	}

	@Override
	protected Component initContent(){
		HorizontalLayout layout = new HorizontalLayout();
		checkDigit.setWidth("50px");

		//Sample format: 852-12345678 <--- bad comment
		layout.addComponent(mainPart);
		layout.addComponent(new Label("("));
		layout.addComponent(checkDigit);
		layout.addComponent(new Label(")"));

		return layout;
	}

	@Override
	public boolean isEmpty(){
		return mainPart.isEmpty() || checkDigit.isEmpty();
	}

	@Override
	public String getValue(){
		return mainPart.getValue() + "(" + checkDigit.getValue() + ")";
	}

	@Override
    protected void doSetValue(String value) {
		if (value != null) {
			int pos = value.indexOf("(");
			if (pos != -1 && value.length() > pos + 1){
				mainPart.setValue(value.substring(0, pos));
				checkDigit.setValue(value.substring(pos+1));
			}
		}

		mainPart.setValue("");
		checkDigit.setValue("");
    }

	@Override
	public void setValue(String value) {
		doSetValue(value);
	}
}
