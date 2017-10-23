package integration;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.*;
import comp3111.field.HKIDEntryField;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CustomerEditorTests extends TestBenchTestCase {

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
		wait4.until(ExpectedConditions.presenceOfElementLocated(By.id("create_new_customer_button")));
		$(ButtonElement.class).id("create_new_customer_button").click();

		WebDriverWait wait5 = new WebDriverWait(getDriver(), 10);
		wait5.until(ExpectedConditions.presenceOfElementLocated(By.id("customer_name")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("customer_name").setValue("Peter");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("customer_line_id").setValue("123452334");

		$(FormLayoutElement.class).$(TextFieldElement.class).id("hkid_main_part").setValue("G1234567");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("hkid_check_digit").setValue("A");

		$(FormLayoutElement.class).$(CustomComponentElement.class).id("customer_phone")
				.$(TextFieldElement.class).child($(TextFieldElement.class)).get(0).setValue("852");
		$(FormLayoutElement.class).$(CustomComponentElement.class).id("customer_phone")
				.$(TextFieldElement.class).child($(TextFieldElement.class)).get(1).setValue("12345678");

		$(FormLayoutElement.class).$(TextFieldElement.class).id("customer_age").setValue("23");
	}

	@After
	public void tearDown() throws Exception {
		getDriver().quit();
	}
}