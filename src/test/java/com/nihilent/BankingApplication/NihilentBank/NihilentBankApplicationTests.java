package com.nihilent.BankingApplication.NihilentBank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.Roles;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.serviceimpl.CustomerServiceImpl;

//@ExtendWith(MockitoExtension.class)
class NihilentBankApplicationTests {

	@Mock
	private CustomerRepository customerRepository;

	@InjectMocks
	private CustomerServiceImpl customerService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 1 — Register Customer Successfully
	// -------------------------------------------------------------------------------------------------
	@Test
	void testRegisterCustomer_Success() throws Exception {

		CustomerDto dto = new CustomerDto();
		dto.setMobileNumber(9876543210L);
		dto.setEmailId("test@gmail.com");
		dto.setPassword("password123");
		dto.setName("Test User");
		dto.setGender("Male");
		dto.setAddress("Pune");

		when(customerRepository.findByMobileNumber(9876543210L)).thenReturn(Optional.empty());

		when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

		ReflectionTestUtils.setField(customerService, "customerRegister", "Customer Registered Successfully");

		String result = customerService.registerCustomer(dto);

		assertEquals("Customer Registered Successfully", result);
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 2 — Register Customer Already Exists (Exception)
	// -------------------------------------------------------------------------------------------------
	@Test
	void testRegisterCustomer_AlreadyPresent() {

		CustomerDto dto = new CustomerDto();
		dto.setMobileNumber(9999999999L);

		Customer existingCustomer = new Customer();
		existingCustomer.setMobileNumber(9999999999L);

		when(customerRepository.findByMobileNumber(dto.getMobileNumber())).thenReturn(Optional.of(existingCustomer));

		assertThrows(NihilentBankException.class, () -> {
			customerService.registerCustomer(dto);
		});

		verify(customerRepository, never()).save(any());
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 3 — LoadUserByUsername Success
	// -------------------------------------------------------------------------------------------------
	@Test
	void testLoadUserByUsername_Success() {

		Customer customer = new Customer();
		customer.setEmailId("test@gmail.com");
		customer.setPassword("encodedPassword");

		customer.setRole(Roles.User);
		when(customerRepository.findByEmailId("test@gmail.com")).thenReturn(Optional.of(customer));

		assertNotNull(customerService.loadUserByUsername("test@gmail.com"));
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 4 — LoadUserByUsername Not Found
	// -------------------------------------------------------------------------------------------------
	@Test
	void testLoadUserByUsername_NotFound() {

		when(customerRepository.findByEmailId("notfound@gmail.com")).thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> {
			customerService.loadUserByUsername("notfound@gmail.com");
		});
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 5 — Get Customer Details Success
	// -------------------------------------------------------------------------------------------------
	@Test
	void testGetCustomerDetails_Success() throws Exception {

		Customer customer = new Customer();
		customer.setMobileNumber(999L);
		customer.setName("Aniket");
		customer.setAddress("Pune");
		customer.setGender("Male");
		customer.setEmailId("test@gmail.com");

		when(customerRepository.findByMobileNumber(999L)).thenReturn(Optional.of(customer));

		CustomerDto dto = customerService.getCustomerDetails(999L);

		assertEquals("Aniket", dto.getName());
		assertEquals("Pune", dto.getAddress());
		assertEquals("test@gmail.com", dto.getEmailId());
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 6 — Get Customer Details — Not Found
	// -------------------------------------------------------------------------------------------------
	@Test
	void testGetCustomerDetails_NotFound() {

		when(customerRepository.findByMobileNumber(123L)).thenReturn(Optional.empty());

		assertThrows(NihilentBankException.class, () -> {
			customerService.getCustomerDetails(123L);
		});
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 7 — Show All Customer (Excluding Admin)
	// -------------------------------------------------------------------------------------------------
	@Test
	void testShowAllCustomer() throws Exception {

		Customer c1 = new Customer();
		c1.setName("User One");
		c1.setRole(Roles.User);

		Customer c2 = new Customer();
		c2.setName("Admin User");
		c2.setRole(Roles.Admin);

		List<Customer> list = new ArrayList<>();
		list.add(c1);
		list.add(c2);

		when(customerRepository.findAll()).thenReturn(list);

		List<CustomerDto> result = customerService.showAllCustomer();

		assertEquals(1, result.size());
		assertEquals("User One", result.get(0).getName());
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 8 — Show All Customer — Empty List (Throws Exception)
	// -------------------------------------------------------------------------------------------------
	@Test
	void testShowAllCustomer_Empty() {

		when(customerRepository.findAll()).thenReturn(new ArrayList<>());

		assertThrows(NihilentBankException.class, () -> {
			customerService.showAllCustomer();
		});
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 9 — Update Email Success
	// -------------------------------------------------------------------------------------------------
	@Test
	void testUpdateEmailId_Success() throws Exception {

		// mock customer
		Customer customer = new Customer();
		customer.setMobileNumber(9876543210L);
		customer.setEmailId("old@gmail.com");
		customer.setAddress("Pune");
		customer.setName("Test User");

		// mock repository behavior
		when(customerRepository.findByMobileNumber(9876543210L)).thenReturn(Optional.of(customer));

		when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));

		// Inject the @Value field manually
		ReflectionTestUtils.setField(customerService, "customerUpdate", "Customer Updated Successfully");

		// call method
		String result = customerService.updateEmailId("new@gmail.com", 9876543210L);

		// assert
		assertEquals("Customer Updated Successfully", result);
	}

	// -------------------------------------------------------------------------------------------------
	// ✔ TEST 10 — Update Email Not Found
	// -------------------------------------------------------------------------------------------------
	@Test
	void testUpdateEmailId_NotFound() {

		when(customerRepository.findByMobileNumber(888L)).thenReturn(Optional.empty());

		assertThrows(NihilentBankException.class, () -> {
			customerService.updateEmailId("x@gmail.com", 888L);
		});
	}

}
