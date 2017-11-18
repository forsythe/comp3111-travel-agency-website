package integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CustomFieldElement;
import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.VerticalLayoutElement;

import comp3111.Application;
import comp3111.data.DBManager;
import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerEditorUITests extends TestBenchTestCase {

	private static final String TEST_CUSTOMER_NAME = "Peter The Great Tester";

	@Autowired
	private CustomerRepository customerRepo;

	@Autowired
	private DBManager dbManager;

	@BeforeClass
	public static void init() {
		System.setProperty(TestConstants.CHROME_DRIVER, TestConstants.CHROME_PATH);

	}

	@Before
	public void setUp() throws Exception {
		setDriver(new ChromeDriver());
	}

	@Test
	public void testCreateCustomer() {
		Collection<Customer> originalCustomers = customerRepo.findByName(TEST_CUSTOMER_NAME);

		getDriver().get(TestConstants.HOME_URL);

		WebDriverWait wait1 = new WebDriverWait(getDriver(), 10);
		wait1.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("tf_username")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_username").setValue("admin");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_password").setValue("Q1w2e3r4");
		$(ButtonElement.class).id("btn_submit").click();

		WebDriverWait wait2 = new WebDriverWait(getDriver(), 10);
		wait2.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("lbl_title")));

		WebDriverWait wait3 = new WebDriverWait(getDriver(), 10);
		wait3.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("Customers")));
		$(VerticalLayoutElement.class).$(ButtonElement.class).id("Customers").click();

		WebDriverWait wait4 = new WebDriverWait(getDriver(), 10);
		wait4.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("btn_create_customer")));
		$(ButtonElement.class).id("btn_create_customer").click();

		WebDriverWait wait5 = new WebDriverWait(getDriver(), 10);
		wait5.until(ExpectedConditions.presenceOfElementLocated(org.openqa.selenium.By.id("tf_customer_name")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_customer_name").setValue(TEST_CUSTOMER_NAME);
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_customer_line_id").setValue("123452334");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_hkid").$(TextFieldElement.class).get(0)
				.setValue("G123456");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_hkid").$(TextFieldElement.class).get(1)
				.setValue("A");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_phone").$(TextFieldElement.class).get(0)
				.setValue("852");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_phone").$(TextFieldElement.class).get(1)
				.setValue("12345678");

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_customer_age").setValue("23");

		$(FormLayoutElement.class).$(ButtonElement.class).id("btn_confirm_customer").click();

		Collection<Customer> afterAddingCustomers = customerRepo.findByName(TEST_CUSTOMER_NAME);

		int count = 0;
		for (Customer customer : afterAddingCustomers) {
			if (!originalCustomers.contains(customer)) {
				assertThat(customer.getName().equals(TEST_CUSTOMER_NAME));
				assertThat(customer.getHkid().equals("G123456(A)"));
				assertThat(customer.getPhone().equals("(852)1234567"));
				assertThat(customer.getAge() == 23);

				count++;
				// customerRepo.delete(customer.getId());
			}
		}

		assertThat(count == 1);
	}

	@After
	public void tearDown() throws Exception {
		getDriver().quit();
	}
}