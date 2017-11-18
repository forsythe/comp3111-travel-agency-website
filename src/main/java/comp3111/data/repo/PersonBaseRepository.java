package comp3111.data.repo;

import comp3111.data.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

/**
 * A repository for the abstract Person class
 * 
 * @author Forsythe
 *
 * @param <T>
 *            The subclass of Person
 */
@NoRepositoryBean
public interface PersonBaseRepository<T extends Person> extends CrudRepository<T, Long> {

	/**
	 * @param name
	 *            A name
	 * @return Finds all person objects with the specified name
	 */
	Collection<T> findByName(String name);

	/**
	 * @param name
	 *            A name
	 * @return Finds a single person object with the specified name
	 */
	T findOneByName(String name);

	/**
	 * @param id
	 *            a database ID
	 * @return Finds all persons with the specified id (database id, not line id)
	 */
	Collection<T> findById(Long id);

}
