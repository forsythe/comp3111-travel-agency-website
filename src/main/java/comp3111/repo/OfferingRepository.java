package comp3111.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;

import comp3111.model.Offering;

public interface OfferingRepository extends CrudRepository<Offering, Long> {
	Collection<Offering> findByHotelName(String hotelName);
}
