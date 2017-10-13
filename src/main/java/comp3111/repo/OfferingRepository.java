package comp3111.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import comp3111.model.Offering;
import comp3111.model.Tour;

import java.util.List;

public interface OfferingRepository extends CrudRepository<Offering, Long> {

}
