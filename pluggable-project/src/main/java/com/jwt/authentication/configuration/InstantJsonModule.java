package com.jwt.authentication.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.time.Instant;

class InstantJsonModule extends SimpleModule {

  InstantJsonModule() {
    super("InstantJsonModule");
    addSerializer(Instant.class, new InstantSerializer());
    addDeserializer(Instant.class, new InstantDeserializer());
  }

  static class InstantDeserializer extends StdDeserializer<Instant> {

    InstantDeserializer() {
      super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      long i = p.getLongValue();
      return Instant.ofEpochSecond(i);
    }
  }

  static class InstantSerializer extends StdSerializer<Instant> {

    public InstantSerializer() {
      super(Instant.class);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
        throws IOException {
      gen.writeNumber(value.getEpochSecond());
    }
  }
}

