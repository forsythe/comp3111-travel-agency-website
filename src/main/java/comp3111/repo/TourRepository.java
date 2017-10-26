package comp3111.repo;

import comp3111.model.Tour;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

//@NoRepositoryBean
public interface TourRepository extends CrudRepository<Tour, Long> {

	Collection<Tour> findByTourName(String tourName);
}
