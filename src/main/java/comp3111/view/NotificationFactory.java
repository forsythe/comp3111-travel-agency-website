package comp3111.view;

import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * A factory that generates the front end notification elements for displaying notifications on the web page
 * @author kristiansuhartono
 *
 */
public class NotificationFactory {
	
	/**
	 * @param subText The notification text
	 * @param s time delay
	 * @return
	 */
	public static Notification getTopBarWarningNotification(String subText, int s) {
		return getNotification("Error!", subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_WARNING_MESSAGE);
	}
	
	/**
	 * @param subText The notification text
	 * @param s time delay
	 * @return
	 */
	public static Notification getTopBarNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_WARNING_MESSAGE);
	}

	/**
	 * @param subText The notification text
	 * @param s time delay
	 * @return
	 */
	public static Notification getCenteredWarningNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED,
				Notification.TYPE_WARNING_MESSAGE);
	}

	/**
	 * @param subText The notification text
	 * @param s time delay
	 * @return
	 */
	public static Notification getTopBarErrorNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_ERROR_MESSAGE);
	}

	/**
	 * @param subText The notification text
	 * @param s time delay
	 * @return
	 */
	public static Notification getTopBarSuccessNotification() {
		return getNotification("Success!", null, 3, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}

	/**
	 * @param subText The notification text
	 * @param s time delay
	 * @return
	 */
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
