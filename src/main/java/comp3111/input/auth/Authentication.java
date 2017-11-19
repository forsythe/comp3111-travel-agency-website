package comp3111.input.auth;

import com.vaadin.spring.annotation.SpringComponent;
import comp3111.data.model.LoginUser;
import comp3111.data.repo.LoginUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;

/**
 * A class responsible for authenticating login username and passwords
 * 
 * @author Forsythe
 *
 */
@SpringComponent
public class Authentication {
	private static final Logger log = LoggerFactory.getLogger(Authentication.class);

	// note: you cannot autowire static fields.
	private LoginUserRepository loginUserRepository;

	/**
	 * Constructs an Authentication object
	 * 
	 * @param loginUserRepository
	 *            The LoginUserRepository
	 */
	@Autowired
	public Authentication(LoginUserRepository loginUserRepository) {
		this.loginUserRepository = loginUserRepository;
	}

	/**
	 * Authenticates a user
	 * 
	 * @param username
	 *            The username
	 * @param rawPassword
	 *            The raw password
	 * @return Whether the login is correct
	 */
	public boolean authenticate(String username, String rawPassword) {
		log.info("Tried to log in user:{}", username);
		LoginUser user = loginUserRepository.findByUsername(username);
		if (null == user) {
			log.info("couldn't find user {}", username);
			return false;
		}
		BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
		if (bcpe.matches(rawPassword, user.getHashedSaltedPassword())) {
			log.info("logged in!");
			return true;
		} else {
			log.info("wrong password.");
			return false;
		}
	}

	@PostConstruct
	void ensureAdminHasAccount() {
		LoginUser user = loginUserRepository.findByUsername("admin");
		if (null == user) {
			log.info("admin didn't have an account, so we made one");
			loginUserRepository.save(new LoginUser("admin", "Q1w2e3r4"));
		}
	}

}
