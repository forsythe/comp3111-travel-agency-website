package comp3111.data.repo;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import comp3111.data.model.Customer;

@Repository
public interface CustomerRepository extends PersonBaseRepository<Customer> {
	Customer findOneByLineId(String lineId);
	Collection<Customer> findByLineId(String lineId);
}