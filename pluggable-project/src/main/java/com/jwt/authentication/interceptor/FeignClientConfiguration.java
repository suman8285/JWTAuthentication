package com.jwt.authentication.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.Logger;
import feign.Request;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.auth.BasicAuthRequestInterceptor;

// This class is not used if not added as configuration in all feign client interface
@Configuration
public class FeignClientConfiguration {
	
	/**
	 * Logging levels are BASIC, FULL, HEADERS, NONE
	 * 
	 * @return
	 */
	@Bean
	public Logger.Level configureLogLevel(){
		return  Logger.Level.FULL;
	}
	
	/**
	 * A custom RequestInterceptor. In this instance we 
	 * add a custom header. This is a common enough use case for a 
	 * request header 
	 * 
	 * @return RequestInterceptor
	 */

	@Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("user", "suman");
            requestTemplate.header("password", "suman");
           // requestTemplate.header("token", "suman");
            //requestTemplate.header("Accept", ContentType.APPLICATION_JSON.getMimeType());
        };
    }
	
	/**
	 * Request.Options allows you to configure the connection and 
	 * read timeout values that will be used by the client for each 
	 * request
	 * 
	 * @return Request.Options
	 */
	@Bean 
	public Request.Options timeoutConfiguration(){
		
		return new Request.Options(5000, 30000);
	}
	
	/**
	 * Request interceptor adds HTTP header for basic auth
	 * using the values supplied
	 * 
	 * @return
	 */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("kieserver", "kieserver1!");
    }
    
    /** 
	 * Default Retryer will retry 5 times, backing off (exponentially) between retries.
	 * You can provide your own retry logic by implementing the Retry interface if you need 
	 * some specific behaviour. 
	 * 
	 * @return Retryer
	 */
	@Bean
	public Retryer retryer() {
		
		return new Retryer.Default(1000, 8000, 2);		
	}
}
