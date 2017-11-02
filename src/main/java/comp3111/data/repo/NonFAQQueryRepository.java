package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.NonFAQQuery;

@Repository
public interface NonFAQQueryRepository extends CrudRepository<NonFAQQuery, Long> {

}
