package comp3111.suggest;

import com.vaadin.spring.annotation.SpringComponent;
import comp3111.Utils;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Tour;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.Point;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A Class to suggest new tours to customers who have booked before.
 */
@SpringComponent
public class SuggestTour {

    private CustomerRepository customerRepo;
    private BookingRepository bookingRepo;
    private OfferingRepository offeringRepo;
    private TourRepository tourRepo;

    private TourCluster cluster;

    /**
     * Create a SuggestTour. All autowired.
     * @param customerRepo Customer repo
     * @param bookingRepo Booking repo
     * @param offeringRepo Offering repo
     * @param tourRepo Tour repo
     * @param cluster The tour cluster
     */
    @Autowired
    public SuggestTour(CustomerRepository customerRepo, BookingRepository bookingRepo,
                       OfferingRepository offeringRepo, TourRepository tourRepo, TourCluster cluster){
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
        this.offeringRepo = offeringRepo;
        this.tourRepo = tourRepo;
        this.cluster = cluster;
    }

    /**
     * Init the cluster. Large computation involved.
     */
    public void initCluster(){
        cluster.clusterTour(false);
        cluster.printCluster();
    }

    /**
     * Obtain suggestion for this customer.
     * @param customer The customer to look at
     * @return List of suggested tours, possibly empty
     */
    public List<Tour> suggestForCustomer(Customer customer){
        Iterable<Booking> bookingIterable = bookingRepo.findByCustomer(customer);

        ArrayList<Tour> tourList = new ArrayList<>();
        ArrayList<float[]> vectorList = new ArrayList<>();
        bookingIterable.forEach(booking -> {
            vectorList.add(cluster.getCustomerVector(booking.getOffering().getTour()));
        });

        if (!vectorList.isEmpty()) {
            float[] averageVector = new float[vectorList.get(0).length];
            for (float[] v : vectorList) {
                for (int i = 0; i < averageVector.length; i++) {
                    averageVector[i] += v[i];
                }
            }
            for (int i = 0; i < averageVector.length; i++) {
                averageVector[i] /= vectorList.size();
            }
            INDArray vector = Nd4j.create(averageVector);
            Point pt = new Point(customer.getId().toString(), vector);
            Cluster c = cluster.getClusterWithPointPreferResult(pt);
            tourList = cluster.getToursInCluster(c);

            //Filter tours that this customer has already taken
            Collection<Booking> collection = Utils.iterableToCollection(bookingIterable);

            for (Booking booking : collection){
                if (tourList.contains(booking.getOffering().getTour())){
                    tourList.remove(booking.getOffering().getTour());
                }
            }

            if (tourList.size() > 3){
                return tourList.subList(0, 3);
            }
        }

        return tourList;
    }
}
