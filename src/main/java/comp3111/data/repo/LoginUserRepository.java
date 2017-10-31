package comp3111.data.repo;

import comp3111.data.model.LoginUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginUserRepository extends CrudRepository<LoginUser, Long> {
	LoginUser findByUsername(String username);
}
