package seanizer.json.organizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A Façade around a Jackson ObjectMapper.
 */
public final class Json {

  private static final Json INSTANCE = new Json();
  private final ObjectMapper objectMapper;

  private Json() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
    this.objectMapper.registerModule(new GuavaModule());
  }

  public static Json instance() {
    return INSTANCE;
  }

  public <T> T deserialize(InputStream source, Class<T> type) {
    try (InputStream stream = source) { // auto-close Stream
      return objectMapper.readValue(stream, type);
    } catch (IOException e) {
      throw runtimeException(e, false);
    }
  }

  public String serialize(Object payload) {
    try {
      return objectMapper.writeValueAsString(payload);
    } catch (JsonProcessingException e) {
      throw runtimeException(e, true);
    }
  }

  public void serialize(Object payLoad, OutputStream outputStream) {
    try {
      objectMapper.writeValue(outputStream, payLoad);
    } catch (IOException e) {
      throw runtimeException(e, true);
    }
  }

  private RuntimeException runtimeException(IOException e, boolean write) {
    String message = write ? "JSON serialization error" : "JSON deserialization error";
    return new IllegalStateException(message, e);
  }

  @Override
  public String toString() {
    return "Json.instance() (facade around a Jackson ObjectMapper)";
  }
}
