package com.jwt.authentication.jwt;

import java.security.KeyStore;
import java.security.interfaces.RSAKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTool implements ApplicationListener<ApplicationReadyEvent> {

    private static final String DEFAULT_LOCATION = "jwt/suman.jks";
    private static final String DEFAULT_PASSWORD = "suman123";
    private static final String DEFAULT_KEY_ALIAS = "suman";
    private static final String AUD_1 = "USER_ADMIN_RESOURCE";
    private static final String AUD_2 = "aud2";
    private static final String ISSUER = "issuer";
    private static final String SUBJECT = "IBR";
    private static final String JWT_1 = "JWT1";
//    private static final String AUTH_CONTEXT = "auth_context";
//    private static final String[] AUTH_METHODS = {"credential", "otp"};
    
    private static RSAPublicKey tokenPublicKey;
    private static KeyStore keystore;
    
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        try {
            KeyStore localKeystore = KeyStore.getInstance(KeyStore.getDefaultType());
            localKeystore.load(JwtTool.class.getClassLoader().getResourceAsStream(DEFAULT_LOCATION), DEFAULT_PASSWORD.toCharArray());
            setKeyStore(localKeystore);
            RSAPublicKey rsaPublicKey = (RSAPublicKey) localKeystore.getCertificate(DEFAULT_KEY_ALIAS).getPublicKey();
            setPublicKey(rsaPublicKey);
        } catch (Exception e) {
//            log.error(e.getMessage());
        }
    }

    public String generateJWT() {
        //30 secs
        return generateFor(Instant.now().plusSeconds(30));
    }
    
    public String generateJWT(int minutes) {
        return generateFor(Instant.now().plusSeconds(60 * minutes));
    }

    public String generateExpiredJWT() {
        //30 min
        return generateFor(Instant.now().minusSeconds(1800));
    }

    private String generateFor(Instant instant) {
        RSAKey key = getPrivateKey();
        Date now = Date.from(Instant.now());
        Date expiryTS = Date.from(instant);
          String[] SCOPE = {"ALL"};
          String[] AUTHORITIES = {"owner","admin","underwriter"};
        return JWT.create()
            .withExpiresAt(expiryTS)
            .withAudience(AUD_1, AUD_2)
            .withIssuedAt(now)
            .withIssuer(ISSUER)
            .withSubject(SUBJECT)
            .withJWTId(JWT_1)
            .withNotBefore(now)
            .withClaim("username", "suman")
            .withClaim( "userid", "123456")
            .withClaim("user-open-sec-response-code", "500")
            .withClaim("user-aliase-name", "shanrma")
            .withClaim("client_id", "clientid")
            .withArrayClaim("scope", SCOPE)
            .withArrayClaim("authorities", AUTHORITIES)
            .sign(Algorithm.RSA256(key));
        
    }
    
    public String generateCustomJWT(BearerToken.Payload payload) {
      //  RSAKey key = getDefault().getPrivateKey();
        Date now = Date.from(Instant.now());
        //24 Hours
        Date expiryTS = Date.from(Instant.now().plusSeconds(86400));
        ObjectMapper m = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String,Object> payloadClaims = m.convertValue(payload, Map.class);
        
        return  Jwts.builder()
                .setClaims(payloadClaims)
                .setExpiration(expiryTS)
                .setIssuedAt(now)
                .setNotBefore(now)
                .signWith(SignatureAlgorithm.RS256, getPrivateKey())
              .compact();
        
    }
    
    RSAPrivateKey getPrivateKey() {
        try {
          return (RSAPrivateKey) keystore.getKey(DEFAULT_KEY_ALIAS, DEFAULT_PASSWORD.toCharArray());
        } catch (Exception e) {
          throw new RuntimeException(DEFAULT_KEY_ALIAS + ": Unable to load private key", e);
        }
    }

    DecodedJWT verifyJWT(String token) {
        return JWT.require(Algorithm.RSA256(getPublicKey()))
            .build().verify(token);

    }
    
    public static RSAPublicKey getPublicKey() {
        return tokenPublicKey;
    }

    private static void setPublicKey(RSAPublicKey publicKey) {
        JwtTool.tokenPublicKey = publicKey;
    }
    
    private static void setKeyStore(KeyStore keystore) {
        JwtTool.keystore = keystore;
    }

}
