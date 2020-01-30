package com.jwt.authentication.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;

/**
 * 
 * @author 1590737
 *
 */

@Configuration
public class PropertyConfiguration {
	
	 @Bean
	    public PermissionEvaluator permissionEvaluator() {
	    	CustomPermissionEvaluator bean = new CustomPermissionEvaluator();
	    	return bean;
	    }
	 
   @Bean
   public MethodSecurityExpressionHandler expressionHandler() {
       DefaultMethodSecurityExpressionHandler bean = new DefaultMethodSecurityExpressionHandler();
       bean.setPermissionEvaluator(permissionEvaluator());
       return bean;
   }

}
