package springboot;

import org.junit.Before;
import org.junit.Ignore;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vaadin.spring.boot.VaadinAutoConfiguration;

import springboot.repo.CustomerRepository;
import springboot.Application;
import springboot.model.Customer;
import springboot.presenter.CustomerEditor;
import springboot.presenter.VaadinUI;

import static org.assertj.core.api.BDDAssertions.*;

import javax.annotation.PostConstruct;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)

public class ApplicationTests {

	@Autowired
	private CustomerRepository repository;

	@Before
	public void setup() {
		this.repository.deleteAll();
		this.repository.save(new Customer("Jack", 50));
		this.repository.save(new Customer("Chloe", 20));
		this.repository.save(new Customer("Kim", 30));
		this.repository.save(new Customer("Kim", 34));
		this.repository.save(new Customer("Michelle", 9));
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
