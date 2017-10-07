package org.gradle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ConfigurableApplicationContext app = SpringApplication.run(Application.class, args);
		// Obtain the EmployeeRepository Bean
		EmpRepo repo = (EmpRepo) app.getBean("empRepo"); //convention: class name, but start lowercase

		// Create two POJO e and f
		Employee e = new Employee();
		Employee f = new Employee();
		e.setName("Peter");
		e.setSalary(1000.5f);
		f.setName("Robert");
		f.setSalary(500.4f);

		// Using the repository to save it into database
		repo.save(e);
		repo.save(f);

		System.out.println("How many people name Peter? Ans: " + repo.countByName("Peter"));
		System.out.println(e);
	}
}
