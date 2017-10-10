package springboot;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import springboot.model.Customer;
import springboot.presenter.CustomerEditor;
import springboot.repo.CustomerRepository;

import static org.mockito.BDDMockito.*;
import static org.mockito.Matchers.argThat;

@RunWith(MockitoJUnitRunner.class)
public class CustomerEditorTests {

	private static final String FIRST_NAME = "Marcin";
	private static final String AGE_STRING = "20";

	@Mock CustomerRepository customerRepository;
	@InjectMocks CustomerEditor editor;

	@Ignore
	@Test
	public void shouldStoreCustomerInRepoWhenEditorSaveClicked() {
		this.editor.getFirstName().setValue(FIRST_NAME);
		this.editor.getAge().setValue(AGE_STRING);
		customerDataWasFilled();

		this.editor.getSave().click();

		then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()));
	}

	@Ignore
	@Test
	public void shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		this.editor.getFirstName().setValue(FIRST_NAME);
		this.editor.getAge().setValue(AGE_STRING);
		customerDataWasFilled();

		editor.getDelete().click();

		then(this.customerRepository).should().delete(argThat(customerMatchesEditorFields()));
	}

	private void customerDataWasFilled() {
		this.editor.editCustomer(new Customer(FIRST_NAME, Integer.parseInt(AGE_STRING)));
	}

	private TypeSafeMatcher<Customer> customerMatchesEditorFields() {
		return new TypeSafeMatcher<Customer>() {
			@Override
			public void describeTo(Description description) {}

			@Override
			protected boolean matchesSafely(Customer item) {
				return FIRST_NAME.equals(item.getName()) && AGE_STRING.equals(item.getAge());
			}
		};
	}

}
