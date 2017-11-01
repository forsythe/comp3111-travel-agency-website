package comp3111.data.repo;

import comp3111.data.model.Booking;
import comp3111.data.model.Offering;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface BookingRepository extends CrudRepository<Booking, Long> {
	Collection<Booking> findByPaymentStatus(String paymentStatus);
	
	Collection<Booking> findByOffering(Offering o);
}
