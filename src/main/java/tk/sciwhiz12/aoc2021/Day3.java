package tk.sciwhiz12.aoc2021;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Day3 {
    public static void main(String[] args) {
        final List<String> lines = Helpers.readLines("day3.txt");

        final Map<Integer, BitCounter> counters = new HashMap<>();
        count(lines, counters);

        final int gamma = buildFromCounter(counters.values(), false);
        final int epsilon = buildFromCounter(counters.values(), true);

        System.out.printf("%s * %s = %s%n", gamma, epsilon, gamma * epsilon);

        // Part two

        final int oxygen = partTwo(lines, c -> c.ones >= c.zeroes ? '1' : '0');
        final int co2 = partTwo(lines, c -> c.zeroes > c.ones ? '1' : '0');

        System.out.printf("%s * %s = %s%n", oxygen, co2, oxygen * co2);
    }

    static class BitCounter {
        public static final BitCounter EMPTY = new BitCounter();
        int zeroes = 0;
        int ones = 0;

        public void reset() {
            zeroes = 0;
            ones = 0;
        }
    }

    private static void count(List<String> lines, Map<Integer, BitCounter> counters) {
        for (String line : lines) {
            char[] characters = line.toCharArray();
            for (int i = 0; i < characters.length; i++) {
                char c = characters[i];

                final BitCounter counter = counters.computeIfAbsent(i, num -> new BitCounter());

                switch (c) {
                    case '0' -> counter.zeroes++;
                    case '1' -> counter.ones++;
                    default -> throw new IllegalArgumentException("Unknown bit: " + c);
                }
            }
        }
    }

    private static int buildFromCounter(Iterable<BitCounter> counters, boolean reverse) {
        final StringBuilder builder = new StringBuilder();

        for (BitCounter counter : counters) {
            if (counter.ones > counter.zeroes) {
                builder.append(reverse ? '1' : '0');
            } else {
                builder.append(reverse ? '0' : '1');
            }
        }

        return Integer.parseInt(builder.toString(), 2);
    }

    private static int partTwo(List<String> lines, Function<BitCounter, Character> keepCharacter) {
        final ArrayList<String> copy = new ArrayList<>(lines);
        System.out.println(copy);
        Map<Integer, BitCounter> counters = new HashMap<>();

        count(copy, counters);

        for (Map.Entry<Integer, BitCounter> entry : counters.entrySet()) {
            BitCounter counter = entry.getValue();
            char keep = keepCharacter.apply(counter);
            System.out.printf("%s  [0:%s,1:%s]  %s  ", entry.getKey(), counter.zeroes, counter.ones, keep);
            copy.removeIf(str -> str.charAt(entry.getKey()) != keep);
            System.out.println(copy);

            if (copy.size() == 1) break;

            counters.values().forEach(BitCounter::reset);
            count(copy, counters);
        }

        if (copy.size() > 1) {
            throw new IllegalStateException("Found more than one: " + copy);
        }
        return Integer.parseInt(copy.get(0), 2);
    }
}
