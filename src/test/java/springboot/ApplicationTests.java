package springboot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.Application;
import comp3111.model.*;
import comp3111.repo.*;

import static org.assertj.core.api.BDDAssertions.*;

import java.time.DayOfWeek;
import java.util.Arrays;

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

	@Before
	public void setup() {
		this.customerRepo.deleteAll();
		this.customerOfferingRepo.deleteAll();
		this.limitedTourRepo.deleteAll();
		//this.loginUserRepo.deleteAll();
		this.offeringRepo.deleteAll();
		this.repeatingTourRepo.deleteAll();
		this.tourGuideRepo.deleteAll();

		Customer peppaPig = new Customer("Peppa Pig", 35);
		Customer emilyElephant = new Customer("Emily Elephant", 36);

		RepeatingTour shimen = new RepeatingTour("Shimen Forest", "Colorful ponds", 2, null, 0.8, 0, 499, 599,
				Arrays.asList(DayOfWeek.MONDAY, DayOfWeek.TUESDAY));
		
		this.customerRepo.save(peppaPig);
		this.customerRepo.save(emilyElephant);
		this.repeatingTourRepo.save(shimen);

		// this.customerRepo.save(new Customer("Jack", 50));
		// this.customerRepo.save(new Customer("Chloe", 20));
		// this.customerRepo.save(new Customer("Kim", 30));
		// this.customerRepo.save(new Customer("Kim", 34));
		// this.customerRepo.save(new Customer("Michelle", 9));
		//
		// this.loginUserRepo.deleteAll();
		//loginUserRepo.save(new LoginUser("admin", "password"));

	}

	@Test
	public void shouldHaveOneLoginUser() {
		then(this.loginUserRepo.count()).isEqualTo(1);
	}

}
