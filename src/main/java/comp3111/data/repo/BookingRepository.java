package comp3111.data.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import comp3111.data.model.Booking;
import comp3111.data.model.Offering;

public interface BookingRepository extends CrudRepository<Booking, Long> {
	Collection<Booking> findByPaymentStatus(String paymentStatus);
	
	Collection<Booking> findByOffering(Offering o);
}
