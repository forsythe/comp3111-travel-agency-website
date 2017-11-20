package comp3111;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourGuideRepository;
import comp3111.data.repo.TourRepository;

/**
 * Spring Application point of entry
 * 
 * @author Forsythe
 *
 */
@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	// Don't want scheduling on test cases, or else it causes race conditions!
	// Annotate all SPRING test classes with @SpringBootTest(properties =
	// "scheduling.enabled=false")
	// Non-spring tests don't need this annotation.
	@Configuration
	@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
	@EnableScheduling
	static class SchedulingConfiguration {

	}

	@Bean
	public CommandLineRunner loadData(TourRepository tourRepo, OfferingRepository offerRepo, TourGuideRepository tgRepo,
			CustomerRepository customerRepo, BookingRepository bookingRepo, DBManager dbManager) {
		return (args) -> {
			dbManager.deleteAll();
			log.info("Populating the db...");

			TourGuide kim = new TourGuide("Kim", "tensorflowboss");
			kim = tgRepo.save(kim);
			TourGuide kevin = new TourGuide("Kevin", "springboss");
			kevin = tgRepo.save(kevin);

			Customer heng = new Customer("Heng", "Uc8f613f85e41d93ed9ffa228188466d1", "123-12311", 20, "A123456(3)");
			Customer kv = new Customer("KV", "U7e5b42b4ea64a1ff1d812a3ff33b48b0", "123-12311", 20, "A123456(3)");
			Customer rex = new Customer("Rex", "U8b20aa2040714d4ff45782709f7b1ba6", "123-12311", 20, "A123456(3)");
			Customer kris = new Customer("Kris", "U4b23c4647091ccdcce12d5392d37866d", "123-12311", 20, "A123456(3)");
			heng = customerRepo.save(heng);
			kv = customerRepo.save(kv);
			rex = customerRepo.save(rex);
			kris = customerRepo.save(kris);

			Tour lg7 = new Tour("LG7", "Bad food", 2, 0.8, 0.2, 200, 250);
			lg7.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
			lg7 = tourRepo.save(lg7);

			Offering offer = new Offering(lg7, kim, Utils.addDate(new Date(), 5), "Hotel California", 15, 50,
					Offering.STATUS_PENDING);
			offer = offerRepo.save(offer);

			Booking book = new Booking(kris, offer, 2, 1, 0, 0, "No cockroaches in my food", Booking.PAYMENT_PENDING);
			book = bookingRepo.save(book);

			Offering offer2 = new Offering(lg7, kevin, Utils.addDate(new Date(), 8), "Hotel Waldo", 15, 50,
					Offering.STATUS_PENDING);
			offer2 = offerRepo.save(offer2);
			Booking book2 = new Booking(heng, offer2, 2, 1, 0, 0, "Non smoking room", Booking.PAYMENT_PENDING);
			book2 = bookingRepo.save(book2);

		};
	}

}
