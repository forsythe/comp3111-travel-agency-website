package comp3111.data.repo;

import comp3111.data.model.LoginUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * A repository storing LoginUser objects.
 * 
 * @author Forsythe
 *
 */
@Repository
public interface LoginUserRepository extends CrudRepository<LoginUser, Long> {
	/**
	 * @param username A username
	 * @return Finds the LoginUser with the provided username
	 */
	LoginUser findByUsername(String username);
}
