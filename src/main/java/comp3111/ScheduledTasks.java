package comp3111;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.PromoEventRepository;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private PromoEventRepository promoEventRepo;

	@Autowired
	private DBManager actionManager;
	@Autowired
	private LineMessenger lineMessenger;

	public static final String EVERYDAY_8_AM = "0 0 8 * * *";
	public static final String EVERY_10_SECONDS = "*/10 * * * * *";

	@Scheduled(cron = EVERYDAY_8_AM)
	public void updatePendingOfferingStatusIfNecessary() {
		LineMessenger.resetCounter();
		Date now = new Date();
		log.info("The time is now [{}], checking if any offerings need updating (in terms of status)",
				Utils.simpleDateFormat(now));

		for (Offering o : offeringRepo.findByStatus(Offering.STATUS_PENDING)) {

			// if now >= start-3
			if (o.getStatus().equals(Offering.STATUS_PENDING) && now.after(Utils.addDate(o.getStartDate(), -3))) {
				log.info("Offering [{}] has reached t=-3 days before start!", o);

				int totalCustomers = actionManager.countNumberOfPaidPeopleInOffering(o);

				if (totalCustomers >= o.getMinCustomers()) {
					log.info(
							"Success! Offering [{}] can be offered, having [{}] of [{}] minimum confirmed payment customers",
							o, totalCustomers, o.getMinCustomers());
					actionManager.notifyOfferingStatus(o, true);
				} else {
					log.info("Fail! Offering [{}] cannot be offered, having only [{}] of [{}] minimum customers", o,
							totalCustomers, o.getMinCustomers());
					actionManager.notifyOfferingStatus(o, false);
				}
			} else {
				log.info("Offering [{}] still has time left, not updating its status yet...", o);
			}
		}
		log.info("[{}] people were notified", LineMessenger.getCounter());
	}

	@Scheduled(cron = EVERY_10_SECONDS)
	public void updatePendingPromotionalBroadcasts() {
		LineMessenger.resetCounter();
		Date now = new Date();
		log.info("The time is now [{}], checking if any promoevents are overdue", Utils.simpleDateFormat(now));

		for (PromoEvent p : promoEventRepo.findAllByIsTriggered(false)) {

			if (now.after(p.getTriggerDate())) {
				log.info("Time to trigger promoevent [{}]", p.getId());
				lineMessenger.sendToAll(p.getCustomMessage());
			}
			p.setTriggered(true);
		}
		log.info("[{}] people were notified", LineMessenger.getCounter());

	}
}