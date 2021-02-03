package seanizer.json.organizer;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.converters.PathConverter;
import com.google.common.base.Throwables;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import seanizer.json.organizer.value.LeadsContainer;
import seanizer.json.organizer.value.Output;

public class Main {

  @Parameter(names = {"--source", "-s"}, converter = PathConverter.class,
             description = "Path to JSON input file")
  Path source = Paths.get("src/test/resources/leads.json");

  @Parameter(names = {"--target", "-t"}, converter = PathConverter.class,
             description = "Path to JSON output file where valid (non-duplicate) records will be stored")
  Path target = Paths.get("target/output.json");

  @Parameter(names = {"--rejected", "-r"}, converter = PathConverter.class,
             description = "Path to JSON output file where invalid (duplicate) records will be stored")

  Path rejected = Paths.get("target/rejected.json");

  @Parameter(names = "--help", arity = 0, description = "Display usage and quit")
  boolean showHelp = false;

  @Parameter(names = {"--debug", "-d"}, arity = 0, description = "Display exception stack traces")
  boolean showExceptionStackTraces = false;


  public static void main(String... args) {
    printHeader();
    Main main = new Main();
    JCommander jCommander = JCommander.newBuilder()
                                      .addObject(main)
                                      .build();
    parseCli(main, jCommander, args);
    runApplication(main);
  }

  private static void printHeader() {
    System.out.println("**********************************************************************");
    System.out.println("Running " + Main.class.getName());
    System.out.println("**********************************************************************");
    System.out.println();
  }

  private static void parseCli(Main main, JCommander jCommander, String[] args) {
    try {
      jCommander.parse(args);
    } catch (ParameterException e) {
      jCommander.usage();
      System.exit(1);
    }
    if (main.showHelp) {
      jCommander.usage();
      System.exit(0);
    }
  }

  private static void runApplication(Main main) {
    try {
      main.validate();
      main.run();
    } catch (Exception e) {
      if (main.showExceptionStackTraces) {
        Throwable rootCause = Throwables.getRootCause(e);
        if (rootCause != e) {
          System.out.println(e.getMessage());
        }
        rootCause.printStackTrace();
      } else {
        System.out.println(e.getMessage());
      }
      System.exit(1);
    }
  }

  private void validate() {
    if (!Files.isRegularFile(source)) {
      throw new IllegalStateException(
          "Parameter --source does not point to a valid file: " + source);
    }
    if (Files.isDirectory(target)) {
      throw new IllegalStateException(
          "Parameter --target points to a directory: " + target);
    }
    if (Files.isDirectory(rejected)) {
      throw new IllegalStateException(
          "Parameter --rejected points to a directory: " + rejected);
    }
    System.out.println("Application parameters:");
    System.out.println(" - source file: " + source);
    System.out.println(" - output file: " + target);
    System.out.println(" - duplicates file: " + rejected);
    System.out.println();

  }

  private void run() throws IOException {
    Json json = Json.instance();
    InputStream inputStream = Files.newInputStream(source);
    LeadsContainer input = json.deserialize(inputStream, LeadsContainer.class);

    System.out.printf("Processing JSON payload (%d records)%n", input.leads().size());
    Output output = Application.instance().process(input);

    System.out.printf("Writing accepted records (%d) to file %s%n",
                      output.acceptedCount(), target);
    writeJson(json, target, output.accepted());
    System.out.printf("Writing rejected records (%d) to file %s%n",
                      output.rejectedCount(), rejected);
    writeJson(json, rejected, output.rejected());
    System.out.println("Done");
  }

  private void writeJson(Json json, Path targetPath, Object payLoad) throws IOException {
    Path parent = targetPath.getParent();
    if (parent != null) {
      Files.createDirectories(parent);
    }
    json.serialize(payLoad, Files.newOutputStream(targetPath));
  }

}
