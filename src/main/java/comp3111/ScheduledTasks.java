package comp3111;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;

@Component
public class ScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private BookingRepository bookingRepo;
	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private LineMessenger lineMessenger;

	@Scheduled(fixedRate = 5000)
	public void updatePendingOfferingStatusIfNecessary() {
		boolean offeringConfirmed = false;
		Date now = new Date();
		log.info("The time is now [{}], checking if any offerings need updating (in terms of status)",
				Utils.simpleDateFormat(now));

		for (Offering o : offeringRepo.findByStatus(Offering.STATUS_PENDING)) {

			log.info("Offering [{}] still has time left, not updating its status yet...");
			if (o.getStatus().equals(Offering.STATUS_PENDING) && now.after(Utils.addDate(o.getStartDate(), -3))) {
				log.info("Offering [{}] has reached t=-3 days before start!");
				int totalCustomers = 0;
				for (Booking b : bookingRepo.findByOffering(o)) {
					totalCustomers += b.getNumAdults() + b.getNumChildren() + b.getNumToddlers();
				}
				if (totalCustomers >= o.getMinCustomers()) {
					log.info("Success! Offering [{}] can be offered, having [{}] of [{}] minimum customers", o,
							totalCustomers, o.getMinCustomers());
					o.setStatus(Offering.STATUS_CONFIRMED);
					o = offeringRepo.save(o);
					offeringConfirmed = true;
				} else {
					log.info("Fail! Offering [{}] cannot be offered, having only [{}] of [{}] minimum customers", o,
							totalCustomers, o.getMinCustomers());
					o.setStatus(Offering.STATUS_CANCELLED);
					o = offeringRepo.save(o);
				}
			}
			for (Booking b : bookingRepo.findByOffering(o)) {
				Customer recipient = b.getCustomer();
				String msg = String.format("Dear %s, offering %s has been %s!", recipient.getName(), o,
						offeringConfirmed ? "confirmed" : "cancelled");
				boolean deliveryResult = lineMessenger.sendToOffering(o, msg);
				log.info("delivery result to [{}] customers is [{}]", LineMessenger.getAndResetCount(), deliveryResult);
				if (!offeringConfirmed) {
					b.setPaymentStatus(Booking.PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED);
					b = bookingRepo.save(b);
				}
			}

		}

	}
}