package comp3111.data.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.Offering;
import comp3111.data.model.Tour;


@Repository
public interface OfferingRepository extends CrudRepository<Offering, Long> {
	Collection<Offering> findByHotelName(String hotelName);

	Collection<Offering> findByTour(Tour t);
}
