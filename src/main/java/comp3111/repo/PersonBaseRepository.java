package comp3111.repo;

import comp3111.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;

@NoRepositoryBean
public interface PersonBaseRepository<T extends Person> extends CrudRepository<T, Long> {

	Collection<T> findByName(String name);

	Collection<T> findByLineId(String lineId);
}
