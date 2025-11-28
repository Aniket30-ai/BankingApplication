package com.nihilent.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nihilent.bank.filter.JwtFilter;
import com.nihilent.bank.utility.CustomAuthenticationEntryPoint;

/**
 * Security configuration class for setting up Spring Security. It defines
 * authentication, authorization, JWT filter, and CORS/CSRF settings.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {



	private final JwtFilter jwtFilter;

	private final CustomAuthenticationEntryPoint authenticationEntryPoint;

	public SecurityConfig(JwtFilter jwtFilter,
			CustomAuthenticationEntryPoint authenticationEntryPoint) {

		this.jwtFilter = jwtFilter;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity
	        .csrf(csrf -> csrf.disable())
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers("/NihilentBank/register", "/NihilentBank/auth", "/NihilentBank/**").permitAll()
	            .requestMatchers("/actuator/**").hasAuthority("Admin")
	            .requestMatchers("/NihilentBank/admin/**").hasAuthority("Admin")
	            .requestMatchers("/NihilentBank/user/**").hasAuthority("User")
	            .anyRequest().authenticated()
	        )
	        .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
	        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}




	/**
	 * Provides the authentication manager used by Spring Security.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	/**
	 * Password encoder for hashing passwords securely. BCrypt is recommended for
	 * production.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

}
