package springboot.repo;

import org.springframework.stereotype.Repository;

import springboot.model.Customer;

//http://blog.netgloo.com/2014/12/18/handling-entities-inheritance-with-spring-data-jpa/

public interface CustomerRepository extends PersonBaseRepository<Customer> {

}