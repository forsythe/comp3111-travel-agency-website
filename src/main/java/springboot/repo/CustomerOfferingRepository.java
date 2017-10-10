package springboot.repo;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import springboot.model.CustomerOffering;

@Transactional // https://dzone.com/articles/how-does-spring-transactional
public interface CustomerOfferingRepository extends CrudRepository<CustomerOffering, Long> {

}
