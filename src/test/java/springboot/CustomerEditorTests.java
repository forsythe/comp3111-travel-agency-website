package springboot;

import static org.junit.Assert.assertEquals;

import javax.annotation.PostConstruct;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.boot.VaadinAutoConfiguration;

import comp3111.editors.CustomerEditor;
import comp3111.repo.CustomerRepository;

@SpringBootTest(classes = CustomerEditorTests.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ComponentScan(basePackages = { "comp3111.*" })
@EntityScan("comp3111.*")
@EnableJpaRepositories("comp3111.*")
@RunWith(SpringJUnit4ClassRunner.class)
public class CustomerEditorTests {

	private static final String NAME = "Marcin";
	private static final int AGE = 20;

	@Autowired
	CustomerRepository customerRepository;
	@Autowired
	CustomerEditor editor;

	@Test
	public void shouldStoreCustomerInRepoWhenEditorSaveClickedAndDeletable() {
		this.editor.getCreateNewCustomerButton().click();
		this.editor.getCustomerName().setValue(NAME);
		this.editor.getCustomerAge().setValue(Integer.toString(AGE));
		// customerDataWasFilled();

		this.editor.getSubwindowConfirmCreateCustomer().click();

		// then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()));
		assertEquals(this.customerRepository.findByName(NAME).size(), 1);
		this.customerRepository.delete(this.customerRepository.findByName(NAME));
		assertEquals(this.customerRepository.findByName(NAME).size(), 0);
	}

	@Ignore
	@Test
	public void shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		// this.editor.getName().setValue(NAME);
		// this.editor.getAge().setValue(Integer.toString(AGE));
		// customerDataWasFilled();
		//
		// editor.getDelete().click();

		// then(this.customerRepository).should().delete(argThat(customerMatchesEditorFields()));
	}

	// private TypeSafeMatcher<Customer> customerMatchesEditorFields() {
	// return new TypeSafeMatcher<Customer>() {
	// @Override
	// public void describeTo(Description description) {
	// }
	//
	// @Override
	// protected boolean matchesSafely(Customer item) {
	// return NAME.equals(item.getName()) && AGE == item.getAge();
	// }
	// };
	// }

	@Configuration
	@EnableAutoConfiguration(exclude = VaadinAutoConfiguration.class)
	@EnableVaadin
	static class Config {

		@Autowired
		CustomerRepository repository;

		@PostConstruct
		public void initializeData() {
			// this.repository.deleteAll();
			// this.repository.save(new Customer("Jack", 50));
			// this.repository.save(new Customer("Chloe", 20));
			// this.repository.save(new Customer("Kim", 30));
			// this.repository.save(new Customer("David", 5));
			// this.repository.save(new Customer("Michelle", 9));
		}
	}
}

