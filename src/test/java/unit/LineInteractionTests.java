package unit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.Application;
import comp3111.LineMessenger;
import comp3111.data.DBManager;
import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.LoginUserRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class LineInteractionTests {

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private LineMessenger lineMessenger;
	@Autowired
	private LoginUserRepository loginUserRepo;
	@Autowired
	private OfferingRepository offeringRepo;

	private NonFAQQueryRepository nonFAQQuery;
	@Autowired
	private DBManager actionManager;

	@Test
	public void testSuccessSendMessageToHengsPhone() {
		Customer heng = new Customer("heng", "U6934790c40beeed33b8b89fa359aa9cf", "12312341234", 20, "A1234563");
		heng = customerRepo.save(heng);
		boolean recieved200ok = lineMessenger.sendToUser(heng.getLineId(), "hi heng's phone");
		assertTrue(recieved200ok);
		assertEquals(LineMessenger.getAndResetCount(), 1);
		customerRepo.delete(heng);

	}

}
