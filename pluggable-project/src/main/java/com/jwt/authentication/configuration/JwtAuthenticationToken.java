package com.jwt.authentication.configuration;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;


public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    
    private static final long serialVersionUID = 1L;
    private BearerToken bearerToken;
    private String token;
    private String client_id;

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.eraseCredentials();
        super.setAuthenticated(true);
    }

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, BearerToken bearerToken,
            String token) {
        super(authorities);
        this.eraseCredentials();
        this.bearerToken = bearerToken;
        this.token=token;
        super.setAuthenticated(true);
    }

    public JwtAuthenticationToken(BearerToken bearerToken, String token) {
        super(null);
        this.token=token;
        this.bearerToken=bearerToken;
    }
 
    public void setBearerToken(BearerToken bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public Object getCredentials() {
        return this.bearerToken;
    }


    @Override
    public Object getPrincipal() {
        return this.bearerToken;
    }

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public BearerToken getBearerToken() {
		return bearerToken;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

}

