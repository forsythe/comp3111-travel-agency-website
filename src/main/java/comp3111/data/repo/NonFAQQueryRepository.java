package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.NonFAQQuery;

/**
 * A repository storing NonFAQQuery objects. These objects are created when a
 * user asks a question on the chatbot that we couldn't answer. They appear on
 * the web interface, and it is the employee's job to answer them.
 * 
 * @author Forsythe
 *
 */
@Repository
public interface NonFAQQueryRepository extends CrudRepository<NonFAQQuery, Long> {

}
