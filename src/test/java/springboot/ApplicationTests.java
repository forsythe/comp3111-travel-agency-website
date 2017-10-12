package springboot;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.transaction.Transactional;

import org.hibernate.AnnotationException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.ActionManager;
import comp3111.Application;
import comp3111.exceptions.OfferingDateUnsupportedException;
import comp3111.exceptions.OfferingDayOfWeekUnsupportedException;
import comp3111.exceptions.UsernameTakenException;
import comp3111.model.Customer;
import comp3111.model.LimitedTour;
import comp3111.model.Offering;
import comp3111.model.RepeatingTour;
import comp3111.model.TourGuide;
import comp3111.repo.CustomerOfferingRepository;
import comp3111.repo.CustomerRepository;
import comp3111.repo.LimitedTourRepository;
import comp3111.repo.LoginUserRepository;
import comp3111.repo.OfferingRepository;
import comp3111.repo.RepeatingTourRepository;
import comp3111.repo.TourGuideRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private CustomerOfferingRepository customerOfferingRepo;
	@Autowired
	private LimitedTourRepository limitedTourRepo;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;
	@Autowired
	private RepeatingTourRepository repeatingTourRepo;
	@Autowired
	private TourGuideRepository tourGuideRepo;
	@Autowired
	private ActionManager actionManager;

	@Autowired
	SessionFactory sessionFactory;
	
	@Before
	public void setup() {
		Customer c = new Customer("PLS WORK", 10);
		Session curSession = sessionFactory.getCurrentSession();
		curSession.saveOrUpdate(c);
		
//		// actionManager.deleteAll();
//
//		Customer peppaPig = new Customer("Peppa Pig", 35);
//		//Customer emilyElephant = new Customer("Emily Elephant", 36);
//		
//		RepeatingTour shimenTour = new RepeatingTour("Shimen Forest", "Colorful ponds", 2, 0.8, 0, 499, 599,
//				Arrays.asList(Calendar.MONDAY, Calendar.SUNDAY));
//		LimitedTour yangshanTour = new LimitedTour("Yangshan", "Many hotsprings", 3, 0.8, 0, 699, 799,
//				Arrays.asList(new GregorianCalendar(2017, Calendar.DECEMBER, 9).getTime(),
//						new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime()));
//
//		TourGuide amber = new TourGuide("Amber", "LINEID123");
//		
//		repeatingTourRepo.save(shimenTour);
//		limitedTourRepo.save(yangshanTour);
//		tourGuideRepo.save(amber);
//		customerRepo.save(peppaPig);
//		
//		try {
//			Offering shimenOffering = actionManager.createOfferingForTour(shimenTour, amber,
//					new GregorianCalendar(2017, Calendar.DECEMBER, 4).getTime(), "Hotel chep", 4, 20);
//			actionManager.createBookingForOffering(shimenOffering, peppaPig, 5, 2, 3, 0, "no smoking",
//					"pending");
//			actionManager.createOfferingForTour(yangshanTour, amber,
//					new GregorianCalendar(2017, Calendar.DECEMBER, 12).getTime(), "hotel expenvi", 4, 20);
//		} catch (OfferingDayOfWeekUnsupportedException | OfferingDateUnsupportedException | UsernameTakenException e) {
//			e.printStackTrace();
//		}

	}

	@Test
	public void shouldHaveOneLoginUser() {
		then(this.loginUserRepo.count()).isEqualTo(1);
	}

}
