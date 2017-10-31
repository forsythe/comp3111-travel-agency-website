package integration;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.*;
import comp3111.Application;
import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;

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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomerEditorTests extends TestBenchTestCase {

	@Autowired
	private CustomerRepository customerRepo;

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
		getDriver().get(TestConstants.HOME_URL);

		WebDriverWait wait1 = new WebDriverWait(getDriver(), 10);
		wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("tf_username")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_username").setValue("admin");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_password").setValue("Q1w2e3r4");
		$(ButtonElement.class).id("btn_submit").click();

		WebDriverWait wait2 = new WebDriverWait(getDriver(), 10);
		wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("lbl_title")));

		WebDriverWait wait3 = new WebDriverWait(getDriver(), 10);
		wait3.until(ExpectedConditions.presenceOfElementLocated(By.id("Customers")));
		$(VerticalLayoutElement.class).$(ButtonElement.class).id("Customers").click();

		WebDriverWait wait4 = new WebDriverWait(getDriver(), 10);
		wait4.until(ExpectedConditions.presenceOfElementLocated(By.id("btn_create_customer")));
		$(ButtonElement.class).id("btn_create_customer").click();

		WebDriverWait wait5 = new WebDriverWait(getDriver(), 10);
		wait5.until(ExpectedConditions.presenceOfElementLocated(By.id("tf_customer_name")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_customer_name").setValue("Peter The Great Tester");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_customer_line_id").setValue("123452334");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_hkid")
				.$(TextFieldElement.class).get(0).setValue("G123456");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_hkid")
				.$(TextFieldElement.class).get(1).setValue("A");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_phone")
				.$(TextFieldElement.class).get(0).setValue("852");

		$(FormLayoutElement.class).$(CustomFieldElement.class).id("tf_customer_phone")
				.$(TextFieldElement.class).get(1).setValue("12345678");

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_customer_age").setValue("23");

		$(FormLayoutElement.class).$(ButtonElement.class).id("btn_confirm_customer").click();

		assertThat (customerRepo.findByName("Peter The Great Tester").size() != 0);
	}

	@After
	public void tearDown() throws Exception {
		getDriver().quit();
	}
}