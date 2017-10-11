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

	private static final String NAME = "Marcin";
	private static final int AGE = 20;

	@Mock
	CustomerRepository customerRepository;
	@InjectMocks
	CustomerEditor editor;

	@Test
	public void shouldStoreCustomerInRepoWhenEditorSaveClicked() {
		this.editor.getName().setValue(NAME);
		this.editor.getAge().setValue(Integer.toString(AGE));
		customerDataWasFilled();

		this.editor.getSave().click();

		then(this.customerRepository).should().save(argThat(customerMatchesEditorFields()));
	}

	@Test
	public void shouldDeleteCustomerFromRepoWhenEditorDeleteClicked() {
		this.editor.getName().setValue(NAME);
		this.editor.getAge().setValue(Integer.toString(AGE));
		customerDataWasFilled();

		editor.getDelete().click();

		then(this.customerRepository).should().delete(argThat(customerMatchesEditorFields()));
	}

	private void customerDataWasFilled() {
		this.editor.editCustomer(new Customer(NAME, Integer.toString(AGE)));
	}

	private TypeSafeMatcher<Customer> customerMatchesEditorFields() {
		return new TypeSafeMatcher<Customer>() {
			@Override
			public void describeTo(Description description) {
			}

			@Override
			protected boolean matchesSafely(Customer item) {
				return NAME.equals(item.getName()) && AGE==item.getAge();
			}
		};
	}

}
