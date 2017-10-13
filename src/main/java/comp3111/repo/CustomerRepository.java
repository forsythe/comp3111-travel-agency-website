package comp3111.repo;

import org.springframework.stereotype.Repository;

import comp3111.model.Customer;

//http://blog.netgloo.com/2014/12/18/handling-entities-inheritance-with-spring-data-jpa/

public interface CustomerRepository extends PersonBaseRepository<Customer> {

}