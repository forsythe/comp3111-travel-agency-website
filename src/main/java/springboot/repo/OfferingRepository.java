package springboot.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import springboot.model.Offering;
import springboot.model.Tour;

import java.util.List;

public interface OfferingRepository extends CrudRepository<Offering, Long> {

}
