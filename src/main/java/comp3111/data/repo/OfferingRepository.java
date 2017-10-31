package comp3111.data.repo;

import comp3111.data.model.Offering;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface OfferingRepository extends CrudRepository<Offering, Long> {
	Collection<Offering> findByHotelName(String hotelName);
}
