package comp3111.data.repo;

import comp3111.data.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface PersonBaseRepository<T extends Person> extends CrudRepository<T, Long> {

	Collection<T> findByName(String name);
	
	Collection<T> findById(Long id);

}
