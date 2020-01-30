package com.jwt.authentication.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BearerTokenValidator {

  private static final Logger logger = LoggerFactory.getLogger(BearerTokenValidator.class);

  private static final int CLAIMS_INDEX = 1;
  private static final int HEADER_INDEX = 0;
  private static final int SIGNATURE_INDEX = 2;
  private static final int JWT_PART_COUNT = 3;
  private static final String PARTS_SEPARATOR = "\\.";

  private final Supplier<RSAPublicKey> keyProvider;


  private final JsonCodec jsonCodec = new JsonCodec();

  public BearerTokenValidator(Supplier<RSAPublicKey> keyProvider) {
	this.keyProvider = keyProvider;
}

@SuppressWarnings("SameReturnValue")
  public boolean validate(String token) {
    logger.debug("Validate Token: {}", token);
    try {
//      JWT.require(Algorithm.RSA256(keyProvider.get()))
//          .build()
//          .verify(token);
//      logger.trace("Validated token:{}", token);
      return true;
    } catch (JWTVerificationException e) {
      logger.warn("Verification failed for token: {}", token);
      throw new InvalidBearerTokenException("Verification failed for token", e);
    }
  }

  public BearerToken parse(String token) {
    ensureValidity(token);
    return createBearerToken(token);
  }

  private BearerToken createBearerToken(String token) {
    String[] parts = token.split(PARTS_SEPARATOR);

    BearerToken.Header header = createHeader(parts[HEADER_INDEX]);
    BearerToken.Payload payload = createClaim(parts[CLAIMS_INDEX]);
    String signature = parts[SIGNATURE_INDEX];

    return new BearerToken(header, payload, signature);
  }

  private void ensureValidity(String token) {
    validate(token);
  }

  private BearerToken.Header createHeader(String part) {
    return parsePart(part, BearerToken.Header.class);
  }

  private BearerToken.Payload createClaim(String part) {
    return parsePart(part, BearerToken.Payload.class);
  }

  private <T> T parsePart(String part, Class<T> targetClass) {
    String json = new String(Base64.getUrlDecoder().decode(part));
    return jsonCodec.read(json, targetClass);
  }

  public static class InvalidBearerTokenException extends RuntimeException {

    InvalidBearerTokenException(String message, Exception e) {
      super(message, e);
    }
  }
}
