package comp3111.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import comp3111.model.Tour;

import java.util.Collection;
import java.util.List;

//@NoRepositoryBean
public interface TourRepository extends CrudRepository<Tour, Long> {

	Collection<Tour> findByTourName(String tourName);
}
