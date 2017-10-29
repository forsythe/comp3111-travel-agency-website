package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;

import comp3111.data.model.Booking;

import java.util.Collection;

public interface BookingRepository extends CrudRepository<Booking, Long> {
	Collection<Booking> findByPaymentStatus(String paymentStatus);
}
