package org.gradle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

	@Autowired
	private EmployeeRepository repo;

	@RequestMapping("/")
	public String index() {
		return "it worked!";
	}

	@RequestMapping("/employees")
	public String employeeList() {
		String s = "";

		for (Employee e : repo.findAll()) {
			s += e.getName() + ": " + e.getSalary() + "\n";
		}
		return s;
	}
}
