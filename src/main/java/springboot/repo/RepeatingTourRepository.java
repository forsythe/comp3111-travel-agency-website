package springboot.repo;

import springboot.model.RepeatingTour;

import javax.transaction.Transactional;

//http://blog.netgloo.com/2014/12/18/handling-entities-inheritance-with-spring-data-jpa/
@Transactional // https://dzone.com/articles/how-does-spring-transactional
public interface RepeatingTourRepository extends TourBaseRepository<RepeatingTour> {

}
