package com.jwt.authentication.interceptor;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.jwt.authentication.configuration.JwtAuthenticationToken;
import com.jwt.authentication.constants.SecurityConstants;

import feign.RequestInterceptor;
import feign.RequestTemplate;

@Configuration
public class FeignClientInterceptor implements RequestInterceptor {
	

	  @Override
	  public void apply(RequestTemplate requestTemplate) {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    requestTemplate.header(SecurityConstants.AUTHENTICATION_HEADER_NAME, String.format("%s %s", SecurityConstants.TOKEN_PREFIX, "suman"));
	    requestTemplate.header(SecurityConstants.AUTHENTICATION_HEADER_NAME, String.format("%s %s", SecurityConstants.TOKEN_PREFIX, "suman"));
	    
	   // if (authentication != null){
	    if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationToken) {
	    	JwtAuthenticationToken details = (JwtAuthenticationToken) authentication.getDetails();
	      requestTemplate.header("Authorization", details.getToken());
	    }
	  }

}
