package comp3111.data.repo;

import comp3111.data.model.Tour;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TourRepository extends CrudRepository<Tour, Long> {

	Collection<Tour> findByTourName(String tourName);
	
	Collection<Tour> findById(Long id);
}
