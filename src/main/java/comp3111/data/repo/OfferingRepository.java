package comp3111.data.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.Offering;
import comp3111.data.model.Tour;
import comp3111.data.model.TourGuide;

/**
 * A repository storing Offering objects
 * 
 * @author Forsythe
 *
 */
@Repository
public interface OfferingRepository extends CrudRepository<Offering, Long> {
	/**
	 * @param hotelName
	 *            A hotel name
	 * @return A collection of offerings which are associated with the provided
	 *         hotel name
	 */
	Collection<Offering> findByHotelName(String hotelName);

	/**
	 * @param t
	 *            A tour
	 * @return A collection of all offerings which were offered based on the
	 *         specified tour
	 */
	Collection<Offering> findByTour(Tour t);

	/**
	 * @param status
	 *            An offering status
	 * @return A collection of all offerings with the specified status.
	 * @see Offering#STATUS_CANCELLED
	 * @see Offering#STATUS_CONFIRMED
	 * @see Offering#STATUS_PENDING
	 */
	Collection<Offering> findByStatus(String status);

	/**
	 * @param tg
	 *            A tour guide
	 * @return A collection of offerings which have been led by the specified tour
	 *         guide
	 */
	Collection<Offering> findByTourGuide(TourGuide tg);
}
