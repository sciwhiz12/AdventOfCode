package sciwhiz12.aoc.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class IO {
    public static List<String> input(String filename) {
        URL output = Thread.currentThread().getContextClassLoader().resources(filename)
                .findFirst().orElseThrow(() -> new IllegalStateException("No resource found for " + filename));
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(output.openStream()))) {
            return reader.lines().collect(Collectors.toList());
        } catch (IOException e) {
            System.err.printf("Error while opening %s for inputs. %n", filename);
            throw new IllegalStateException("Could not open and read " + filename, e);
        }
    }

    public static void output(List<String> output, String first, String... path) {
        output(output, Path.of(first, path));
    }

    public static void output(List<String> output, Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE_NEW)) {
            for (String str : output) {
                writer.write(str);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.printf("Error while opening %s for output. %n", path);
            throw new IllegalStateException("Could not write to " + path, e);
        }
    }
}
