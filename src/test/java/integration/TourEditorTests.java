package integration;

import com.vaadin.testbench.By;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.*;
import com.vaadin.ui.TextArea;
import comp3111.Application;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.TourRepository;

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
public class TourEditorTests extends TestBenchTestCase {

	@Autowired
	private TourRepository tourRepo;

	@BeforeClass
	public static void init() {
		System.setProperty(TestConstants.CHROME_DRIVER, TestConstants.CHROME_PATH);

	}

	@Before
	public void setUp() throws Exception {
		setDriver(new ChromeDriver());
	}

	@Test
	public void testCreateTour() {
		getDriver().get(TestConstants.HOME_URL);

		WebDriverWait wait1 = new WebDriverWait(getDriver(), 10);
		wait1.until(ExpectedConditions.presenceOfElementLocated(By.id("tf_username")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_username").setValue("admin");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_password").setValue("Q1w2e3r4");
		$(ButtonElement.class).id("btn_submit").click();

		WebDriverWait wait2 = new WebDriverWait(getDriver(), 10);
		wait2.until(ExpectedConditions.presenceOfElementLocated(By.id("lbl_title")));

		WebDriverWait wait3 = new WebDriverWait(getDriver(), 10);
		wait3.until(ExpectedConditions.presenceOfElementLocated(By.id("Tour Management")));
		$(VerticalLayoutElement.class).$(ButtonElement.class).id("Tour Management").click();

		WebDriverWait wait4 = new WebDriverWait(getDriver(), 10);
		wait4.until(ExpectedConditions.presenceOfElementLocated(By.id("btn_create_tour")));
		$(ButtonElement.class).id("btn_create_tour").click();

		WebDriverWait wait5 = new WebDriverWait(getDriver(), 10);
		wait5.until(ExpectedConditions.presenceOfElementLocated(By.id("tf_tour_name")));

		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_tour_name").setValue("One Day Trip to Mars");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_days").setValue("1");
		$(FormLayoutElement.class).$(RadioButtonGroupElement.class).id("rbgrp_tour_type").selectByText("Repeating");
		$(FormLayoutElement.class).$(CheckBoxGroupElement.class).id("chkbxgrp_allowed_days_of_week").selectByText("Mon");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_child_discount").setValue("1");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_toddler_discount").setValue("0.2");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_weekday_price").setValue("555");
		$(FormLayoutElement.class).$(TextFieldElement.class).id("tf_weekend_price").setValue("666");
		$(FormLayoutElement.class).$(TextAreaElement.class).id("tf_description").setValue("Martians not welcomed!");

		$(FormLayoutElement.class).$(ButtonElement.class).id("btn_confirm_tour").click();

		assertThat (tourRepo.findByTourName("One Day Trip to Mars").size() != 0);
	}

	@After
	public void tearDown() throws Exception {
		getDriver().quit();
	}
}