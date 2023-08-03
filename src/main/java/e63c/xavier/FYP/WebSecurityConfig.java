/**
 * 
 * I declare that this code was written by me, xavier. 
 * I will not copy or allow others to copy my code. 
 * I understand that copying code is considered as plagiarism.
 * 
 * Student Name: Xavier
 * Student ID: 21020683
 * Class: E63C
 * Date created: 2023-Jan-18 12:26:25 am 
 * 
 */

package e63c.xavier.FYP;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Bean
	public MemberDetailsService memberDetailsService() {
		return new MemberDetailsService();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(memberDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		
		return authProvider;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.authenticationProvider(authenticationProvider());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    http.authorizeRequests()
	    	.antMatchers("/forgot_password", "/forgot_password_form", "/reset_password").permitAll()
	        .antMatchers("/categories", "/categories/add", "/categories/edit/*", "/categories/save", "/categories/delete/*").hasAnyRole("ADMIN", "SELLER")
	        .antMatchers("/products/add", "/products/edit/*", "/products/save", "/products/delete/*").hasRole("SELLER")
	        .antMatchers("/members/add/*", "/members/edit/*", "/members/save", "/members/delete/*", "/members", "/generatead").hasRole("ADMIN")
	        .antMatchers("/", "/contactus", "/products", "/purchase_history").hasAnyRole("BUYER", "SELLER")
	        .antMatchers("/", "/contactus", "/products", "/categories").hasAnyRole("SELLER", "ADMIN")
	        .antMatchers("/", "/products", "/contactus", "/success2", "/signupsuccess", "/failure").permitAll()
	        .antMatchers("/bootstrap/*/*").permitAll()
	        .antMatchers("/images/*").permitAll()
	        .antMatchers("/aboutus").access("hasAnyRole('BUYER', 'SELLER') or not hasRole('BANNED')")
	        .antMatchers("/banned").hasRole("BANNED")
	        .anyRequest().authenticated()
	        .and()
	        .formLogin().loginPage("/login").permitAll()
	        .and()
	        .formLogin().loginPage("/signup").permitAll()
	        .and()
	        .logout().logoutUrl("/logout").permitAll()
	        .and()
	        .exceptionHandling()
	        .accessDeniedHandler((request, response, accessDeniedException) -> {
	            if (request.isUserInRole("BANNED")) {
	                response.sendRedirect("/banned");
	                String script = "<script>alert('You are banned!');</script>";
	                response.getWriter().write(script);
	            } else {
	                response.sendRedirect("/403");
	            }
	        });
	}



}