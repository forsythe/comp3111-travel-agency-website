package comp3111;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import comp3111.model.LoginUser;
import comp3111.repo.LoginUserRepository;

@SpringBootApplication
@ComponentScan(basePackages = { "comp3111.auth", "comp3111.model", "comp3111.view", "comp3111.repo",
		"comp3111.presenter" })
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class);
	}

	// @Bean
	// public CommandLineRunner loadData(CustomerRepository repository) {
	// return (args) -> {
	// // save a couple of customers
	// repository.save(new Customer("Jack", "Bauer"));
	// repository.save(new Customer("Chloe", "O'Brian"));
	// repository.save(new Customer("Kim", "Bauer"));
	// repository.save(new Customer("David", "Palmer"));
	// repository.save(new Customer("Michelle", "Dessler"));
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
	// log.info("Customer found with findOne(1L):");
	// log.info("--------------------------------");
	// log.info(customer.toString());
	// log.info("");
	//
	// // fetch customers by last name
	// log.info("Customer found with findByLastNameStartsWithIgnoreCase('Bauer'):");
	// log.info("--------------------------------------------");
	// for (Customer bauer : repository
	// .findByLastNameStartsWithIgnoreCase("Bauer")) {
	// log.info(bauer.toString());
	// }
	// log.info("");
	// };
	// }

}
