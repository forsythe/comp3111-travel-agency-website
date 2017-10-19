package comp3111.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.model.LoginUser;

@Repository
public interface LoginUserRepository extends CrudRepository<LoginUser, Long> {
	LoginUser findByUsername(String username);
}
