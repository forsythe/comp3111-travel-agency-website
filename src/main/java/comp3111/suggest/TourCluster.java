package comp3111.suggest;

import com.vaadin.spring.annotation.SpringComponent;
import comp3111.data.DBManager;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.*;
import comp3111.input.auth.Authentication;
import comp3111.input.validators.ValidatorFactory;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.cluster.PointClassification;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.IntStream;

/**
 * A Class to cluster all the tours into clusters for the recommendation engine.
 */
@SpringComponent
public class TourCluster {

    private final static int clusterCount = 3;
    private final static int maxIterationCount = 200;
    private final static String distanceMeasure = "cosinesimilarity";

    private CustomerRepository customerRepo;
    private BookingRepository bookingRepo;
    private OfferingRepository offeringRepo;
    private TourRepository tourRepo;

    private ClusterSet tourClusterSet;
    private TreeMap<Long, Integer> customerID;
    private TreeMap<String, ArrayList<Tour>> clusterIDToTour;

    private static final Logger log = LoggerFactory.getLogger(TourCluster.class);

    /**
     * Create a tour cluster. All autowired.
     * @param customerRepo Customer repo
     * @param bookingRepo Booking repo
     * @param offeringRepo Offering repo
     * @param tourRepo Tour repo
     */
    @Autowired
    public TourCluster(CustomerRepository customerRepo, BookingRepository bookingRepo,
                       OfferingRepository offeringRepo, TourRepository tourRepo){
        this.customerRepo = customerRepo;
        this.bookingRepo = bookingRepo;
        this.offeringRepo = offeringRepo;
        this.tourRepo = tourRepo;
    }

    /**
     * Create a vector from customer for a tour. Each booking adds one to the customer vector.
     * @param tour The tour to be looked at for creation
     * @return float array with the size the same as the tree map
     */
    float[] getCustomerVector (Tour tour){
        float customerVector[] = new float[customerID.size()];
        Iterable<Offering> offeringsIterable = offeringRepo.findByTour(tour);
        offeringsIterable.forEach(offering -> {
            Iterable<Booking> bookingIterable = bookingRepo.findByOffering(offering);
            bookingIterable.forEach(booking -> {
                //Finding all the customers in all the offerings of a tour
                try {
                    int customerId = customerID.get(booking.getCustomer().getId());
                    customerVector[customerId] += 1.0f;
                }catch (Exception e){
                    //Customer might not in the tree, that's ok
                    log.info("Customer not in tree");
                }
            });
        });
        return customerVector;
    }

    /**
     * Generate a treemap for all the customers in the repo to act as inverted index
     */
    private void generateCustomerTreeMap(){
        //Re-labeling all the customers with IDs, acting as inverted index
        customerID = new TreeMap<>();

        Iterable<Customer> customerIterable = customerRepo.findAll();
        ArrayList<Customer> tempList = new ArrayList<>();
        customerIterable.forEach(tempList::add);
        IntStream.range(0, tempList.size())
                .forEach(index -> {
                    customerID.put(tempList.get(index).getId(), index);
                });
    }

    /**
     * Generate the cluster set based on information in the database
     */
    public void clusterTour(){
        KMeansClustering kmc = KMeansClustering.setup(clusterCount, maxIterationCount, distanceMeasure);

        List<Point> pointsLst = new ArrayList<>();
        generateCustomerTreeMap();

        {
            Iterable<Tour> tourIterable = tourRepo.findAll();
            tourIterable.forEach(tour -> {
                float customerVector[] = getCustomerVector(tour);
//            System.out.print(tour.getTourName());
//            System.out.println(Arrays.toString(customerVector));
                INDArray vector = Nd4j.create(customerVector);
                Point pt = new Point(tour.getTourName(), vector);
//            System.out.println(pt.getArray());
                pointsLst.add(pt);
            });
        }

        tourClusterSet = kmc.applyTo(pointsLst);
        clusterIDToTour = new TreeMap<>();
        List<Cluster> clusterList = tourClusterSet.getClusters();
        for(Cluster c: clusterList) {
            Point center = c.getCenter();
            System.out.print(center.getId());
            System.out.println(center.getArray());

            clusterIDToTour.put(c.getId(), new ArrayList<>());
        }

        {
            //Put all the tours into clusters
            Iterable<Tour> tourIterable = tourRepo.findAll();
            ArrayList<Tour> tempList = new ArrayList<>();
            tourIterable.forEach(tempList::add);
            IntStream.range(0, tempList.size())
                    .forEach(index -> {
                        clusterIDToTour.get(getClusterForTour(tempList.get(index)).getId()).add(tempList.get(index));
                    });
        }
    }

    ArrayList<Tour> getToursInCluster(Cluster c){
        return clusterIDToTour.get(c.getId());
    }

    Cluster getClusterWithPoint(Point pt){
        List<Cluster> clusterList = tourClusterSet.getClusters();
        double max = 0.0;
        Cluster maxCluster = clusterList.get(0);
        for(Cluster c: clusterList) {
            double dis = c.getDistanceToCenter(pt);
            if (dis > max){
                max = dis;
                maxCluster = c;
            }
        }
        return maxCluster;
    }

    /**
     * Get the cluster that the tour belongs to
     * @param tour The tour to be searched
     * @return The cluster
     */
    private Cluster getClusterForTour(Tour tour){
        float customerVector[] = getCustomerVector(tour);
        INDArray vector = Nd4j.create(customerVector);
        Point pt = new Point(tour.getTourName(), vector);
        return getClusterWithPoint(pt);
    }

    /**
     * Printing the tour clustering out. For Debugging.
     */
    public void printCluster(){
        System.out.println("Cluster Classification:");

        Iterable<Tour> tourIterable = tourRepo.findAll();
        tourIterable.forEach(tour -> {
            Cluster cluster = getClusterForTour(tour);
            System.out.println(tour.getTourName() + " " + cluster.getId());
        });
    }
}
