package comp3111.data.repo;

import comp3111.data.model.TourGuide;
import org.springframework.stereotype.Repository;

/**
 * A repository storing tour guide objects
 * @author Forsythe
 *
 */
@Repository
public interface TourGuideRepository extends PersonBaseRepository<TourGuide> {

}
