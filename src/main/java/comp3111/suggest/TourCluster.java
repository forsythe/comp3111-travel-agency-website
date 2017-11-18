package comp3111.suggest;

import com.vaadin.spring.annotation.SpringComponent;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.*;
import comp3111.input.auth.Authentication;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.IntStream;

@SpringComponent
public class TourCluster {

    private final static int clusterCount = 5;
    private final static int maxIterationCount = 5;
    private final static String distanceMeasure = "cosinesimilarity";

    private CustomerRepository customerRepo;
    private BookingRepository bookingRepo;
    private OfferingRepository offeringRepo;
    private TourRepository tourRepo;

    @Autowired
    public TourCluster(CustomerRepository customerRepo, BookingRepository bookingRepo,
                       OfferingRepository offeringRepo, TourRepository tourRepo){
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
        this.offeringRepo = offeringRepo;
        this.tourRepo = tourRepo;
    }

    public void clusterTour(){
        KMeansClustering kmc = KMeansClustering.setup(clusterCount, maxIterationCount, distanceMeasure);

        List<Point> pointsLst = new ArrayList<>();

        //Re-labeling all the customers with IDs, acting as inverted index
        TreeMap<Customer, Integer> customerID = new TreeMap<>();
        {
            Iterable<Customer> customerIterable = customerRepo.findAll();
            ArrayList<Customer> tempList = new ArrayList<>();
            customerIterable.forEach(tempList::add);
            IntStream.range(0, tempList.size())
                    .forEach(index -> {
                        customerID.put(tempList.get(index), index);
                    });
        }

        Iterable<Tour> tourIterable = tourRepo.findAll();
        tourIterable.forEach(tour -> {
            float customerVector[] = new float[customerID.size()];
            Iterable<Offering> offeringsIterable = offeringRepo.findByTour(tour);
            offeringsIterable.forEach(offering -> {
                Iterable<Booking> bookingIterable = bookingRepo.findByOffering(offering);
                bookingIterable.forEach(booking -> {
                    //Finding all the customers in all the offerings of a tour
                    int customerId = customerID.get(booking.getCustomer());
                    customerVector[customerId] += 1.0f;
                });
            });
            //Adding the vector to the point matrix
            INDArray vector = Nd4j.create(customerVector);
            pointsLst.add(new Point(tour.getTourName(), vector));
        });

        ClusterSet cs = kmc.applyTo(pointsLst);
        List<Cluster> clusterLst = cs.getClusters();

        System.out.println("\nCluster Centers:");
        for(Cluster c: clusterLst) {
            Point center = c.getCenter();
            System.out.println(center.getId());
        }
    }
}
