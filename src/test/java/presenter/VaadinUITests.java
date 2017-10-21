package presenter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.*;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.boot.VaadinAutoConfiguration;

import comp3111.editors.CustomerEditor;
import comp3111.model.Customer;
import comp3111.repo.CustomerRepository;
import comp3111.view.VaadinLoginUI;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = VaadinUITests.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableJpaRepositories("comp3111.repo") // need this to find the repos, since in different package!
@EntityScan("comp3111.model")
// https://stackoverflow.com/questions/33997031/spring-data-jpa-no-qualifying-bean-found-for-dependency
// @ComponentScan("other.components.package")
public class VaadinUITests {
	private static final Logger log = LoggerFactory.getLogger(VaadinUITests.class);

	@Autowired
	CustomerRepository repository;

	VaadinRequest vaadinRequest = Mockito.mock(VaadinRequest.class);
//
//	CustomerEditor editor;

	VaadinLoginUI vaadinUI;

	@Before
	public void setup() {
		//this.editor = new CustomerEditor(this.repository);
		//this.vaadinUI = new VaadinLoginUI(this.repository, editor);
		this.vaadinUI = new VaadinLoginUI();
		
		vaadinUI.init(this.vaadinRequest);
	}
	
	@Test
	public void dummy() {
		
	}

//	@Test
//	public void shouldInitializeTheGridWithCustomerRepositoryData() {
//		int customerCount = (int) this.repository.count();
//
//		vaadinUI.init(this.vaadinRequest);
//
//		then(vaadinUI.getGrid().getColumns()).hasSize(2);
//		then(getCustomersInGrid()).hasSize(customerCount);
//	}
//
//	private List<Customer> getCustomersInGrid() {
//		ListDataProvider<Customer> ldp = (ListDataProvider) vaadinUI.getGrid().getDataProvider();
//		return new ArrayList<>(ldp.getItems());
//	}
//
//	@Test
//	public void shouldFillOutTheGridWithNewData() {
//		this.repository.deleteAll();
//
//		int initialCustomerCount = (int) this.repository.count();
//		this.vaadinUI.init(this.vaadinRequest);
//		customerDataWasFilled(editor, "Marcin", 20);
//
//		this.editor.getSave().click();
//
//		then(getCustomersInGrid()).hasSize(initialCustomerCount + 1);
//
//		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1)).extracting("name", "age")
//				.containsExactly("Marcin", 20);
//
//	}
//
//	@Test
//	public void shouldFilterOutTheGridWithTheProvidedName() {
//		this.vaadinUI.init(this.vaadinRequest);
//		this.repository.deleteAll();
//		this.repository.save(new Customer("Josh", 20));
//
//		vaadinUI.listCustomers("Josh");
//
//		then(getCustomersInGrid()).hasSize(1);
//		then(getCustomersInGrid().get(getCustomersInGrid().size() - 1)).extracting("name", "age")
//				.containsExactly("Josh", 20);
//	}
//
//	@Test
//	public void shouldInitializeWithInvisibleEditor() {
//		this.vaadinUI.init(this.vaadinRequest);
//
//		then(this.editor.isVisible()).isFalse();
//	}
//
//	@Test
//	public void shouldMakeEditorVisible() {
//		this.vaadinUI.init(this.vaadinRequest);
//		Customer first = getCustomersInGrid().get(0);
//		this.vaadinUI.getGrid().select(first);
//
//		then(this.editor.isVisible()).isTrue();
//	}
//
//	private void customerDataWasFilled(CustomerEditor editor, String name, int age) {
//		this.editor.getName().setValue(name);
//		this.editor.getAge().setValue(Integer.toString(age));
//		editor.editCustomer(new Customer(name, age));
//	}

	@Configuration
	@EnableAutoConfiguration(exclude = VaadinAutoConfiguration.class)
	static class Config {

		@Autowired
		CustomerRepository repository;
	

		@PostConstruct
		public void initializeData() {
//			this.repository.deleteAll();
//			this.repository.save(new Customer("Jack", 50));
//			this.repository.save(new Customer("Chloe", 20));
//			this.repository.save(new Customer("Kim", 30));
//			this.repository.save(new Customer("David", 5));
//			this.repository.save(new Customer("Michelle", 9));
		}
	}
}
