package comp3111;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Application point of entry
 * @author Forsythe
 *
 */
@SpringBootApplication
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}
	
	//Don't want scheduling on test cases, or else it causes race conditions!
	//Annotate all SPRING test classes with @SpringBootTest(properties = "scheduling.enabled=false")
	//Non-spring tests don't need this annotation.	
	@Configuration
    @ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true", matchIfMissing = true)
    @EnableScheduling
    static class SchedulingConfiguration {

    }

	// @Bean
	// public CommandLineRunner loadData(CustomerRepository repository) {
	// return (args) -> {
	// // save a couple of customers
	// repository.save(new Customer("Jack", 5));
	// repository.save(new Customer("JOhn", 5));
	// repository.save(new Customer("Bob", 5));
	// repository.save(new Customer("David", 5));
	//
	// // fetch all customers
	// log.info("Customers found with findAll():");
	// log.info("-------------------------------");
	// for (Customer customer : repository.findAll()) {
	// log.info(customer.toString());
	// }
	// log.info("");
	//
	// // fetch an individual customer by ID
	// Customer customer = repository.findOne(1L);
	// customer.setAge(999);
	// repository.save(customer);
	// log.info("saved updated customer");
	// };
	// }

}
