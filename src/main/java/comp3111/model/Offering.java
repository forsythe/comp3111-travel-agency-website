package comp3111.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.transaction.Transactional;

@Entity
@Transactional
public class Offering {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // many offerings to 1 tour
	private Tour tour;

	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}) // many offerings to 1 tour guide
	private TourGuide tourGuide;

	private Date startDate;
	private String hotelName;
	private int minCustomers;
	private int maxCustomers;

	@OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private Collection<CustomerOffering> customerOffering = new ArrayList<CustomerOffering>();

	public Offering() {
	}

	public Offering(Tour tour, TourGuide tourGuide, Date startDate, String hotelName, int minCustomers,
			int maxCustomers) {
		this.tour = tour;
		this.tourGuide = tourGuide;
		this.startDate = startDate;
		this.hotelName = hotelName;
		this.minCustomers = minCustomers;
		this.maxCustomers = maxCustomers;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tour getTour() {
		return tour;
	}

	public void setTour(Tour tour) {
		this.tour = tour;
	}

	public TourGuide getTourGuide() {
		return tourGuide;
	}

	public void setTourGuide(TourGuide tourGuide) {
		this.tourGuide = tourGuide;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getHotelName() {
		return hotelName;
	}

	public void setHotelName(String hotelName) {
		this.hotelName = hotelName;
	}

	public int getMinCustomers() {
		return minCustomers;
	}

	public void setMinCustomers(int minCustomers) {
		this.minCustomers = minCustomers;
	}

	public int getMaxCustomers() {
		return maxCustomers;
	}

	public void setMaxCustomers(int maxCustomers) {
		this.maxCustomers = maxCustomers;
	}

	public Collection<CustomerOffering> getCustomerOffering() {
		return customerOffering;
	}

	public void setCustomerOffering(Collection<CustomerOffering> offering) {
		this.customerOffering = offering;
	}

	// Observer pattern
	public void notifyCustomersAboutStatus() {
		for (CustomerOffering co : getCustomerOffering()) {
			co.getCustomer().updateAboutOfferingStatus("statusOfTour");
		}
	}

	@Override
	public String toString() {
		return String.format("Offering[id=%d, tourName='%s', date='%s']", id, getTour().getTourName(), startDate);
	}
}
