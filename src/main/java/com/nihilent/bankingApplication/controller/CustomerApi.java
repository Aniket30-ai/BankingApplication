package com.nihilent.bankingApplication.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nihilent.bankingApplication.dto.AuthRequest;
import com.nihilent.bankingApplication.dto.AuthResponse;
import com.nihilent.bankingApplication.dto.CustomerDto;
import com.nihilent.bankingApplication.entity.Customer;
import com.nihilent.bankingApplication.exception.NihilentBankException;
import com.nihilent.bankingApplication.repository.CustomerRepository;
import com.nihilent.bankingApplication.serviceImpl.CustomerServiceImpl;
import com.nihilent.bankingApplication.utility.JwtUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "NihilentBank")
@Validated
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerApi {

	@Autowired
	private CustomerServiceImpl customerService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomerRepository customerRepository;

	@PostMapping("/register")
	public ResponseEntity<String> registerCustomer(@RequestBody CustomerDto customerDto) throws NihilentBankException {

		System.out.println(customerDto);

		String registerCustomer = customerService.registerCustomer(customerDto);

		return new ResponseEntity<String>(registerCustomer, HttpStatus.CREATED);
	}

	@GetMapping("user/welcome")

	public ResponseEntity<String> home() {

		return new ResponseEntity<String>("Welcome user page", HttpStatus.OK);
	}

	@GetMapping("/admin")

	public ResponseEntity<String> admin() {

		return new ResponseEntity<String>("welcome to admin page", HttpStatus.OK);
	}

	@PostMapping("/auth")
	public ResponseEntity<Map<String, Object>> authenticate(@RequestBody AuthRequest request) {

		System.out.println(request.getPassword());
		System.out.println(request.getUsername());
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

//		String token = jwtUtil.generateToken(request);

		Optional<Customer> byEmailId = customerRepository.findByEmailId(request.getUsername());

		Long mobileNumber = byEmailId.get().getMobileNumber();

		String name = byEmailId.get().getName();

		String role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).findFirst()
				.orElse(null);

		String token = jwtUtil.generateToken(request.getUsername(), role);

//		Collection<? extends GrantedAuthority> roles = authentication.getAuthorities();
//
//	    // Option 2: Check for specific role
//	    boolean isAdmin = authentication.getAuthorities().stream()
//	            .anyMatch(a -> a.getAuthority().equals("role"));

		AuthResponse response = new AuthResponse();
		response.setToken(token);
//		response.setMessage("Login Successfully");

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
//	    headers.set("", );

//	    response.put
		// Return response entity with body + headers + status
//	    return new ResponseEntity<Map<String, String>>(response.set, HttpStatus.OK)
//	            .headers(headers);

		return ResponseEntity.ok().headers(headers).body(responses);
//		return new ResponseEntity<Map<String, String>>(response, HttpStatus.OK);

	}

	@GetMapping("/admin/getCustomers")
	public ResponseEntity<List<CustomerDto>> showAllCustomers() throws NihilentBankException {

		List<CustomerDto> allCustomer = customerService.showAllCustomer();

		return new ResponseEntity<List<CustomerDto>>(allCustomer, HttpStatus.OK);
	}

	@PostMapping("/login")
	public ResponseEntity<CustomerDto> login(@RequestBody AuthRequest authRequest) throws NihilentBankException {

		CustomerDto login = customerService.login(authRequest.getUsername(), authRequest.getPassword());

		return new ResponseEntity<CustomerDto>(login, HttpStatus.OK);
	}

	@GetMapping("/user/getCustomer/{mobileNumber}")
	public ResponseEntity<CustomerDto> getCustomers(@PathVariable Long mobileNumber) throws NihilentBankException {

		
		System.out.println(mobileNumber);
		CustomerDto customerDetails = customerService.getCustomerDetails(mobileNumber);

		return new ResponseEntity<CustomerDto>(customerDetails, HttpStatus.OK);
	}

	
	
	
	@PutMapping("/user/updateProfile/{emailId}/{mobileNumber}")
	public ResponseEntity<String> updateCustomer(@PathVariable String emailId,@PathVariable Long mobileNumber) throws NihilentBankException {

		
		System.out.println(emailId);
		System.out.println(mobileNumber);
		String updateProfileDetails = customerService.updateEmailId(emailId,mobileNumber);

		return new ResponseEntity<String>(updateProfileDetails, HttpStatus.OK);

	}

}
