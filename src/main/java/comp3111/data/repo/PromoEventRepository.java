package comp3111.data.repo;

import java.util.Collection;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import comp3111.data.model.Offering;
import comp3111.data.model.PromoEvent;

@Repository
public interface PromoEventRepository extends CrudRepository<PromoEvent, Long> {
	Collection<PromoEvent> findByOffering(Offering o);

	PromoEvent findOneByOffering(Offering o);

	PromoEvent findOneByPromoCode(String promoCode);

	Collection<PromoEvent> findByIsTriggered(boolean b);
}
