package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;

import comp3111.data.model.Offering;

import java.util.Collection;

public interface OfferingRepository extends CrudRepository<Offering, Long> {
	Collection<Offering> findByHotelName(String hotelName);
}
