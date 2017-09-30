## Demo code with:  
-Autowiring  
-Hibernate ORM  
-Spring boot  

## Setup:
In src/main/resources/application.properties, change the following to match your credentials (refer to lab):
  
`spring.datasource.url=jdbc:postgresql://ec2-54-235-177-45.compute-1.amazonaws.com:5432/dajph5pjtpcf0k?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory`
Keep the `?ssl=true...` stuff
 

`spring.datasource.username=wmulcztjbomlgq`  
`spring.datasource.password=51558e68b24797ef320fd80f633b425b2117da90ea1d768de39766b5885c8a61`

## Launch:
`gradle bootrun`
http://localhost:8080  
http://localhost:8080/employees
