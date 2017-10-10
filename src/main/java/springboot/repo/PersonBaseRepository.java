package springboot.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import springboot.model.Customer;
import springboot.model.Person;

@NoRepositoryBean
public interface PersonBaseRepository<T extends Person> extends CrudRepository<T, Long> {

	Collection<Customer> findByName(String name);

	Collection<Person> findByLineId(String lineId);
}
