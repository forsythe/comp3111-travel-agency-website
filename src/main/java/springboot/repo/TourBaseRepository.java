package springboot.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import springboot.model.Tour;

import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface TourBaseRepository<T extends Tour> extends CrudRepository<T, Long> {

	Collection<Tour> findByTourName(String tourName);
}
