package presenter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.*;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.boot.VaadinAutoConfiguration;

import springboot.model.Customer;
import springboot.presenter.CustomerEditor;
import springboot.presenter.VaadinUI;
import springboot.repo.CustomerRepository;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VaadinUITests.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class VaadinUITests {

	@Autowired
	CustomerRepository repository;

	VaadinRequest vaadinRequest = Mockito.mock(VaadinRequest.class);

	CustomerEditor editor;

	VaadinUI vaadinUI;

	@Before
	public void setup() {
		this.editor = new CustomerEditor(this.repository);
		this.vaadinUI = new VaadinUI(this.repository, editor);
	}

	@Ignore
	@Test
	public void shouldInitializeTheGridWithCustomerRepositoryData() {
		int customerCount = (int) this.repository.count();

		vaadinUI.init(this.vaadinRequest);

		then(vaadinUI.getGrid().getColumns()).hasSize(3);
		then(getCustomersInGrid()).hasSize(customerCount);
	}

	private List<Customer> getCustomersInGrid() {
		ListDataProvider<Customer> ldp = (ListDataProvider) vaadinUI.getGrid().getDataProvider();
		return new ArrayList<>(ldp.getItems());
	}

	@Ignore
	@Test
	public void shouldFillOutTheGridWithNewData() {
		int initialCustomerCount = (int) this.repository.count();
		this.vaadinUI.init(this.vaadinRequest);
		customerDataWasFilled(editor, "Marcin", "Grzejszczak");

		this.editor.getSave().click();

		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1);

		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1)).extracting("firstName", "lastName")
				.containsExactly("Marcin", "Grzejszczak");

	}

	@Ignore
	@Test
	public void shouldFilterOutTheGridWithTheProvidedLastName() {
		this.vaadinUI.init(this.vaadinRequest);
		this.repository.save(new Customer("Josh", 20));

		vaadinUI.listCustomers("Long");

		then(getCustomersInGrid()).hasSize(1);
		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1)).extracting("firstName", "lastName")
				.containsExactly("Josh", "Long");
	}

	@Ignore
	@Test
	public void shouldInitializeWithInvisibleEditor() {
		this.vaadinUI.init(this.vaadinRequest);

		then(this.editor.isVisible()).isFalse();
	}

	@Ignore
	@Test
	public void shouldMakeEditorVisible() {
		this.vaadinUI.init(this.vaadinRequest);
		Customer first = getCustomersInGrid().get(0);
		this.vaadinUI.getGrid().select(first);

		then(this.editor.isVisible()).isTrue();
	}

	private void customerDataWasFilled(CustomerEditor editor, String name, String age) {
		this.editor.getFirstName().setValue(name);
		this.editor.getAge().setValue(age);
		editor.editCustomer(new Customer(name, age));
	}

	@Configuration
	@EnableAutoConfiguration(exclude = VaadinAutoConfiguration.class)
	static class Config {

		@Autowired
		CustomerRepository repository;

		@PostConstruct
		public void initializeData() {
			this.repository.save(new Customer("Jack", "Bauer"));
			this.repository.save(new Customer("Chloe", "O'Brian"));
			this.repository.save(new Customer("Kim", "Bauer"));
			this.repository.save(new Customer("David", "Palmer"));
			this.repository.save(new Customer("Michelle", "Dessler"));
		}
	}
}
