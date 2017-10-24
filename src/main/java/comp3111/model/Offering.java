package comp3111.model;

import comp3111.validators.Utils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Entity
@Transactional
public class Offering implements Persistable<Long> {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(cascade = CascadeType.ALL) // many offerings to 1 tour
	private Tour tour;

	@ManyToOne(cascade = CascadeType.ALL) // many offerings to 1 tour guide
	private TourGuide tourGuide;

	private Date startDate;
	private String hotelName;
	private int minCustomers;
	private int maxCustomers;

	@OneToMany(mappedBy = "offering", cascade = CascadeType.ALL, orphanRemoval = true)
	@LazyCollection(LazyCollectionOption.FALSE)
	private Collection<CustomerOffering> customerOffering = new HashSet<CustomerOffering>();

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
		return String.format("Offering[id=%d, '%s']", id, Utils.simpleDateFormat(startDate));
	}

	@Override
	public boolean isNew() {
		for (CustomerOffering co : customerOffering) {
			if (null != co.getId()) {
				return false;
			}
		}
		return null == getId() && null == tour.getId() && null == tourGuide.getId();
	}
}
