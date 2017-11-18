/**
 * 
 */
package unit.data.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.junit.Test;

import comp3111.Utils;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.LoginUser;
import comp3111.data.model.NonFAQQuery;
import comp3111.data.model.Offering;
import comp3111.data.model.Person;
import comp3111.data.model.PromoEvent;
import comp3111.data.model.Tour;

/**
 * @author Forsythe
 *
 */
public class DataModelTests {

	@Test
	public void testBookingGetTotalCost() {
		Tour t = new Tour();
		t.setWeekdayPrice(200);
		t.setWeekendPrice(300);
		t.setChildDiscount(0.8);
		t.setToddlerDiscount(0.3);

		Offering o = new Offering();
		o.setTour(t);
		o.setStartDate(new GregorianCalendar(2017, Calendar.DECEMBER, 10).getTime()); // a sunday

		Booking b = new Booking();
		b.setOffering(o);
		b.setNumAdults(2);
		b.setNumChildren(2);
		b.setNumToddlers(2);
		b.setPromoDiscountMultiplier(0.9);

		double weekendPrice = b.getTotalCost();
		o.setStartDate(Utils.addDate(o.getStartDate(), 1)); // a monday

		double weekdayPrice = b.getTotalCost();

		assertTrue(weekendPrice > weekdayPrice);

	}

	@Test
	public void testBookingGettingCustomerHkid() {
		Booking b = new Booking();
		assertNull(b.getCustomerHkid());
		Customer c = new Customer();
		c.setHkid("123");
		b.setCustomer(c);
		assertEquals(b.getCustomerHkid(), c.getHkid());
	}

	@Test
	public void testBookingGettingCustomer() {
		Booking b = new Booking();
		assertNull(b.getCustomer());
		Customer c = new Customer();
		b.setCustomer(c);
		assertSame(c, b.getCustomer());
	}

	@Test
	public void testBookingGettingCustomerName() {
		Booking b = new Booking();
		assertNull(b.getCustomerName());
		Customer c = new Customer();
		c.setName("john");
		b.setCustomer(c);
		assertEquals(c.getName(), b.getCustomerName());
	}

	@Test
	public void testBookingEquals() {
		Booking b = new Booking();
		b.setId(123);
		assertTrue(b.equals(b));

		Booking c = new Booking();
		c.setId(124);
		assertFalse(b.equals(c));
		assertFalse(b.equals(new Object()));
		assertEquals(b.equals(c), c.equals(b));
	}

	@Test
	public void testPersonEquals() {
		Person p = new Customer();
		p.setId(123L);

		assertTrue(p.equals(p));

		Person c = new Customer();
		c.setId(124L);

		assertFalse(p.equals(c));
		assertFalse(p.equals(new Object()));
		assertEquals(p.equals(c), c.equals(p));
	}

	@Test
	public void testLoginUserEquals() {
		LoginUser p = new LoginUser();
		p.setId(123L);

		assertTrue(p.equals(p));

		LoginUser c = new LoginUser();
		c.setId(124L);

		assertFalse(p.equals(c));
		assertFalse(p.equals(new Object()));
		assertEquals(p.equals(c), c.equals(p));
	}

	@Test
	public void testOfferingCanGetAvailability() {
		Tour t = new Tour();
		t.setAllowedDates(new HashSet<Date>(Arrays.asList(new Date())));
		assertTrue(null != t.getOfferingAvailability());
		t.getAllowedDates().clear();
		t.setAllowedDaysOfWeek(new HashSet<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7)));
		assertTrue(null != t.getOfferingAvailability());
	}

	@Test
	public void testTourEquals() {
		Tour p = new Tour();
		p.setId(123L);

		assertTrue(p.equals(p));

		Tour c = new Tour();
		c.setId(124L);

		assertFalse(p.equals(c));
		assertFalse(p.equals(new Object()));
		assertEquals(p.equals(c), c.equals(p));
	}

	@Test
	public void testPromoEventEquals() {
		PromoEvent p = new PromoEvent();
		p.setId(123L);

		assertTrue(p.equals(p));

		PromoEvent c = new PromoEvent();
		c.setId(124L);

		assertFalse(p.equals(c));
		assertFalse(p.equals(new Object()));
		assertEquals(p.equals(c), c.equals(p));
	}

	@Test
	public void testNonFAQQueryEquals() {
		NonFAQQuery p = new NonFAQQuery();
		p.setId(123L);

		assertTrue(p.equals(p));

		NonFAQQuery c = new NonFAQQuery();
		c.setId(124L);

		assertFalse(p.equals(c));
		assertFalse(p.equals(new Object()));
		assertEquals(p.equals(c), c.equals(p));
	}

}
