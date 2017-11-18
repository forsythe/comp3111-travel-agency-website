package comp3111.data.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;

/**
 * Represents a non-FAQ query sent by a LINE customer. These questions are
 * forwarded to the web interface for an employee to answer manually.
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

	/**
	 * @param query
	 *            The custom query that wasn't in the FAQ database.
	 * @param customer
	 *            The customer who asked the question.
	 */
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

	public String getCustomerName() {
		return customer.getName();
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
