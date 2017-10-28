package comp3111;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "comp3111.data", "comp3111.auth", "comp3111.data.model", "comp3111.view",
		"comp3111.data.repo", "comp3111.presenter", "comp3111.editors", "comp3111.exceptions", "comp3111.validators",
		"comp3111.converters" })
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
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
