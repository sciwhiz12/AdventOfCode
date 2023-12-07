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

public class Day2 {
    static final Path inputFile = Paths.get(AoC2019, "day2.txt");
    static final Path pt1LogFile = Paths.get(AoC2019, "logs", "day2_part1_syslog.txt");
    static final Path pt2LogFile = Paths.get(AoC2019, "logs", "day2_part2_syslog.txt");

    public static void main(String[] args) {
        Timings.start();

        long position0 = -1;
        int noun = -1;
        int verb = 0;

        try {
            Files.deleteIfExists(pt1LogFile);
            Files.deleteIfExists(pt2LogFile);
        } catch (IOException e) {
        }
        try (BufferedReader input = Files.newBufferedReader(inputFile);
             BufferedWriter logger = Files.newBufferedWriter(pt1LogFile, StandardOpenOption.CREATE_NEW)) {
            String instructionLine = input.readLine();
            final long[] instructions = Arrays.stream(instructionLine.split(",")).mapToLong(Long::parseLong).toArray();

            Processor processor;

            processor = new Processor(instructions, new PrintWriter(logger, true));
            processor.setValueAt(1, 12);
            processor.setValueAt(2, 2);
            processor.run();
            position0 = processor.getValueAt(0);

            long outputValue = -1;
            try (BufferedWriter log = Files.newBufferedWriter(pt2LogFile, StandardOpenOption.CREATE_NEW)) {
                do {
                    noun++;
                    if (noun > 99) {
                        noun = 0;
                        verb++;
                    }
                    processor = new Processor(instructions, new PrintWriter(log, true));
                    processor.setValueAt(1, noun);
                    processor.setValueAt(2, verb);
                    processor.run();
                    outputValue = processor.getValueAt(0);
                } while (outputValue != 19690720);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Timings.stop();

        System.out.println(position0);

        System.out.println((100 * noun) + verb);
    }
}
