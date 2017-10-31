package comp3111;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import comp3111.data.model.Booking;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;
import comp3111.view.CustomerEngagementView;

@Component
public class LineMessenger {
	private static final String AUTH_TOKEN_LINE = "Bearer H5M2+yupl+owBVJunHjV6z3NVMWJ51vRa2j8NcVgpQ0NwSoR2xGvMlGq+mD4NwZ8JHDOAUbM8ss+BKUPMQIXLYXazbSLZvH5qzqmOEi3Khvg/71Ye90O4DIGsnOJ0JJVSewSzBNMS3VYAQARZUE39QdB04t89/1O/w1cDnyilFU=";

	private static final Logger log = LoggerFactory.getLogger(CustomerEngagementView.class);

	@Autowired
	private CustomerRepository cRepo;

	@Autowired
	private OfferingRepository oRepo;

	@Autowired
	private BookingRepository bRepo;

	/**
	 * @param custLineId
	 *            The customer's line ID (not the human readable one, but the one
	 *            used internally by LINE's push API)
	 * @param text
	 *            the content to send them
	 * @return true or false, depending on whether the message was sent successfully
	 *         (200 OK)
	 */
	public boolean sendToUser(String custLineId, String text) {
		log.info("\tsending [{}]'s lind id: [{}], [{}]", text, custLineId, cRepo.findOneByLineId(custLineId));
		JSONObject body = null;
		try {
			body = new JSONObject();
			body.put("to", custLineId);

			JSONObject msg1 = new JSONObject();
			msg1.put("type", "text");
			msg1.put("text", text);

			// JSONObject msg2 = new JSONObject();
			// msg2.put("type", "text");
			// msg2.put("text", "goodbye!");

			JSONArray msgArr = new JSONArray();
			msgArr.put(msg1);
			// msgArr.put(msg2);

			body.put("messages", msgArr);

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			log.info(e1.getMessage());
			return false;
		}

		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost postRequest = new HttpPost("https://api.line.me/v2/bot/message/push");
			log.info("json data: [{}]", body.toString());
			StringEntity params = new StringEntity(body.toString());

			postRequest.addHeader("Content-Type", "application/json");
			postRequest.addHeader("Authorization", AUTH_TOKEN_LINE);
			postRequest.setEntity(params);

			HttpResponse response = client.execute(postRequest);
			log.info("send message status: " + response.getStatusLine().getStatusCode() + " "
					+ response.getStatusLine().getReasonPhrase() + "\n" + response.getEntity().toString());

			return response.getStatusLine().getStatusCode() == 200;
			// handle response here...
		} catch (Exception ex) {
			// handle exception here
			log.info(ex.getMessage());
			return false;
		}

	}

	/**
	 * @param o
	 *            The offering whose customers you would like to notify
	 * @param text
	 *            The text to send them
	 * @return True or false, depending on whether all customers in this offering
	 *         were notified successfully. Note that for customers who don't have a
	 *         line ID (i.e. walk in customers), we ignore them
	 */
	public boolean sendToOffering(Offering o, String text) {
		log.info("\tBroadcasting to all users who booked for offering [{}]", o);
		log.info("there are [{}] booked customers", bRepo.findByOffering(o).size());
		for (Booking record : bRepo.findByOffering(o)) {
			if (record.getCustomer().getLineId() != null && !record.getCustomer().getLineId().isEmpty()) {
				if (!sendToUser(record.getCustomer().getLineId(), text))
					return false;
			} else {
				log.info("customer [{}] doesn't have a line id, so nothing was sent to them",
						record.getCustomer().getName());
			}
		}
		log.info("Succesfully sent to participants in this offering [{}]", o);
		return true;
	}

	/**
	 * @param t
	 *            The tour whos participants (which span many offerings potentially)
	 *            you want to reach
	 * @param text
	 *            The text to send them
	 * @return True or false, depending on whether all associated users successfully
	 *         recieved the message
	 */
	public boolean sendToTour(Tour t, String text) {
		log.info("Broadcasting to all users who booked for offerings in the tour [{}]", t);

		oRepo.findByTour(t);

		for (Offering o : oRepo.findByTour(t)) {
			if (!sendToOffering(o, text))
				return false;
		}
		log.info("Succesfully sent to participants in this tour [{}]", t.getTourName());
		return true;
	}
}
