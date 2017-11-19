package comp3111.suggest;

import com.vaadin.spring.annotation.SpringComponent;
import comp3111.data.model.Booking;
import comp3111.data.model.Customer;
import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;
import org.deeplearning4j.clustering.cluster.Cluster;
import org.deeplearning4j.clustering.cluster.ClusterSet;
import org.deeplearning4j.clustering.cluster.Point;
import org.deeplearning4j.clustering.kmeans.KMeansClustering;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.sentenceiterator.labelaware.LabelAwareListSentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
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
    private ParagraphVectors paragraphVectors;

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
                INDArray vector = Nd4j.create(customerVector);
                Point pt = new Point(tour.getTourName(), vector);
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
        loadDoc2VecModel();
        secondCluster();
    }

    /**
     * Create the second cluster together with Doc2Vec information
     */
    private void secondCluster(){
        KMeansClustering kmc = KMeansClustering.setup(clusterCount, maxIterationCount, distanceMeasure);

        List<Point> pointsLst = new ArrayList<>();
        generateCustomerTreeMap();

        {
            Iterable<Tour> tourIterable = tourRepo.findAll();
            tourIterable.forEach(tour -> {
                float customerVector[] = getCustomerVector(tour);
                INDArray desVec = paragraphVectors.inferVector(tour.getDescription());
                INDArray vector = Nd4j.hstack(Nd4j.create(customerVector), desVec.div(200));
                Point pt = new Point(tour.getTourName(), vector);
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

    /**
     * Load the Doc2Vec Model, either from file, or train it from scratch
     */
    private void loadDoc2VecModel() {
        List<Cluster> clusterList = tourClusterSet.getClusters();

        //Put all the labels into a string
        StringBuilder stringBuilder = new StringBuilder();
        for (Cluster c : clusterList){
            ArrayList<Tour> list = clusterIDToTour.get(c.getId());
            for (Tour tour : list) {
                stringBuilder.append(c.getId());
                stringBuilder.append("\t");
                stringBuilder.append(tour.getTourName());
                stringBuilder.append(": ");
                stringBuilder.append(tour.getDescription());
                stringBuilder.append("\n");
            }
        }
        String resultString = stringBuilder.toString();
        InputStream stream = new ByteArrayInputStream(resultString.getBytes(Charset.forName("UTF-8")));
        try {
            LabelAwareListSentenceIterator iterator = new LabelAwareListSentenceIterator(stream, "\t", 0, 1);

            TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

            /* Uncomment this part to train the model
            paragraphVectors = new ParagraphVectors.Builder()
                    .learningRate(0.03)
                    .minLearningRate(0.01)
                    .batchSize(1)
                    .epochs(100)
                    .layerSize(6)
                    .iterate(iterator)
                    .trainWordVectors(true)
                    .tokenizerFactory(tokenizerFactory)
                    .build();
            paragraphVectors.fit();
            WordVectorSerializer.writeParagraphVectors(paragraphVectors, "weight.txt");
            */

            //Comment this part to train the model
            paragraphVectors = WordVectorSerializer.readParagraphVectors("weight.txt");
            paragraphVectors.setTokenizerFactory(tokenizerFactory);
            paragraphVectors.getConfiguration().setIterations(1);
            //Comment til here

            INDArray origS1 = paragraphVectors.inferVector("One day trip to Mars: Do you want to explore the beauty of space? Do you want to take a spaceship and travel" +
                    "to Mars in just one day? You must not miss this opportunity! Explore the cosmo with us!");
            INDArray origS2 = paragraphVectors.inferVector("Rocket Design Workshop: Everyone can be a rocket scientist! This is true rocket science, right in your face!" +
                    "Join our workshop, and you will have a chance to build your own spaceship and rocket in the HK" +
                    "science museum. ");
            INDArray origH1 = paragraphVectors.inferVector("Visit the Hong Kong Museum of Coastal Defence: Hong Kong Museum of Coastal Defence is a famous museum in Hong Kong. In the past, it" +
                    "was a coastal defense fortress. Back in 1941, the British army has defensed the Japanese army there." +
                    " It is a great place to learn more about the history of Hong Kong.");

            log.info(String.valueOf(origS1.length()));
            log.info("Cosine similarity S1-S2: {}", Transforms.cosineSim(origS1, origS2));
            log.info("Cosine similarity S1-H1: {}", Transforms.cosineSim(origS1, origH1));
            log.info("Cosine similarity S2-H1: {}", Transforms.cosineSim(origS2, origH1));

//            INDArray arrayA = paragraphVectors.inferVector("Rocket science trip.");
//            INDArray arrayB = paragraphVectors.inferVector("Go shopping in a good mall.");
//            INDArray arrayC = paragraphVectors.inferVector("This trip let you look at the past stories in " +
//                    "Hong Kong, like the British and Japanese rule.");
//            INDArray arrayD = paragraphVectors.inferVector("Come visit the science museum.");
//            INDArray arrayE = paragraphVectors.inferVector("History let us learn about the past.");
//            INDArray arrayF = paragraphVectors.inferVector("Hong Kong is a place with short history.");
//
//            log.info("Cosine similarity 1 (Hi): {}", Transforms.cosineSim(arrayA, arrayD));
//            log.info("Cosine similarity 2 (Hi): {}", Transforms.cosineSim(arrayC, arrayE));
//            log.info("Cosine similarity 3 (Hi): {}", Transforms.cosineSim(arrayC, arrayF));
//            log.info("Cosine similarity 4 (Hi): {}", Transforms.cosineSim(arrayF, arrayE));
//
//            log.info("Cosine similarity 5 (Lo): {}", Transforms.cosineSim(arrayA, arrayB));
//            log.info("Cosine similarity 6 (Lo): {}", Transforms.cosineSim(arrayC, arrayD));
//            log.info("Cosine similarity 7 (Lo): {}", Transforms.cosineSim(arrayA, arrayE));
//            log.info("Cosine similarity 8 (Lo): {}", Transforms.cosineSim(arrayB, arrayD));
//            log.info("Cosine similarity 9 (Lo): {}", Transforms.cosineSim(arrayB, arrayF));
//            log.info("Cosine similarity 10 (Lo): {}", Transforms.cosineSim(arrayC, arrayB));
        }catch (IOException e){
            e.printStackTrace();
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
        log.info("Cluster Classification:");

        Iterable<Tour> tourIterable = tourRepo.findAll();
        tourIterable.forEach(tour -> {
            Cluster cluster = getClusterForTour(tour);
            log.info(tour.getTourName() + " " + cluster.getId());
        });
    }
}
