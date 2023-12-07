import common.Paths;
import common.Timings;
import intcode.Processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

import static common.Version.AoC2019;

public class Day9 {
    static final Path inputFile = Paths.get(AoC2019, "day9.txt");
    static final Path pt1LogFile = Paths.get(AoC2019, "logs", "day9_part1_syslog.txt");

    public static void main(String[] args) {
        Timings.start();

        try {
            Files.deleteIfExists(pt1LogFile);
        } catch (IOException e) {
        }
        try (BufferedReader input = Files.newBufferedReader(inputFile);
             BufferedWriter logger = Files.newBufferedWriter(pt1LogFile, StandardOpenOption.CREATE_NEW)) {
            String instructionLine = input.readLine();
            final long[] instructions = Arrays.stream(instructionLine.split(",")).mapToLong(Long::parseLong).toArray();

            Processor processor;

            processor = new Processor(instructions, new PrintWriter(logger, true));
            processor.run();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Timings.stop();
    }
}
