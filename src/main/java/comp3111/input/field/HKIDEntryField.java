package comp3111.input.field;

import com.vaadin.ui.*;

/**
 * A custom Vaadin entry field for HKID
 * @author Forsythe
 *
 */
public class HKIDEntryField extends CustomField<String> {
	private final TextField mainPart = new TextField();
	private final TextField checkDigit = new TextField();

	public HKIDEntryField(){
	}


	/**
	 * Constructor with caption
	 * @param caption The caption to display beside the input box
	 */
	public HKIDEntryField(String caption){
		this.setCaption(caption);
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.CustomField#initContent()
	 */
	@Override
	protected Component initContent(){
		HorizontalLayout layout = new HorizontalLayout();
		checkDigit.setWidth("50px");

		//Sample format: G123456(A)
		layout.addComponent(mainPart);
		layout.addComponent(new Label("("));
		layout.addComponent(checkDigit);
		layout.addComponent(new Label(")"));

		return layout;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.HasValue#isEmpty()
	 */
	@Override
	public boolean isEmpty(){
		return mainPart.isEmpty() || checkDigit.isEmpty();
	}

	/* (non-Javadoc)
	 * @see com.vaadin.data.HasValue#getValue()
	 */
	@Override
	public String getValue(){
		return mainPart.getValue() + "(" + checkDigit.getValue() + ")";
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#doSetValue(java.lang.Object)
	 */
	@Override
    protected void doSetValue(String value) {
		if (value != null) {
			int pos = value.indexOf("(");
			if (pos != -1 && value.length() > pos + 1){
				mainPart.setValue(value.substring(0, pos));
				checkDigit.setValue(value.substring(pos+1, value.length()-1));
				return;
			}
		}
		mainPart.setValue("");
		checkDigit.setValue("");
    }

	/* (non-Javadoc)
	 * @see com.vaadin.ui.AbstractField#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(String value) {
		doSetValue(value);
	}
}
