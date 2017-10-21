package comp3111.auth;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import comp3111.model.LoginUser;
import comp3111.repo.LoginUserRepository;

@SpringComponent
public class Authentication {
	private static final Logger log = LoggerFactory.getLogger(Authentication.class);

	// note: you cannot autowire static fields.
	private LoginUserRepository loginUserRepository;

	// constructor injection
	@Autowired
	public Authentication(LoginUserRepository loginUserRepository) {
		this.loginUserRepository = loginUserRepository;
	}

	public boolean authenticate(String username, String rawPassword) {
		log.info("Tried to log in user:{} with password:{}", username, rawPassword);
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
