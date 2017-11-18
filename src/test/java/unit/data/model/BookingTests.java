/**
 * 
 */
package unit.data.model;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import comp3111.Utils;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;

/**
 * @author Forsythe
 *
 */
public class BookingTests {

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
	public void testGettingCustomerHkid() {
		Booking b = new Booking();
		assertNull(b.getCustomerHkid());
		Customer c = new Customer();
		c.setHkid("123");
		b.setCustomer(c);
		assertEquals("123", b.getCustomer().getHkid());
	}

	@Test
	public void testGettingCustomer() {
		Booking b = new Booking();
		assertNull(b.getCustomer());
		Customer c = new Customer();
		b.setCustomer(c);
		assertSame(c, b.getCustomer());
	}

	@Test
	public void testGettingCustomerName() {
		Booking b = new Booking();
		assertNull(b.getCustomerName());
		Customer c = new Customer();
		c.setName("john");
		b.setCustomer(c);
		assertEquals(c.getName(), b.getCustomerName());
	}

	@Test
	public void testEquals() {
		Booking b = new Booking();
		b.setId(123);
		assertTrue(b.equals(b));

		Booking c = new Booking();
		c.setId(124);
		assertFalse(b.equals(c));
		assertEquals(b.equals(c), c.equals(b));
	}

}
