package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;

import comp3111.data.model.Tour;

import java.util.Collection;

//@NoRepositoryBean
public interface TourRepository extends CrudRepository<Tour, Long> {

	Collection<Tour> findByTourName(String tourName);
}
