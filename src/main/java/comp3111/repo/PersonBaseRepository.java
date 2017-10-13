package comp3111.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import comp3111.model.Customer;
import comp3111.model.Person;

@NoRepositoryBean
public interface PersonBaseRepository<T extends Person> extends CrudRepository<T, Long> {

	Collection<T> findByName(String name);

	Collection<T> findByLineId(String lineId);
}
