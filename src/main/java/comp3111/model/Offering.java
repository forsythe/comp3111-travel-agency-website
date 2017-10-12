package comp3111.model;

import java.util.Date;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Offering {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne // many offerings to 1 tour
	private Tour tour;

	@ManyToOne // many offerings to 1 tour guide
	private TourGuide tourGuide;
	private Date startDate;
	private String hotelName;
	private int minCustomers;
	private int maxCustomers;

	@OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true)
	private Collection<CustomerOffering> offering;

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

	public Collection<CustomerOffering> getOffering() {
		return offering;
	}

	public void setOffering(Collection<CustomerOffering> offering) {
		this.offering = offering;
	}

	// Observer pattern
	public void notifyCustomersAboutStatus() {
		for (CustomerOffering co : getOffering()) {
			co.getCustomer().updateAboutOfferingStatus("statusOfTour");
		}
	}

	@Override
	public String toString() {
		return String.format("Offering[id=%d, tourName='%s', date='%s']", id, getTour().getTourName(), startDate);
	}
}
