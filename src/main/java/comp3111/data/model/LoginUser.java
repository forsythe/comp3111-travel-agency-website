package comp3111.data.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Represents a user which can log in to the system.
 * 
 * @author Forsythe
 *
 */
@Entity
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

	/**
	 * @return The salted and bcrypt hashed output. Do not directly compare it with
	 *         the output of a hash to verify a login. Instead, use
	 *         {@link comp3111.input.auth.Authentication#authenticate(String, String)}.
	 */
	public String getHashedSaltedPassword() {
		return hashedSaltedPassword;
	}

	/**
	 * @param rawPassword
	 *            a raw password string. Will be hashed and salted using bcrypt,
	 *            before storing into the database. By default, uses 10 rounds of
	 *            hashing.
	 */
	public void setHashedSaltedPassword(String rawPassword) {
		BCryptPasswordEncoder bcpe = new BCryptPasswordEncoder();
		this.hashedSaltedPassword = bcpe.encode(rawPassword);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LoginUser) {
			return ((LoginUser) other).getId().equals(this.getId());
		}
		return false;
	}

}
