package comp3111.repo;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import comp3111.model.CustomerOffering;

@Transactional // https://dzone.com/articles/how-does-spring-transactional
public interface CustomerOfferingRepository extends CrudRepository<CustomerOffering, Long> {

}
