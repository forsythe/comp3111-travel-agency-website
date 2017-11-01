package comp3111.input.converters;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import comp3111.data.model.Customer;
import comp3111.data.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerIDConverter implements Converter<Long, Customer> {

    @Autowired
    private CustomerRepository customerRepo;

    @Override
    public Result<Customer> convertToModel(Long id, ValueContext context) {
        Customer found = customerRepo.findOne(id);
        if (found != null) {
            return Result.ok(found);
        }else{
            return Result.error("Customer not found!");
        }
    }

    @Override
    public Long convertToPresentation(Customer customer, ValueContext context) {
        if (customer == null) return 0L;
        return customer.getId();
    }
}
