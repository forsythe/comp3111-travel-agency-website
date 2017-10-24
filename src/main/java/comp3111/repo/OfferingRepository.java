package comp3111.repo;

import comp3111.model.Offering;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface OfferingRepository extends CrudRepository<Offering, Long> {
	Collection<Offering> findByHotelName(String hotelName);
}
