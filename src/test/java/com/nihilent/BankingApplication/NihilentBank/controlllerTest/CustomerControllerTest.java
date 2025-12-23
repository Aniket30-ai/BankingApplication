package com.nihilent.BankingApplication.NihilentBank.controlllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nihilent.bank.NihilentBankApplication;
import com.nihilent.bank.controller.CustomerController;
import com.nihilent.bank.dto.AuthRequest;
import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.Roles;
import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.service.CustomerService;
import com.nihilent.bank.utility.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(CustomerController.class)
@ContextConfiguration(classes = NihilentBankApplication.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private JwtFilter jwtFilter;


    @MockitoBean
    private AuthenticationManager authenticationManager;

    private CustomerDto customerDto;





    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setName("John");
        customerDto.setEmailId("john@gmail.com");
        customerDto.setPassword("password123");

        // Required fields
        customerDto.setEmailId("john@example.com");
        customerDto.setMobileNumber(1234567890l);
        customerDto.setAddress("123 Main Street");
        customerDto.setGender("Male");
        customerDto.setRoles(Roles.User); // If roles is List<String>
    }

    // -------------------------------------------------
    // POST /NihilentBank/register
    // -------------------------------------------------
    @Test
    void testRegisterCustomer_success() throws Exception {

        when(customerService.registerCustomer(any()))
                .thenReturn("Customer registered successfully");

        mockMvc.perform(post("/NihilentBank/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Customer registered successfully"));
    }


    @Test
    void testAuthenticate_success() throws Exception {
// Sample AuthRequest
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("john@example.com");
        authRequest.setPassword("password123");

// Sample Customer
        Customer customer = new Customer();
        customer.setName("John");
        customer.setEmailId("john@example.com");
        customer.setMobileNumber(1234567890L);
        customer.setRole(Roles.User);
        customer.setPassword("password123");

// Mock customerRepository
        when(customerRepository.findByEmailId(authRequest.getUsername()))
                .thenReturn(Optional.of(customer));

// Mock authenticationManager
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())))
                .thenReturn(new UsernamePasswordAuthenticationToken(
                        authRequest.getUsername(),
                        authRequest.getPassword(),
                        List.of(new SimpleGrantedAuthority("User"))));

// Mock JWT generation
        when(jwtUtil.generateToken(authRequest.getUsername(), "User"))
                .thenReturn("mocked-jwt-token");

        mockMvc.perform(post("/NihilentBank/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked-jwt-token"))
                .andExpect(jsonPath("$.roles").value("User"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.mobileNumber").value(1234567890L));
    }



    @Test
    void testShowAllCustomers_success() throws Exception {



        List<CustomerDto> customers = List.of(customerDto);

        when(customerService.showAllCustomer()).thenReturn(customers);

        mockMvc.perform(get("/NihilentBank/admin/getCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].emailId").value("john@example.com"))
                .andExpect(jsonPath("$[0].mobileNumber").value(1234567890L));
    }


    @Test
    void testGetCustomerByMobileNumber_success() throws Exception {
        when(customerService.getCustomerDetails(1234567890L)).thenReturn(customerDto);

        mockMvc.perform(get("/NihilentBank/user/getCustomer/{mobileNumber}", 1234567890L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.emailId").value("john@example.com"))
                .andExpect(jsonPath("$.mobileNumber").value(1234567890L));
    }

    @Test
    void testGetCustomerByMobileNumber_invalidMobile() throws Exception {
        // mobile number below min constraint
        mockMvc.perform(get("/NihilentBank/user/getCustomer/{mobileNumber}", 123L))
                .andExpect(status().isBadRequest());
    }



    @Test
    void testUpdateCustomer_success() throws Exception {
        String updatedMessage = "Profile updated successfully";
        when(customerService.updateEmailId("john@example.com", 1234567890L)).thenReturn(updatedMessage);

        mockMvc.perform(put("/NihilentBank/user/updateProfile/{emailId}/{mobileNumber}", "john@example.com", 1234567890L))
                .andExpect(status().isOk())
                .andExpect(content().string("Profile updated successfully"));
    }

    @Test
    void testUpdateCustomer_invalidEmail() throws Exception {
        mockMvc.perform(put("/NihilentBank/user/updateProfile/{emailId}/{mobileNumber}", "invalid-email", 1234567890L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateCustomer_invalidMobile() throws Exception {
        mockMvc.perform(put("/NihilentBank/user/updateProfile/{emailId}/{mobileNumber}", "john@example.com", 123L))
                .andExpect(status().isBadRequest());
    }


}

