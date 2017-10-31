package comp3111.view;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import comp3111.Utils;
import comp3111.data.model.Booking;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.NonFAQQueryRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;

@SpringView(name = HomeView.VIEW_NAME)
// @UIScope
@Scope("prototype") // needed to make this an error view (default fallback view)
public class HomeView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "";

	@Autowired
	private CustomerRepository cRepo;
	@Autowired
	private BookingRepository bRepo;
	@Autowired
	private OfferingRepository oRepo;
	@Autowired
	private TourRepository tRepo;
	@Autowired
	private NonFAQQueryRepository qRepo;

	@PostConstruct
	void init() {
		addComponent(new Label("This is the home view. Here you will find some nice summary information."));

		addComponent(new Label("There are currently " + Utils.iterableToCollection(cRepo.findAll()).size()
				+ " customers in our system. "));

		int numChildren = 0;
		int numToddlers = 0;
		int numAdults = 0;
		for (Booking b : bRepo.findAll()) {
			numChildren += b.getNumChildren();
			numToddlers += b.getNumToddlers();
			numAdults += b.getNumAdults();
		}

		addComponent(new Label("There are " + numToddlers + " toddlers, " + numChildren + " children, " + numAdults
				+ " adults enrolled in our system."));

		addComponent(new Label("We currently offer " + Utils.iterableToCollection(bRepo.findAll()).size()
				+ " booking(s) for " + Utils.iterableToCollection(oRepo.findAll()).size() + " offering(s) across "
				+ Utils.iterableToCollection(tRepo.findAll()).size() + " tour(s)"));

		int maxCustomersPerTour = 0;
		Tour mostPopTour = null;
		for (Tour t : tRepo.findAll()) {
			int amt = 0;
			for (Offering o : oRepo.findByTour(t)) {
				for (Booking b : bRepo.findByOffering(o)) {
					amt += b.getNumAdults() + b.getNumChildren() + b.getNumToddlers();
				}
			}
			if (amt > maxCustomersPerTour) {
				maxCustomersPerTour = amt;
				mostPopTour = t;
			}
		}
		addComponent(new Label("The most popular tour is " + mostPopTour));

		addComponent(new Label("There are currently "
				+ Utils.iterableToCollection(qRepo.findAll()).stream().filter(q -> q.getAnswer().isEmpty()).count()
				+ " pending querie(s) waiting to be resolved"));
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// This view is constructed in the init() method()
	}
}