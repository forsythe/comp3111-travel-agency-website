package comp3111.data.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.LoginUser;

@Repository
public interface LoginUserRepository extends CrudRepository<LoginUser, Long> {
	LoginUser findByUsername(String username);
}
