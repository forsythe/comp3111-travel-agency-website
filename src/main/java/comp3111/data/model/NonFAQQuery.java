package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * An entity representing a non-FAQ query sent by a LINE customer
 * 
 * @author Forsythe
 *
 */
@Entity
@Inheritance
public class NonFAQQuery {

	@Id
	@GeneratedValue
	private Long id;

	private String query;
	private String answer;

	@ManyToOne(fetch = FetchType.EAGER) // many queries to 1 customer
	private Customer customer;

	public NonFAQQuery() {
		query = "";
		answer = "";
	}

	public NonFAQQuery(String query, Customer customer) {
		this.query = query;
		this.answer = "";
		this.customer = customer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Override
	public String toString() {
		return String.format("NonFAQQuery[id=%d, q=[%s], a=[%s]]", id, query, answer);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof NonFAQQuery)
			return other != null && this.getId() != null && ((NonFAQQuery) other).getId().equals(this.getId());
		return false;
	}

}
