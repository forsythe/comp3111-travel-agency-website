package comp3111.repo;

import javax.transaction.Transactional;

import comp3111.model.TourGuide;

//http://blog.netgloo.com/2014/12/18/handling-entities-inheritance-with-spring-data-jpa/
@Transactional // https://dzone.com/articles/how-does-spring-transactional
public interface TourGuideRepository extends PersonBaseRepository<TourGuide> {

}
