package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.Tour;

import java.util.Collection;

@Repository
public interface TourRepository extends CrudRepository<Tour, Long> {

	Collection<Tour> findByTourName(String tourName);
}
