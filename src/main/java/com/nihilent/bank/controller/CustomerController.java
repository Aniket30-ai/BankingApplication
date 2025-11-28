package com.nihilent.bank.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bank.dto.AuthRequest;
import com.nihilent.bank.dto.AuthResponse;
import com.nihilent.bank.dto.CustomerDto;
import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.exception.NihilentBankException;
import com.nihilent.bank.repository.CustomerRepository;
import com.nihilent.bank.service.CustomerService;
import com.nihilent.bank.utility.JwtUtil;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Controller responsible for handling all customer-related operations such as
 * registration, authentication, fetching customer details, and updating
 * profiles. Also integrates JWT-based authentication and Spring Security login
 * flow.
 */
@RestController
@RequestMapping(value = "NihilentBank")
@Validated
public class CustomerController {

	// Utility class for generating JWT tokens
	private final JwtUtil jwtUtil;

	// AuthenticationManager to validate username/password
	private final AuthenticationManager authenticationManager;

	// Repository for fetching Customer entities from the database
	private final CustomerRepository customerRepository;

	// Service layer for business logic related to customer operations
	private final CustomerService customerService;

	public CustomerController(CustomerRepository customerRepository, CustomerService customerService, JwtUtil jwtUtil,
			AuthenticationManager authenticationManager) {

		this.customerRepository = customerRepository;
		this.customerService = customerService;
		this.jwtUtil = jwtUtil;
		this.authenticationManager = authenticationManager;
	}

	// API for Customer Register
	@PostMapping("/register")
	public ResponseEntity<String> registerCustomer(@Valid @RequestBody CustomerDto customerDto)
			throws NihilentBankException {

		String registerCustomer = customerService.registerCustomer(customerDto);

		return new ResponseEntity<>(registerCustomer, HttpStatus.CREATED);
	}

	/**
	 * API for customer login/authentication. Validates username & password using
	 * Spring Security's AuthenticationManager, generates a JWT token, and returns
	 * additional customer details.
	 */
	@PostMapping("/auth")
	public ResponseEntity<Map<String, Object>> authenticate(@Valid @RequestBody AuthRequest request) {

		// Authenticate user credentials
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		Optional<Customer> byEmailId = customerRepository.findByEmailId(request.getUsername());

		Long mobileNumber=0l;
		
		if(byEmailId.isPresent()){
			
		 mobileNumber = byEmailId.get().getMobileNumber();
		}
		

		String name = byEmailId
		        .orElseThrow(() -> new RuntimeException("User not found"))
		        .getName();

		// Extract user role
		String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
				.orElse(null);

		// Generate JWT token
		String token = jwtUtil.generateToken(request.getUsername(), role);

		AuthResponse response = new AuthResponse();
		response.setToken(token);

		Map<String, Object> responses = new HashMap<>();
		responses.put("token", token);
		responses.put("roles", role);
		responses.put("mobileNumber", mobileNumber);
		responses.put("name", name);
		responses.put("message", "Login successful");

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + token);
		headers.set("X-Custom-Header", "SomeValue");
		headers.setCacheControl("no-cache");

		return ResponseEntity.ok().headers(headers).body(responses);

	}

	// Admin API to fetch all registered customers.
	@GetMapping("/admin/getCustomers")
	public ResponseEntity<List<CustomerDto>> showAllCustomers() throws NihilentBankException {

		List<CustomerDto> allCustomer = customerService.showAllCustomer();

		return new ResponseEntity<>(allCustomer, HttpStatus.OK);
	}

	// User API to fetch details of a customer using mobile number.
	@GetMapping("/user/getCustomer/{mobileNumber}")
	public ResponseEntity<CustomerDto> getCustomers(

			@PathVariable @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}") Long mobileNumber)
			throws NihilentBankException {
		CustomerDto customerDetails = customerService.getCustomerDetails(mobileNumber);

		return new ResponseEntity<>(customerDetails, HttpStatus.OK);
	}

	// API for users to update their email ID or mobile number.
	@PutMapping("/user/updateProfile/{emailId}/{mobileNumber}")
	public ResponseEntity<String> updateCustomer(
			@PathVariable @NotNull(message = "{customer.emailId.notPresent}") @Pattern(regexp = "^[a-z]+\\d*@[a-z]+\\.[a-z]{2,}$", message = "{customer.emailId.invalid}") String emailId,
			@PathVariable @Min(value = 1000000000L, message = "{customer.mobileNumber.invalid}") @Max(value = 9999999999L, message = "{customer.mobileNumber.invalid}") Long mobileNumber)
			throws NihilentBankException {

		String updateProfileDetails = customerService.updateEmailId(emailId, mobileNumber);

		return new ResponseEntity<>(updateProfileDetails, HttpStatus.OK);

	}

}
