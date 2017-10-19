package comp3111.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.transaction.Transactional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Entity
@Transactional
public class LoginUser {
	@Id
	@GeneratedValue
	private Long id;

	@Column(unique = true)
	private String username;
	private String hashedSaltedPassword;

	public LoginUser() {
	}

	public LoginUser(String username, String rawPassword) {
		setUsername(username);
		setHashedSaltedPassword(rawPassword);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHashedSaltedPassword() {
		return hashedSaltedPassword;
	}

	public void setHashedSaltedPassword(String rawPassword) {
		BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
		this.hashedSaltedPassword = bcpe.encode(rawPassword);
	}

}
