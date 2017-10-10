package springboot.repo;

import javax.transaction.Transactional;

import springboot.model.Customer;

//http://blog.netgloo.com/2014/12/18/handling-entities-inheritance-with-spring-data-jpa/
@Transactional // https://dzone.com/articles/how-does-spring-transactional
public interface CustomerRepository extends PersonBaseRepository<Customer> {

}
