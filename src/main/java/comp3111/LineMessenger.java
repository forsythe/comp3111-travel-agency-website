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

import com.vaadin.ui.Notification;

import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;
import comp3111.view.CustomerEngagementView;

@Component
public class LineMessenger {
	private static final String AUTH_TOKEN_LINE = "Bearer H5M2+yupl+owBVJunHjV6z3NVMWJ51vRa2j8NcVgpQ0NwSoR2xGvMlGq+mD4NwZ8JHDOAUbM8ss+BKUPMQIXLYXazbSLZvH5qzqmOEi3Khvg/71Ye90O4DIGsnOJ0JJVSewSzBNMS3VYAQARZUE39QdB04t89/1O/w1cDnyilFU=";

	private static final Logger log = LoggerFactory.getLogger(CustomerEngagementView.class);

	@Autowired
	private CustomerRepository cRepo;

	public boolean sendText(String custLineId, String text) {

		log.info("sending [{}]'s lind id: [{}], [{}]", text, custLineId, cRepo.findOneByLineId(custLineId));
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
}
