package sciwhiz12.aoc.y2020;

import sciwhiz12.aoc.common.IO;
import sciwhiz12.aoc.common.Timings;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static sciwhiz12.aoc.common.Printer.Builder.outputs;

public class Day2 {
    static final List<String> INPUTS = IO.input("2020/day2.txt");
    static final Pattern DB_LINE = Pattern.compile("^(\\d+)-(\\d+) ([a-z]): ([a-z]+)$");

    public static void main(String... args) {

        partOne();

        partTwo();

    }

    public static void partOne() {
        final long count;

        Timings.start();

        final List<Entry> entries = parseEntries(INPUTS);
        count = entries.parallelStream()
                .filter(entry -> {
                    final long chars = entry.password.chars()
                            .filter(ch -> ch == entry.requirement)
                            .count();
                    return entry.firstNum <= chars && chars <= entry.secondNum;
                })
                .count();

        Timings.stop();

        outputs()
                .add("Valid Passwords", count)
                .print();
    }

    public static void partTwo() {
        final long count;

        Timings.start();

        final List<Entry> entries = parseEntries(INPUTS);
        count = entries.parallelStream()
                .filter(entry -> {
                    boolean firstChar = entry.password.charAt(entry.firstNum - 1) == entry.requirement;
                    boolean secondChar = entry.password.charAt(entry.secondNum - 1) == entry.requirement;
                    return firstChar != secondChar;
                })
                .count();

        Timings.stop();

        outputs()
                .add("Valid Passwords", count)
                .print();
    }

    static List<Entry> parseEntries(List<String> lines) {
        return lines.parallelStream()
                .map(DB_LINE::matcher)
                .filter(Matcher::matches)
                .map(matcher -> {
                    int min = Integer.parseInt(matcher.group(1));
                    int max = Integer.parseInt(matcher.group(2));
                    char requirement = matcher.group(3).charAt(0);
                    String password = matcher.group(4);
                    return new Entry(min, max, requirement, password);
                })
                .collect(Collectors.toList());
    }

    static class Entry {
        public final int firstNum;
        public final int secondNum;
        public final char requirement;
        public final String password;

        Entry(int firstNum, int secondNum, char requirement, String password) {
            this.firstNum = firstNum;
            this.secondNum = secondNum;
            this.requirement = requirement;
            this.password = password;
        }
    }
}
