package seanizer.json.organizer;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {


  @TempDir
  Path tmpDir;

  Path input;
  Path output;
  Path rejected;

  @BeforeEach
  void setUp() {
    input = Paths.get("src/test/resources/leads.json");
    output = tmpDir.resolve("deep/nesting/here/output.json");
    rejected = tmpDir.resolve("re/jected.json");
  }

  @Test
  void endToEndTest() {
    Main.main(
        Stream.of("-s", input, "-t", output, "-r", rejected)
              .map(Objects::toString)
              .toArray(String[]::new)
    );
    assertThat(output).isRegularFile();
    assertThat(rejected).isRegularFile();
  }

}