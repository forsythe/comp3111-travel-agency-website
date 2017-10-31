package comp3111.data.repo;

import org.springframework.stereotype.Repository;

import comp3111.data.model.Customer;

@Repository
public interface CustomerRepository extends PersonBaseRepository<Customer> {

}