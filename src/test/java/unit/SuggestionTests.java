package unit;

import comp3111.data.DBManager;
import comp3111.data.repo.BookingRepository;
import comp3111.data.repo.CustomerRepository;
import comp3111.data.repo.OfferingRepository;
import comp3111.data.repo.TourRepository;
import comp3111.suggest.TourCluster;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SuggestionTests {
    @Mock
    TourRepository tourRepo;
    @Mock
    CustomerRepository customerRepo;
    @Mock
    BookingRepository bookingRepo;
    @Mock
    OfferingRepository offeringRepo;
    @Mock
    DBManager dbManager;
    @InjectMocks
    TourCluster tourCluster;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTourCluster(){
        tourCluster.clusterTour();
    }
}
