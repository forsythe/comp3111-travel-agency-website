package springboot;

import org.junit.Ignore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import springboot.repo.CustomerRepository;
import springboot.Application;

import static org.assertj.core.api.BDDAssertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ApplicationTests {

	@Autowired
	private CustomerRepository repository;

	@Ignore
	@Test
	public void shouldFillOutComponentsWithDataWhenTheApplicationIsStarted() {
		then(this.repository.count()).isEqualTo(5);
	}

	@Ignore
	@Test
	public void shouldFindTwoBauerCustomers() {
		then(this.repository.findByName("Bauer")).hasSize(2);
	}
}
