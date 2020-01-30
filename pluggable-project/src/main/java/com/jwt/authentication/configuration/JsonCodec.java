package com.jwt.authentication.configuration;

import static java.lang.String.format;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class JsonCodec {

  private static final Logger logger = LoggerFactory.getLogger(JsonCodec.class);

  private ObjectMapper objectMapper;

  JsonCodec() {
    initObjectMapper();
  }

  private void initObjectMapper() {
    this.objectMapper = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new InstantJsonModule());
  }


  <T> T read(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (IOException e) {
      logger.warn("Error parsing json [{}] to class [{}]", json, clazz.getName());
      throw JsonCodecProcessingException.errorReading(json, clazz, e);
    }
  }

  <T> String write(T t) {
    try {
      return objectMapper.writeValueAsString(t);
    } catch (JsonProcessingException e) {
      logger.warn("Unable to write Json for an object of type [{}]", t.getClass());
      throw JsonCodecProcessingException.errorWriting(t, e);
    }
  }

  static class JsonCodecProcessingException extends RuntimeException {

    JsonCodecProcessingException(String message, Throwable cause) {
      super(message, cause);
    }

    static <T> JsonCodecProcessingException errorReading(String json, Class<T> clazz, Exception e) {
      String message = format("Unable to read json [%s] to class [%s]", json, clazz.getName());
      return new JsonCodecProcessingException(message, e);
    }

    static <T> JsonCodecProcessingException errorWriting(T t, Exception e) {
      return new JsonCodecProcessingException(
          format("Unable to write JSON for object of type [%s]", t.getClass()), e);
    }
  }
}

