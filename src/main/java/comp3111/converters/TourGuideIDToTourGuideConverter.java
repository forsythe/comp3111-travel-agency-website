package comp3111.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.spring.annotation.SpringComponent;
import comp3111.model.TourGuide;
import comp3111.repo.TourGuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TourGuideIDToTourGuideConverter implements Converter<Long, TourGuide> {

    @Autowired
    private TourGuideRepository tourGuideRepo;

    @Override
    public Result<TourGuide> convertToModel(Long id, ValueContext context) {
        TourGuide found = tourGuideRepo.findOne(id);
        if (found != null) {
            return Result.ok(found);
        }else{
            return Result.error("Tour Guide not found!");
        }
    }

    @Override
    public Long convertToPresentation(TourGuide tourGuide, ValueContext context) {
        if (tourGuide == null) return 0L;
        return tourGuide.getId();
    }
}
