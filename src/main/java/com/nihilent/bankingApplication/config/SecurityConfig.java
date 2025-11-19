package com.nihilent.bankingApplication.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nihilent.bankingApplication.filter.JwtFilter;
import com.nihilent.bankingApplication.utility.CustomAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	
	
	private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

	private final UserDetailsService userDetailsService;

	private final JwtFilter jwtFilter;

	private final CustomAuthenticationEntryPoint authenticationEntryPoint;

	public SecurityConfig(UserDetailsService userDetailsService, JwtFilter jwtFilter,
			CustomAuthenticationEntryPoint authenticationEntryPoint) {
		this.userDetailsService = userDetailsService;
		this.jwtFilter = jwtFilter;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.cors().and()

				.csrf(httpSecurityConfig -> httpSecurityConfig.disable()).authorizeHttpRequests(authorizeRequest -> {

					try {
						authorizeRequest
								.requestMatchers("/NihilentBank/register", "/NihilentBank/auth", "/NihilentBank/**"
										)

								.permitAll()
								 .requestMatchers("/actuator/**").hasAuthority("Admin")
								.requestMatchers("/NihilentBank/admin/**").hasAuthority("Admin")
								.requestMatchers("/NihilentBank/user/**").hasAuthority("User").anyRequest()
								.authenticated().and().exceptionHandling()
								.authenticationEntryPoint(authenticationEntryPoint);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						   e.printStackTrace();
					}

				}).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

				.formLogin().and().httpBasic();

		return httpSecurity.build();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

		return daoAuthenticationProvider;

	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

}
