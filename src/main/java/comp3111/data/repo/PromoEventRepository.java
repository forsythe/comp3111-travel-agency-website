package comp3111.data.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;

/**
 * A repository storing PromoEvent objects
 * 
 * @author Forsythe
 *
 */
@Repository
public interface PromoEventRepository extends CrudRepository<PromoEvent, Long> {
	/**
	 * @param o
	 *            An offering
	 * @return A collection of PromoEvents associated with the provided offering
	 */
	Collection<PromoEvent> findByOffering(Offering o);

	/**
	 * @param promoCode
	 *            A promo code
	 * @return A PromoEvent with the specified promoCode. The promoCodes are unique,
	 *         so it returns one object only.
	 */
	PromoEvent findOneByPromoCode(String promoCode);

	/**
	 * @param b
	 *            A boolean reprsenting whether the PromoEvents have triggered or
	 *            not (true for already triggered PromoEvents)
	 * @return Returns all PromoEvents which have the specified isTriggered status.
	 *         If you provide false, then it returns all PromoEvents which have not
	 *         yet triggered.
	 */
	Collection<PromoEvent> findByIsTriggered(boolean b);
}
