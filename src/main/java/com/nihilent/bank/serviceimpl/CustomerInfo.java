package com.nihilent.bank.serviceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.nihilent.bank.entity.Customer;
import com.nihilent.bank.entity.Roles;

public class CustomerInfo implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String username;
	private String password;

	private Roles role;

	private List<GrantedAuthority> authorities;

	public CustomerInfo(Customer customer) {
		this.username = customer.getEmailId();
		this.password = customer.getPassword();

		this.role = customer.getRole();

		this.authorities =  new ArrayList<>(
			    List.of(customer.getRole().toString()).stream()
		        .map(SimpleGrantedAuthority::new)
		        .toList());

	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return authorities;
	}

	@Override
	public String getPassword() {
		
		return password;
	}

	@Override
	public String getUsername() {

		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
	
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
	
		return true;
	}

	@Override
	public boolean isEnabled() {
		
		return true;
	}

}
