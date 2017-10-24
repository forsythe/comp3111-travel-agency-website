package comp3111.repo;

import comp3111.model.LoginUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginUserRepository extends CrudRepository<LoginUser, Long> {
	LoginUser findByUsername(String username);
}
