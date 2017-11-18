package unit;

import comp3111.Application;
import comp3111.input.exceptions.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ExceptionTests {

    @Test
    public void testException (){
        //Well the constructors might throw exceptions right?
        //OK
        ColumnNameNotFoundException exception = new ColumnNameNotFoundException("e");
        NoSuchPromoCodeException exception1 = new NoSuchPromoCodeException();
        OfferingDateUnsupportedException exception2 = new OfferingDateUnsupportedException();
        OfferingOutOfRoomException exception3 = new OfferingOutOfRoomException();
        PromoCodeNotForOfferingException exception4 = new PromoCodeNotForOfferingException();
        PromoCodeUsedUpException exception5 = new PromoCodeUsedUpException();
        PromoForCustomerExceededException exception6 = new PromoForCustomerExceededException();
        TourGuideUnavailableException exception7 = new TourGuideUnavailableException();
        UsernameTakenException exception8 = new UsernameTakenException();
    }

}
