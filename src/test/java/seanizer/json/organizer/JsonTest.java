package seanizer.json.organizer;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import seanizer.json.organizer.value.LeadsContainer;

class JsonTest {

  Json underTest = Json.instance();

  @Test
  void comprehensiveJsonTest() {
    InputStream stream = JsonTest.class.getResourceAsStream("/leads.json");
    LeadsContainer container = underTest.deserialize(stream, LeadsContainer.class);
    assertThat(container).isNotNull();

    // now test round trip of serializing and deserializing
    String json = underTest.serialize(container);
    ByteArrayInputStream bais = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
    LeadsContainer deserialized = underTest.deserialize(bais, LeadsContainer.class);

    assertThat(deserialized).describedAs("JSON serialization round trip should preserve equality")
                            .isEqualTo(container);
  }


}