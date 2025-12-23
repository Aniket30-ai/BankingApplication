package com.nihilent.BankingApplication.NihilentBank.serviceTest;


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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.Roles;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.serviceimpl.CustomerServiceImpl;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;



    @BeforeEach
    void setUp() {


        customerService.invalidCredentials="Failed, Wrong EmailId or Password.";
    }

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


    @Test
    void testLoadUserByUsername_Success() {

        Customer customer = new Customer();
        customer.setEmailId("test@gmail.com");
        customer.setPassword("encodedPassword");

        customer.setRole(Roles.User);
        when(customerRepository.findByEmailId("test@gmail.com")).thenReturn(Optional.of(customer));

        assertNotNull(customerService.loadUserByUsername("test@gmail.com"));
    }


    @Test
    void testLoadUserByUsername_NotFound() {

        when(customerRepository.findByEmailId("notfound@gmail.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            customerService.loadUserByUsername("notfound@gmail.com");
        });
    }



    @Test
    void loadUserByUsername_customerNotFound_throwsException() {
        when(customerRepository.findByEmailId("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> customerService.loadUserByUsername("missing@example.com"));

        assertEquals("Account not found", exception.getMessage());
    }

    // ================= Password Null =================
    @Test
    void loadUserByUsername_passwordNull_throwsUsernameNotFoundException() {

        Customer customer= new Customer();

        customer.setEmailId("test@gmail.com");
        customer.setPassword(null);

        when(customerRepository.findByEmailId("test@example.com")).thenReturn(Optional.of(customer));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customerService.loadUserByUsername("test@example.com"));

        assertEquals("Failed, Wrong EmailId or Password.", exception.getMessage());
    }

    // ================= Email Null =================
    @Test
    void loadUserByUsername_emailNull_throwsUsernameNotFoundException() {


        Customer customer = new Customer();
        customer.setPassword("password123");

        customer.setEmailId(null);
        when(customerRepository.findByEmailId("test@example.com")).thenReturn(Optional.of(customer));

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> customerService.loadUserByUsername("test@example.com"));

        assertEquals("Failed, Wrong EmailId or Password.", exception.getMessage());
    }



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


    @Test
    void testGetCustomerDetails_NotFound() {

        when(customerRepository.findByMobileNumber(123L)).thenReturn(Optional.empty());

        assertThrows(NihilentBankException.class, () -> {
            customerService.getCustomerDetails(123L);
        });
    }

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

    @Test
    void testShowAllCustomer_Empty() {

        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(NihilentBankException.class, () -> {
            customerService.showAllCustomer();
        });
    }


    @Test
    void testUpdateEmailId_Success() throws Exception {


        Customer customer = new Customer();
        customer.setMobileNumber(9876543210L);
        customer.setEmailId("old@gmail.com");
        customer.setAddress("Pune");
        customer.setName("Test User");


        when(customerRepository.findByMobileNumber(9876543210L)).thenReturn(Optional.of(customer));

        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> inv.getArgument(0));


        ReflectionTestUtils.setField(customerService, "customerUpdate", "Customer Updated Successfully");


        String result = customerService.updateEmailId("new@gmail.com", 9876543210L);


        assertEquals("Customer Updated Successfully", result);
    }


    @Test
    void testUpdateEmailId_NotFound() {

        when(customerRepository.findByMobileNumber(888L)).thenReturn(Optional.empty());

        assertThrows(NihilentBankException.class, () -> {
            customerService.updateEmailId("x@gmail.com", 888L);
        });
    }







    @Test
    void generateCustomerId_firstCustomer()  {
        when(customerRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.empty());



        String s = customerService.generateCustomerId();

        assertEquals("CH001", s);
    }

    @Test
    void generateCustomerId_nextCustomer() {
        Customer lastCustomer = new Customer();
        lastCustomer.setCustomerId("CH005");

        when(customerRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.of(lastCustomer));



      String s=  customerService.generateCustomerId();

        assertEquals("CH006", s);
    }

    @Test
    void generateCustomerId_invalidFormat_fallbackToCH001()  {
        Customer lastCustomer = new Customer();
        lastCustomer.setCustomerId("INVALID");

        when(customerRepository.findTopByOrderByIdDesc())
                .thenReturn(Optional.of(lastCustomer));



      String s=  customerService.generateCustomerId();

        assertEquals("CH001", s);
    }
}



