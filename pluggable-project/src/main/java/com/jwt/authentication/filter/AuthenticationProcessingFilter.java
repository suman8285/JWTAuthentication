package com.jwt.authentication.filter;

import java.io.IOException;
import java.math.BigInteger;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwt.authentication.configuration.BearerToken;
import com.jwt.authentication.configuration.BearerTokenValidator;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class AuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {
	
	@Autowired
	private GlobalAuthenticationFailureHandler failureHandler;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
    private BearerToken bearerToken;
    private String country;
    private long timeOffset;
    private boolean useLocalJKSForPublicKey;

    protected AuthenticationProcessingFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
        super(requiresAuthenticationRequestMatcher);
    }

    public AuthenticationProcessingFilter(String defaultFilterProcessesUrl, long timeOffset, String country, boolean useLocalJKSForPublicKey,
    		ObjectMapper objectMapperData,GlobalAuthenticationFailureHandler failureHandle) {
        super(defaultFilterProcessesUrl);
        this.timeOffset = timeOffset;
        this.country = country;
        this.useLocalJKSForPublicKey = useLocalJKSForPublicKey;
        this.objectMapper = objectMapperData;
        this.failureHandler = failureHandle;
    }

    public static Date toDate(Instant instant) {
        BigInteger milis = BigInteger.valueOf(instant.getEpochSecond()).multiply(BigInteger.valueOf(1000));
        milis = milis.add(BigInteger.valueOf(instant.getNano()).divide(BigInteger.valueOf(1_000_000)));
        return new Date(milis.longValue());
    }    

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthorizationServiceException, AuthenticationException, IOException, ServletException {
   // bearerToken = null;
        
        String token = request.getHeader("Authorization");
       // System.out.println("incoming token>> "+token);
        if (token == null) {     
            throw new AuthenticationServiceException("Invalid Token ");
        } else {
            
            Supplier<RSAPublicKey> keyProvider;
            if (useLocalJKSForPublicKey) {
                keyProvider = JwtTool::getPublicKey;
            } else {
                keyProvider = AuthInitializer::getgeneralPublicKeyLocal;
            }
            Claims claims = Jwts.parser().setSigningKey(keyProvider.get()).parseClaimsJws(token).getBody();
            Instant expires_in;
            String country="";
            try {
            	   BearerTokenValidator validator = new BearerTokenValidator(keyProvider);
            	   bearerToken= validator.parse(token);
            	   validator.validate(token);
            	if (useLocalJKSForPublicKey) {
                	 Date date= claims.get("exp", Date.class);
                     expires_in = date.toInstant();
                     request.setAttribute("client_id", "local");
                }else {
                	String client_id = claims.get("client_id", String.class);
                    Date date= claims.get("exp", Date.class);
                    expires_in = date.toInstant();
                   request.setAttribute("client_id", client_id);
                }
                if (toDate(expires_in).before(toDate(Instant.now().minusSeconds(timeOffset)))) {
                    throw new AuthenticationServiceException("Invalid Token - Token Expired");
                }
            } catch (Exception e) {
                throw new AuthenticationServiceException("Invalid Token " + e.getMessage());
            }
            return getAuthenticationManager().authenticate(new JwtAuthenticationToken(bearerToken, token));
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
       // RequestWrapper requestWrapper= new RequestWrapper(request);
        chain.doFilter(request, response);
    }
    
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
      SecurityContextHolder.clearContext();
      failureHandler.onAuthenticationFailure(request, response, failed);
    }
    
    private String parseClaim(String token, String key) throws JsonParseException, JsonMappingException, IOException {
        String[] parts = token.split("\\.");
        String json = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, Object> map = mapper.readValue(json, HashMap.class);
        if (key != null) {
            return map.get(key).toString();
        }
        return json;
    }

}

