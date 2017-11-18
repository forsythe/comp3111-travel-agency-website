package comp3111.data.repo;

import java.util.Collection;

import org.springframework.stereotype.Repository;

import comp3111.data.model.Customer;

/**
 * A repository storing Customer objects.
 * 
 * @author Forsythe
 *
 */
@Repository
public interface CustomerRepository extends PersonBaseRepository<Customer> {

	/**
	 * @param lineId
	 *            Not the human readable username, but a long string of digits and
	 *            characters used internally by LINE which enable us to use the push
	 *            API.
	 * @return A collection of customers who have the specified lineId. Should have
	 *         size 1.
	 */
	Collection<Customer> findByLineId(String lineId);
}