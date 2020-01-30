package com.jwt.authentication.jwt;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthInitializer {
	private static final Logger log = LoggerFactory.getLogger(AuthInitializer.class);

	private static RSAPublicKey genPublicKey;

	@Value("${auth.genPublicKeyURL}")
	private String publicKeyURL;

	@Value("${auth.requirePublicKeyRefresh:#{null}}")
	private String requirePublicKeyRefresh;

	private final RestTemplate restTemplate;

	@Autowired
	public AuthInitializer(@Qualifier("restTemplate") RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
	
	

	//@Scheduled(initialDelay = 0L, fixedDelayString = "${pns.publicKeyRefreshInterval}")
	public void triggerPublicKeyrefresh() {
		if (null != this.requirePublicKeyRefresh && "true".equals(this.requirePublicKeyRefresh)) {
			genPublicKey = getGenPublicKeyExternal();
			log.info("public key refreshed at {} ", Instant.now());
		}
	}

	private RSAPublicKey getGenPublicKeyExternal() {
		try {
			JSONObject consumerJSON = (JSONObject) restTemplate.getForObject(this.publicKeyURL, JSONObject.class,
					new Object[0]);
			String encodedKey = (String) consumerJSON.get("encoded");
			byte[] byteKey = Base64.getDecoder().decode(encodedKey.getBytes());
			X509EncodedKeySpec x509publicKey = new X509EncodedKeySpec(byteKey);
			KeyFactory kf = KeyFactory.getInstance("RSA");

			return (RSAPublicKey) kf.generatePublic(x509publicKey);
		} catch (Exception ex) {
			log.error("getGenPublicKey error: ", ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	public static RSAPublicKey getGenPublicKeyLocal() {
		if (genPublicKey == null) {
			System.out.println("Public key for gen validation is null.");
			throw new AuthenticationServiceException("Public key for gen token validation is null.");
		}
		return genPublicKey;
	}
}