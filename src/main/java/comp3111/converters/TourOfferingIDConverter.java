package comp3111.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import comp3111.model.Offering;
import comp3111.repo.OfferingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TourOfferingIDConverter implements Converter<Long, Offering> {

    @Autowired
    private OfferingRepository offeringRepo;

    @Override
    public Result<Offering> convertToModel(Long id, ValueContext context) {
        Offering found = offeringRepo.findOne(id);
        if (found != null) {
            return Result.ok(found);
        }else{
            return Result.error("Offering not found!");
        }
    }

    @Override
    public Long convertToPresentation(Offering offering, ValueContext context) {
        if (offering == null) return 0L;
        return offering.getId();
    }
}
