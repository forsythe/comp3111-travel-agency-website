package comp3111.repo;

import javax.transaction.Transactional;

import comp3111.model.LimitedTour;

//http://blog.netgloo.com/2014/12/18/handling-entities-inheritance-with-spring-data-jpa/
public interface LimitedTourRepository extends TourBaseRepository<LimitedTour> {

}
