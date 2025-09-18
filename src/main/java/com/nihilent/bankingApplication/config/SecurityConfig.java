package com.nihilent.bankingApplication.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private CustomAuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity
		.cors().and()
		
		
	.	csrf(httpSecurityConfig -> httpSecurityConfig.disable())
				.authorizeHttpRequests(authorizeRequest -> {
					
						try {
							authorizeRequest.requestMatchers("/NihilentBank/register", "/NihilentBank/auth","/NihilentBank/**", "/swagger-ui/**",
							        "/v3/api-docs/**",
							        "/swagger-resources/**")
							
							
							.permitAll()
//						       .requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
									.requestMatchers("/NihilentBank/admin/**")
									.hasAuthority("Admin")
									.requestMatchers("/NihilentBank/user/**")
									.hasAuthority("User")
									.anyRequest().authenticated().and().exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				})
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

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
