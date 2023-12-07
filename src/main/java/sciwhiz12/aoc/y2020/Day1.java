package sciwhiz12.aoc.y2020;

import sciwhiz12.aoc.common.IO;
import sciwhiz12.aoc.common.Timings;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static sciwhiz12.aoc.common.Printer.Builder.outputs;

public class Day1 {
    static final List<String> INPUTS = IO.input("2020/day1.txt");
    static final int TARGET_CONSTANT = 2020;

    public static void main(String... args) {

        partOne();

        partTwo();

    }

    public static void partOne() {
        final int THRESHOLD = 1000;
        int entry1 = -1, entry2 = -1, product = -1;

        Timings.start();

        final List<Integer> numbers = INPUTS.parallelStream()
                .map(Integer::parseInt)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        outer:
        for (int first : numbers) {
            for (int second : numbers) {
                if (first + second == TARGET_CONSTANT && !(first == second)) {
                    entry1 = first;
                    entry2 = second;
                    product = first * second;
                    break outer;
                }
            }
        }

        if (product == -1) throw new IllegalStateException("Did not find two numbers with sum of " + TARGET_CONSTANT);

        Timings.stop();

        outputs()
                .add("1st Entry", entry1)
                .add("2nd Entry", entry2)
                .add("Product", product)
                .print();
    }

    public static void partTwo() {
        int entry1 = -1, entry2 = -1, entry3 = -1, product = -1;

        Timings.start();


        final List<Integer> numbers = INPUTS.parallelStream()
                .map(Integer::parseInt)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
        outer:
        for (Integer first : numbers) {
            for (Integer second : numbers) {
                for (Integer third : numbers)
                    if (first + second + third == TARGET_CONSTANT) {
                        entry1 = first;
                        entry2 = second;
                        entry3 = third;
                        product = first * second * third;
                        break outer;
                    }
            }
        }
        if (product == -1) throw new IllegalStateException("Did not find three numbers with sum of " + TARGET_CONSTANT);

        Timings.stop();

        outputs()
                .add("1st Entry", entry1)
                .add("2nd Entry", entry2)
                .add("3rd Entry", entry3)
                .add("Product", product)
                .print();
    }
}
