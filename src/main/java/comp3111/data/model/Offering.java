package comp3111.data.model;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.data.domain.Persistable;

import comp3111.Utils;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Entity
public class Offering {
	@Id
	@GeneratedValue
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER) // many offerings to 1 tour
	private Tour tour;
	@ManyToOne(fetch = FetchType.EAGER) // many offerings to 1 tour guide
	private TourGuide tourGuide;

	private Date startDate;
	private String hotelName;
	private int minCustomers;
	private int maxCustomers;

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

	public Date getLastEditableDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getStartDate());
		cal.add(Calendar.DATE, -3);
		return cal.getTime();
	}

	@Override
	public String toString() {
		return String.format("Offering[id=%d, %s, %s]", id, Utils.simpleDateFormat(startDate), this.tour.getTourName());
	}

}
