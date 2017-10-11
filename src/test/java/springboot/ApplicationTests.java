package springboot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import comp3111.Application;
import comp3111.model.Customer;
import comp3111.model.LoginUser;
import comp3111.repo.CustomerRepository;
import comp3111.repo.LoginUserRepository;

import static org.assertj.core.api.BDDAssertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class ApplicationTests {

	@Autowired
	private CustomerRepository repository;
	
	@Autowired
	private LoginUserRepository lurepo;

	@Before
	public void setup() {
		this.repository.deleteAll();
		this.repository.save(new Customer("Jack", 50));
		this.repository.save(new Customer("Chloe", 20));
		this.repository.save(new Customer("Kim", 30));
		this.repository.save(new Customer("Kim", 34));
		this.repository.save(new Customer("Michelle", 9));
		
		this.lurepo.deleteAll();
		lurepo.save(new LoginUser("admin", "password"));
	}

	@Test
	public void shouldFillOutComponentsWithDataWhenTheApplicationIsStarted() {
		then(this.repository.count()).isEqualTo(5);
	}

	@Test
	public void shouldFindTwoKimCustomers() {
		then(this.repository.findByName("Kim")).hasSize(2);
	}

}
