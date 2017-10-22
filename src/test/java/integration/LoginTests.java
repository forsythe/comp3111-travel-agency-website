package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.FormLayoutElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.testbench.elements.TextFieldElement;

public class LoginTests extends TestBenchTestCase {

	@BeforeClass
	public static void init() {
		System.setProperty(TestConstants.CHROME_DRIVER, TestConstants.CHROME_PATH);

	}

	@Before
	public void setUp() throws Exception {
		setDriver(new ChromeDriver());
	}

	// @Test
	// public void testFails() {
	// Assert.fail("This is supposed to fail");
	// }

	@Test
	public void testSuccessLogin() {
		getDriver().get(TestConstants.HOME_URL);

		WebDriverWait wait1 = new WebDriverWait(getDriver(), 10);
		wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("tf_username")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_username").setValue("admin");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_password").setValue("Q1w2e3r4");
		$(ButtonElement.class).id("btn_submit").click();

		WebDriverWait wait2 = new WebDriverWait(getDriver(), 10);
		wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("lbl_title")));
		assertNotNull(findElement(By.id("lbl_title")));

	}

	@Test
	public void testFailLogin() {
		getDriver().get(TestConstants.HOME_URL);

		WebDriverWait wait1 = new WebDriverWait(getDriver(), 10);
		wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("tf_username")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_username").setValue("asdf");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_password").setValue("Q1w2e3r4");
		$(ButtonElement.class).id("btn_submit").click();

		NotificationElement invalidCredentials = $(NotificationElement.class).first();
		assertEquals("Invalid credentials", invalidCredentials.getCaption());
		// assertEquals("The description", invalidCredentials.getDescription());
		// assertEquals("warning", invalidCredentials.getType());
		invalidCredentials.close();

	}

	@After
	public void tearDown() throws Exception {
		getDriver().quit();
	}
}