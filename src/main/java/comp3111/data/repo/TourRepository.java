package comp3111.data.repo;

import comp3111.data.model.Tour;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * A repository storing Tour objects
 * 
 * @author Forsythe
 *
 */
@Repository
public interface TourRepository extends CrudRepository<Tour, Long> {

	/**
	 * @param tourName A tour name
	 * @return A collection of Tour objects with the specified tour name
	 */
	Collection<Tour> findByTourName(String tourName);

}
