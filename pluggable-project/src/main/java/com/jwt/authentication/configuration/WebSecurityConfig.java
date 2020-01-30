package com.jwt.authentication.configuration;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scb.cos.hk.sales.security.constants.SecurityConstants;
import com.scb.cos.hk.sales.security.filter.AuthenticationProcessingFilter;
import com.scb.cos.hk.sales.security.filter.GlobalAuthenticationFailureHandler;

@Configuration    
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Value("${auth.bypassAuth}")
    private Boolean bypassAuth = false;
    
    @Value("${auth.timeOffset}")
    private long timeOffset;    
    
    @Value("${countries.country}")
    private String country;
    
    @Value("${auth.useLocalJKSForPublicKey}")
    private boolean useLocalJKSForPublicKey; 
    
    @Autowired
    private GeneralAuthenticationEntryPoint unauthorizedHandler;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    
    @Autowired 
    private GlobalAuthenticationFailureHandler globalAuthenticationFailureHandler;
    
    @Autowired 
    private ObjectMapper objectMapper;
    
    // FOR JWT
    protected AuthenticationProcessingFilter jwtTokenAuthenticationProcessingFilter() throws Exception {
        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter("/**", timeOffset, country, useLocalJKSForPublicKey,objectMapper,globalAuthenticationFailureHandler);
        filter.setAuthenticationManager(this.authenticationManager);
        return filter;
    }
    
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(jwtAuthenticationProvider);
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    } 
	
	@Override
    public void configure(WebSecurity web) throws Exception {
		List<String> permitAllEndpointList = Arrays.asList(
				SecurityConstants.AUTHENTICATION_URL,
				SecurityConstants.REFRESH_TOKEN_URL,
				SecurityConstants.RESOURCES,
				SecurityConstants.UTILS,
				SecurityConstants.ACTUATOR,
				SecurityConstants.ERROR,
				SecurityConstants.SWAGGER_RESOURCE,
				SecurityConstants.SWAGGER_UI,
				SecurityConstants.SWAGGER_DOCS,
				SecurityConstants.SWAGGER_PUBLIC_API,
				SecurityConstants.JARS
	        );
		
        if (bypassAuth) {
            web.ignoring().antMatchers("/**");
        } else {
        	web.ignoring().antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()]));
        }
    }
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
        
        http.csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()        
            .authorizeRequests()
                .antMatchers("/**").permitAll()
                .and()
            .addFilterBefore(jwtTokenAuthenticationProcessingFilter(),
                    UsernamePasswordAuthenticationFilter.class)
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//            .addFilterAfter(buildPolicyProcessingFilter(), 
//                    UsernamePasswordAuthenticationFilter.class);            
       // disabled page cache
       http.headers().cacheControl();
    }

}
