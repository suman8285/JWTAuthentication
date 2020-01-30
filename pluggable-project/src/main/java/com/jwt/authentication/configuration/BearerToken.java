package com.jwt.authentication.configuration;

import static java.util.Collections.unmodifiableList;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public final class BearerToken {

  public final Header header;
  public final Payload payload;
  public final String signature;

  
  public Header getHeader() {
	return header;
  }

	public Payload getPayload() {
		return payload;
	}
	
	public String getSignature() {
		return signature;
	}

public BearerToken(Header header, Payload payload, String signature) {
    this.header = header;
    this.payload = payload;
    this.signature = signature;
  }


  
  public static final class Header {

    @JsonProperty("alg")
    public final String algorithm;
    @JsonProperty("typ")
    public final String keyId;

    
    public String getAlgorithm() {
		return algorithm;
	}


	public String getKeyId() {
		return keyId;
	}


	@JsonCreator
    public Header(@JsonProperty("alg") String algorithm,
        @JsonProperty("typ") String keyId) {
      this.algorithm = algorithm;
      this.keyId = keyId;
    }
  }


  
  public static final class Payload {

 
   
    @JsonProperty("authoritiesModel")
    public final List<Authority> authority;
    @JsonProperty("username")
    public final String userName;
    @JsonProperty("userid")
    public final String userId;
    @JsonProperty("aliasename")
    public final String userAliaseName;
    @JsonProperty("user-rights")
    public final List<ModuleRights> userModuleRights;
    @JsonProperty("authorities")
    public final List<String> authorities;
    @JsonProperty("client_id")
    public final String clientId;
    @JsonProperty("aud")
    public final List<String> audience;
    @JsonProperty("scope")
    public final List<String> scope;
    @JsonProperty("exp")
    public final Instant expiresAt;
    @JsonProperty("jti")
    public final String jwtTokenId;
    
	

	public List<Authority> getAuthority() {
		return authority;
	}

	public String getUserName() {
		return userName;
	}

	public String getUserId() {
		return userId;
	}

	
	public String getUserAliaseName() {
		return userAliaseName;
	}

	public List<ModuleRights> getUserModuleRights() {
		return userModuleRights;
	}


	public List<String> getAuthorities() {
		return authorities;
	}

	public String getClientId() {
		return clientId;
	}

	public List<String> getAudience() {
		return audience;
	}

	public List<String> getScope() {
		return scope;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public String getJwtTokenId() {
		return jwtTokenId;
	}

	@JsonCreator
    Payload(
        @JsonProperty("authoritiesModel") List<Authority> authority,
        @JsonProperty("username") String userName,
        @JsonProperty("userid") String userId,
        @JsonProperty("aliasename") String userAliaseName,
        @JsonProperty("user-rights") List<ModuleRights> userModuleRights,
        @JsonProperty("authorities") List<String> authorities,
        @JsonProperty("client_id") String clientId,
        @JsonProperty("aud") List<String> audience,
        @JsonProperty("scope") List<String> scope,
        @JsonProperty("exp") Instant expiresAt,
        @JsonProperty("jti") String jwtTokenId) {
		this.authority = immutableClone(authority);
		this.userName = userName;
		this.userId = userId;
		this.userAliaseName = userAliaseName;
		this.userModuleRights = immutableClone(userModuleRights);
		this.authorities = immutableClone(authorities);
		this.clientId = clientId;
		this.audience = immutableClone(audience);
		this.scope = immutableClone(scope);
		 this.expiresAt = expiresAt;
		 this.jwtTokenId = jwtTokenId;
    }
  }
 
  

  public static final class Authority {
	  @JsonProperty("authority")
	  public final String authority;

	public String getAuthority() {
		return authority;
	}
	  
	@JsonCreator
	Authority(@JsonProperty("authority") String authority) {
      this.authority = authority;
    }
	  
  }
  
  public static final class ModuleRights {
	  @JsonProperty("name")
	  public final String name;
	  @JsonProperty("permission")
	  public final String permission;
	  
	public String getName() {
		return name;
	}
	public String getPermission() {
		return permission;
	}
	  
	@JsonCreator
	ModuleRights(@JsonProperty("name") String name,
        @JsonProperty("permission") String content) {
      this.name = name;
      this.permission = permission;
    }
	  
  }

  private static <T> List<T> immutableClone(Collection<T> objects) {
    if (objects == null) {
      return unmodifiableList(new ArrayList<>());
    }
    return unmodifiableList(new ArrayList<>(objects));
  }
}




