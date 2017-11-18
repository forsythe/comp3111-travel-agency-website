package comp3111.data.repo;

import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

/**
 * A repository storing Booking objects.
 * 
 * @author Forsythe
 *
 */
public interface BookingRepository extends CrudRepository<Booking, Long> {
	/**
	 * @param paymentStatus
	 *            The payment status
	 * @return All bookings which have the specified payment status
	 *
	 * @see Booking#PAYMENT_PENDING
	 * @see Booking#PAYMENT_CONFIRMED
	 * @see Booking#PAYMENT_CANCELLED_BECAUSE_OFFERING_CANCELLED
	 */
	Collection<Booking> findByPaymentStatus(String paymentStatus);

	/**
	 * @param o
	 *            An offering
	 * @return All bookings which are for the specified offering
	 */
	Collection<Booking> findByOffering(Offering o);

	/**
	 * @param c
	 *            A customer
	 * @return All bookings made by the specified customer
	 */
	Collection<Booking> findByCustomer(Customer c);
}
