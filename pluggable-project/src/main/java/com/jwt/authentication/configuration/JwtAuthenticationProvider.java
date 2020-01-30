package com.jwt.authentication.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;



@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    
    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
        JwtAuthenticationToken jwtAuthentication = (JwtAuthenticationToken) authentication; 
        //Optional token = (Optional) authentication.getPrincipal();
        
      //Get the username of the logged in user: getPrincipal()
        System.out.println("auth.getPrincipal()=>"+jwtAuthentication.getName() );
        //Get the password of the authenticated user: getCredentials()
        System.out.println("auth.getCredentials()=>"+jwtAuthentication.getCredentials());
        //Get the assigned roles of the authenticated user: getAuthorities()
        System.out.println("auth.getAuthorities()=>"+jwtAuthentication.getAuthorities());
        //Get further details of the authenticated user: getDetails()
        System.out.println("auth.getDetails()=>"+jwtAuthentication.getDetails());
        System.out.println("--------------------------------------------------------------");
        
//        if (!token.isPresent() ) {
//            throw new BadCredentialsException("Invalid token");
//        }
        return authentication instanceof JwtAuthenticationToken
                ? getJwtAuthentication(jwtAuthentication.getBearerToken(), jwtAuthentication.getToken()) : null;
    }
    
    private Authentication getJwtAuthentication(BearerToken bearerToken, String token) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        List<BearerToken.Authority> authority = bearerToken.getPayload().getAuthority();
      
        authorities.add(new SimpleGrantedAuthority("ADMIN"));// for Testing
        return new JwtAuthenticationToken(authorities, bearerToken, token);
    }

}
