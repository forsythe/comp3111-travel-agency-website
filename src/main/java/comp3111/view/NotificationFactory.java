package comp3111.view;

import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * A factory that generates the notifications
 * 
 * @author kristiansuhartono
 *
 */
public class NotificationFactory {

	/**
	 * Constructs a yellow (warning style) notification that peeks from the top
	 * 
	 * @param subText
	 *            The subtitle text
	 * @param s
	 *            How many seconds to display
	 * @return The notification object
	 */
	public static Notification getTopBarWarningNotification(String subText, int s) {
		return getNotification("Error!", subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_WARNING_MESSAGE);
	}

	/**
	 * Constructs a yellow (warning style) notification that peeks from the top
	 * 
	 * @param mainText
	 *            The main text of the notification
	 * @param subText
	 *            The subtitle text
	 * @param s
	 *            How many seconds to display
	 * @return The notification object
	 */
	public static Notification getTopBarWarningNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_WARNING_MESSAGE);
	}

	/**
	 * Constructs a yellow (warning style) notification that floats in the center of
	 * the screen
	 * 
	 * @param mainText
	 *            The main text of the notification
	 * @param subText
	 *            The subtitle text
	 * @param s
	 *            How many seconds to display
	 * @return The notification object
	 */
	public static Notification getCenteredWarningNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED, Notification.TYPE_WARNING_MESSAGE);
	}

	/**
	 * Constructs a red (error style) notification that peeks from the top
	 * 
	 * @param mainText
	 *            The main text of the notification
	 * @param subText
	 *            The subtitle text
	 * @param s
	 *            How many seconds to display
	 * @return The notification object
	 */
	public static Notification getTopBarErrorNotification(String mainText, String subText, int s) {
		return getNotification(mainText, subText, s, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_ERROR_MESSAGE);
	}

	/**
	 * Constructs a normal success notification that peeks from the top for 3
	 * seconds
	 * 
	 * @return The notification object
	 */
	public static Notification getTopBarSuccessNotification() {
		return getNotification("Success!", null, 3, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}

	/**
	 * Constructs a normal success notification that peeks from the top for 3
	 * seconds
	 * 
	 * @param caption
	 *            A custom caption
	 * @return The notification object
	 */
	public static Notification getTopBarSuccessNotification(String caption) {
		return getNotification("Success!", caption, 3, Notification.POSITION_CENTERED_TOP,
				Notification.TYPE_HUMANIZED_MESSAGE);
	}

	/**
	 * Constructs a notification object
	 * 
	 * @param mainText
	 *            The main text
	 * @param subText
	 *            The subtitle text
	 * @param s
	 *            How many seconds to show
	 * @param pos
	 *            The position to place
	 * @param type
	 *            The style of the notification
	 * @return The notification
	 */
	public static Notification getNotification(String mainText, String subText, int s, Position pos, Type type) {
		Notification notif = new Notification(mainText, subText, type);
		notif.setDelayMsec(s * 1000);
		notif.setPosition(pos);
		return notif;
	}

}
