package comp3111.data.repo;

import comp3111.data.model.Customer;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends PersonBaseRepository<Customer> {

}