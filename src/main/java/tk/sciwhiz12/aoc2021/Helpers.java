package tk.sciwhiz12.aoc2021;

import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public final class Helpers {
    private Helpers() {
    }

    @SuppressWarnings("UnstableApiUsage")
    public static List<String> readLines(String fileName) {
        try {
            return Resources.readLines(Resources.getResource(fileName), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Exception while trying to read resource " + fileName, e);
        }
    }
}
