package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Autowired
	UserRepository userRepository;

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	UserDetailsService userDetailsService() {
//		UserDetails user1 = User
//				.withUsername("user1")
//				.password(passwordEncoder().encode("password"))
//				.roles("USER")
//				.build();
//
//		return new InMemoryUserDetailsManager(user1);

		return new UserDetailsServiceImpl();
	}

	@Bean
	MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
		return new MvcRequestMatcher.Builder(introspector);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
//		XorCsrfTokenRequestAttributeHandler requestHandler = new XorCsrfTokenRequestAttributeHandler();
//		requestHandler.setCsrfRequestAttributeName(null);

		http
				// .csrf(csrf ->
				// csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests((requests) -> {
					requests
							// .requestMatchers(mvc.pattern("/")).permitAll()
							// .anyRequest().authenticated();
							.anyRequest().permitAll();
				})
				.formLogin((form) -> {
					form
							.loginPage("/login")
							.permitAll();
				})
				.logout((logout) -> logout.permitAll());
		return http.build();
	}
}