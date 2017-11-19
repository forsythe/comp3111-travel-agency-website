package unit;

import comp3111.Application;
import comp3111.Utils;
import comp3111.data.DBManager;
import comp3111.data.model.*;
import comp3111.data.repo.*;
import comp3111.suggest.SuggestTour;
import comp3111.suggest.TourCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.rmi.CORBA.Util;
import java.awt.print.Book;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SuggestionTests {
    @Autowired
    SuggestTour suggestTour;
    @Autowired
    TourRepository tourRepo;
    @Autowired
    CustomerRepository customerRepo;
    @Autowired
    BookingRepository bookingRepo;
    @Autowired
    OfferingRepository offeringRepo;
    @Autowired
    DBManager dbManager;
    @Autowired
    TourGuideRepository tourGuideRepo;

    @Before
    public void setUp() throws Exception {
        dbManager.deleteAll();
        populateDatabase();
    }

    @After
    public void tearDown() {
        dbManager.deleteAll();
    }

    private void createBookingForCustomer(Offering o, Customer c){
        Booking b = new Booking();
        b.setCustomer(c);
        b.setOffering(o);
        try {
            dbManager.createNormalBookingForOffering(b);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    Offering sciOffering[] = new Offering[3];
    Offering hisOffering[] = new Offering[3];
    Offering shoOffering[] = new Offering[3];
    private void populateDatabase(){
        //Like science
        Customer sciCustomer[] = new Customer[4];
        for (int i=0; i<sciCustomer.length; i++){
            sciCustomer[i] = new Customer();
            customerRepo.save(sciCustomer[i]);
        }

        //Like history
        Customer hisCustomer[] = new Customer[4];
        for (int i=0; i<hisCustomer.length; i++){
            hisCustomer[i] = new Customer();
            customerRepo.save(hisCustomer[i]);
        }

        //Like shopping
        Customer shoCustomer[] = new Customer[4];
        for (int i=0; i<shoCustomer.length; i++){
            shoCustomer[i] = new Customer();
            customerRepo.save(shoCustomer[i]);
        }

        TourGuide tourGuide = new TourGuide();
        tourGuideRepo.save(tourGuide);

        //Science tour
        Tour sciTour[] = new Tour[3];
        for (int i=0; i<sciTour.length; i++){
            sciTour[i] = new Tour();
            sciTour[i].setAllowedDaysOfWeek(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
            sciTour[i].setDays(1);
        }

        sciTour[0].setTourName("Science tour in HK");
        sciTour[0].setDescription("This is a science trip for you to explore the space museum and the science museum" +
                " and Observatory in Hong Kong. ");

        sciTour[1].setTourName("One day trip to Mars");
        sciTour[1].setDescription("Do you want to explore the beauty of space? Do you want to take a spaceship and travel" +
                "to Mars in just one day? You must not miss this opportunity! Explore the cosmo with us!");

        sciTour[2].setTourName("Rocket Design Workshop");
        sciTour[2].setDescription("Everyone can be a rocket scientist! This is true rocket science, right in your face!" +
                "Join our workshop, and you will have a chance to build your own spaceship and rocket in the HK" +
                "science museum. ");

        for (Tour t : sciTour){
            tourRepo.save(t);
        }

        //History tour
        Tour hisTour[] = new Tour[3];
        for (int i=0; i<hisTour.length; i++){
            hisTour[i] = new Tour();
            hisTour[i].setAllowedDaysOfWeek(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
            hisTour[i].setDays(1);
        }

        hisTour[0].setTourName("Visit the Hong Kong Museum of Coastal Defence");
        hisTour[0].setDescription("Hong Kong Museum of Coastal Defence is a famous museum in Hong Kong. In the past, it" +
                "was a coastal defense fortress. Back in 1941, the British army has defensed the Japanese army there." +
                " It is a great place to learn more about the history of Hong Kong.");

        hisTour[1].setTourName("Visit the Hong Kong History Museum");
        hisTour[1].setDescription("The Hong Kong History Museum has a large collection of the history of Hong Kong. " +
                "The exhibition includes Stone Age, times when Hong Kong was ruled by British and Japanese, and some" +
                "more recent times history.");

        hisTour[2].setTourName("Visit the Kowloon Walled City");
        hisTour[2].setDescription("The Kowloon Walled City was a very chaotic place in Hong Kong back in 1940s. " +
                "Since both the British and Chinese government have no control over this place, a lot of criminals, " +
                "drug dealers hid in this place.");

        for (Tour t : hisTour){
            tourRepo.save(t);
        }

        //Shopping tour
        Tour shoTour[] = new Tour[3];
        for (int i=0; i<shoTour.length; i++){
            shoTour[i] = new Tour();
            shoTour[i].setAllowedDaysOfWeek(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7));
            shoTour[i].setDays(1);
        }

        shoTour[0].setTourName("Shopping in Central");
        shoTour[0].setDescription("Central is on of the best place in Hong Kong for you to shop! While there are a lot of " +
                "high class clothing, jewelleries and watches, you can also enjoy 5-star restaurant if you are hungary! " +
                "The only thing is that they are all expensive!");

        shoTour[1].setTourName("Shopping in Diamond Hill");
        shoTour[1].setDescription("Diamond Hill has a good mall! It is not as expensive as in Central, you can still have" +
                "good quality electronic gadgets and clothing if you can pay the price!");

        shoTour[2].setTourName("CRAZY SHOPPING IN HKUST");
        shoTour[2].setDescription("You might not heard of it yet... In HKUST, they have this really good shop called the" +
                "souvenir shop. They sell things that are essential to everyone's life like pens and water bottles" +
                "at a perfectly reasonable price!");

        for (Tour t : shoTour){
            tourRepo.save(t);
        }

        int dateCounter = 100;
        //Science offering
        sciOffering = new Offering[sciTour.length];
        for (int i=0; i<sciTour.length; i++){
            sciOffering[i] = new Offering();
            sciOffering[i].setStartDate(Utils.addDate(LocalDate.now(), dateCounter));
            sciOffering[i].setTour(sciTour[i]);
            sciOffering[i].setTourGuide(tourGuide);
            sciOffering[i].setStatus(Offering.STATUS_PENDING);
            sciOffering[i].setMinCustomers(0);
            sciOffering[i].setMaxCustomers(100);
            dateCounter += 2;
        }

        //History offering
        hisOffering = new Offering[hisTour.length];
        for (int i=0; i<hisTour.length; i++){
            hisOffering[i] = new Offering();
            hisOffering[i].setStartDate(Utils.addDate(LocalDate.now(), dateCounter));
            hisOffering[i].setTour(hisTour[i]);
            hisOffering[i].setTourGuide(tourGuide);
            hisOffering[i].setStatus(Offering.STATUS_PENDING);
            hisOffering[i].setMinCustomers(0);
            hisOffering[i].setMaxCustomers(100);
            dateCounter += 2;
        }

        //Shopping offerings
        shoOffering = new Offering[shoTour.length];
        for (int i=0; i<shoTour.length; i++){
            shoOffering[i] = new Offering();
            shoOffering[i].setStartDate(Utils.addDate(LocalDate.now(), dateCounter));
            shoOffering[i].setTour(shoTour[i]);
            shoOffering[i].setTourGuide(tourGuide);
            shoOffering[i].setStatus(Offering.STATUS_PENDING);
            shoOffering[i].setMinCustomers(0);
            shoOffering[i].setMaxCustomers(100);
            dateCounter += 2;
        }

        //Create offering for tour
        try {
            for (int i=0; i<sciOffering.length; i++){
                dbManager.createOfferingForTour(sciOffering[i]);
                dbManager.createOfferingForTour(hisOffering[i]);
                dbManager.createOfferingForTour(shoOffering[i]);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //Create bookings for customers
        for (int i=0; i<sciOffering.length; i++){
            for (Customer c : sciCustomer){
                createBookingForCustomer(sciOffering[i], c);
            }
            for (Customer c : hisCustomer){
                createBookingForCustomer(hisOffering[i], c);
            }

            for (Customer c : shoCustomer){
                createBookingForCustomer(shoOffering[i], c);
            }
        }

        //Introduces some variances, well nothing is perfect
        createBookingForCustomer(sciOffering[0], hisCustomer[0]);
        createBookingForCustomer(hisOffering[1], shoCustomer[1]);
        createBookingForCustomer(shoOffering[2], sciCustomer[2]);
    }

    @Test
    public void testTourCluster(){
        suggestTour.initCluster();

        //Create a test customer for suggestion
        Customer cus1 = new Customer();
        customerRepo.save(cus1);
        createBookingForCustomer(sciOffering[0], cus1);
        List<Tour> sug1 = suggestTour.suggestForCustomer(cus1);
        System.out.println(sug1);

        //Create a test customer for suggestion
        Customer cus2 = new Customer();
        customerRepo.save(cus2);
        createBookingForCustomer(hisOffering[1], cus2);
        List<Tour> sug2 = suggestTour.suggestForCustomer(cus2);
        System.out.println(sug2);

        //Create a test customer for suggestion
        Customer cus3 = new Customer();
        customerRepo.save(cus3);
        createBookingForCustomer(shoOffering[2], cus3);
        List<Tour> sug3 = suggestTour.suggestForCustomer(cus3);
        System.out.println(sug3);
    }
}
