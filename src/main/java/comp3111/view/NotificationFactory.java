package comp3111.view;

import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.shared.Position;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

public class NotificationFactory {
	public static Notification getTopBarWarningNotification(String subText, int s) {
		return getNotification("Error!", subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_WARNING_MESSAGE);
	}

	public static Notification getTopBarNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_WARNING_MESSAGE);
	}

	public static Notification getCenteredWarningNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED,
				Notification.TYPE_WARNING_MESSAGE);
	}

	public static Notification getTopBarErrorNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_ERROR_MESSAGE);
	}

	public static Notification getTopBarSuccessNotification() {
		return getNotification("Success!", null, 3, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}

	public static Notification getTopBarSuccessNotification(String caption) {
		return getNotification("Success!", caption, 3, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}
	
	private static Notification getNotification(String mainText, String subText, int s, Position pos, Type type) {
		Notification notif = new Notification(mainText, subText, type);
		notif.setDelayMsec(s * 1000);
		notif.setPosition(pos);
		return notif;
	}


}
