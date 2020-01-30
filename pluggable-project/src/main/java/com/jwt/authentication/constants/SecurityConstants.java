package com.jwt.authentication.constants;

public class SecurityConstants {
	
	public static final String SECRET = "suman.jks";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHENTICATION_HEADER_NAME = "Authorization";
    public static final String AUTHENTICATION_URL = "/api/auth/login";
    public static final String REFRESH_TOKEN_URL = "/api/auth/token";
    public static final String RESOURCES = "/resources-info";
    public static final String UTILS = "/utils/**";
    public static final String ACTUATOR = "/actuator/**";
    public static final String ERROR = "/error/**";
    public static final String SWAGGER_RESOURCE = "/swagger-resources/**";
    public static final String SWAGGER_UI = "/swagger-ui.html";
    public static final String SWAGGER_DOCS = "/v2/api-docs";
    public static final String SWAGGER_PUBLIC_API = "/public-api";
    public static final String JARS = "/webjars/**";
   
}
