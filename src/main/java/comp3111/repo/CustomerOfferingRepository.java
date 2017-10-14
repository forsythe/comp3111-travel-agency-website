package comp3111.repo;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import comp3111.model.CustomerOffering;

public interface CustomerOfferingRepository extends CrudRepository<CustomerOffering, Long> {

}
