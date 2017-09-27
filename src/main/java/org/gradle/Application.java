package org.gradle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
    	//Obtain the EmployeeRepository Bean
        EmployeeRepository repo = (EmployeeRepository) SpringApplication.run(Application.class, args).getBean("employeeRepository");
        
        //Create two POJO e and f
        Employee e=new Employee();  
        Employee f=new Employee();
        e.setName("Peter");
        e.setSalary(1000.5f);
        f.setName("Robert");
        f.setSalary(500.4f);
        
        //Using the repository to save it into database
        repo.save(e);  
        repo.save(f);
        
        System.out.println("How many people name Peter? Ans: " + repo.countByName("Peter"));

    }

}
