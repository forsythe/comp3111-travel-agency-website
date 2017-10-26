package comp3111.repo;

import comp3111.model.CustomerOffering;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface CustomerOfferingRepository extends CrudRepository<CustomerOffering, Long> {
	Collection<CustomerOffering> findByPaymentStatus(String paymentStatus);
}
