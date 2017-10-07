package org.gradle;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

//Example modified from spring.io

//This will be AUTO IMPLEMENTED by Spring into a Bean called EmployeeRepository
//CRUD refers Create, Read, Update, Delete

@Component
public interface EmpRepo extends CrudRepository<Employee, Long>  {

	public String countByName(String string);
}

