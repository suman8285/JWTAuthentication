package com.scb.cos.hk.sales.security.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class GlobalAuthenticationFailureHandler implements AuthenticationFailureHandler {

	  //private final ObjectMapper mapper;
	  private ObjectMapper objectMapper = new ObjectMapper();

	  public GlobalAuthenticationFailureHandler(ObjectMapper mapper) {
	   // this.mapper = mapper;
	  }

	  @Override
	  public void onAuthenticationFailure(HttpServletRequest request,
	                                      HttpServletResponse response,
	                                      AuthenticationException e) throws IOException, ServletException {
		  SecurityContextHolder.clearContext();
	    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	    response.setStatus(HttpStatus.UNAUTHORIZED.value());
	    Map<String, Object> data = new HashMap<>();
	    data.put("id","UNKNOWN");
	    data.put("code","401");
	    data.put("detail","Authorization Failed");
	    data.put("source"," ");
	    response.getOutputStream()
	      .println(objectMapper.writeValueAsString(data));
	    
	  }
	}

