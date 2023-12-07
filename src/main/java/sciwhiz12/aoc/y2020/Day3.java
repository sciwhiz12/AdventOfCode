package sciwhiz12.aoc.y2020;

import sciwhiz12.aoc.common.IO;
import sciwhiz12.aoc.common.Printer;
import sciwhiz12.aoc.common.Timings;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import static sciwhiz12.aoc.common.Printer.Builder.outputs;

public class Day3 {
    static final List<String> INPUTS = IO.input("2020/day3.txt");
    static final int[][] SLOPES = {
            {1, 1},
            {3, 1},
            {5, 1},
            {7, 1},
            {1, 2}
    };
    static final char TREE = '#';

    public static void main(String... args) {

        partOne();

        partTwo();

    }

    public static void partOne() {
        int amountOfTrees;
        final int slopeX = 3;
        final int slopeY = 1;

        Timings.start();

        char[][] map = toArray(INPUTS);

        amountOfTrees = calculateWithSlope(map, 3, 1);

        if (amountOfTrees < 0)
            throw new IllegalStateException("Did not calculate correctly, value is " + amountOfTrees);

        Timings.stop();

        outputs()
                .add("Slope, X axis", slopeX)
                .add("Slope, Y axis", slopeY)
                .add("Amount of Trees", amountOfTrees)
                .print();
    }

    public static void partTwo() {
        int[] totalAmounts = new int[SLOPES.length];
        long output;

        Timings.start();

        char[][] map = toArray(INPUTS);

        for (int i = 0; i < SLOPES.length; i++) {
            int amountOfTrees;
            amountOfTrees = calculateWithSlope(map, SLOPES[i][0], SLOPES[i][1]);

            if (amountOfTrees < 0)
                throw new IllegalStateException("Did not calculate correctly for slope " + (i + 1) + ", value is " + amountOfTrees);
            totalAmounts[i] = amountOfTrees;
        }

        output = Arrays.stream(totalAmounts).asLongStream().reduce((a, b) -> a * b).orElseThrow(() -> new IllegalStateException("Multiplication failed"));

        if (output <= 0)
            throw new IllegalStateException("Did not calculate correctly, value is " + output);

        Timings.stop();

        Printer.Builder builder = outputs();
        for (int i = 0; i < SLOPES.length; i++) {
            builder.add("Slope #" + i, "%s, %s".formatted(SLOPES[i][0], SLOPES[i][1]))
                    .add("Slope #" + 1 + " result", totalAmounts[i]);
        }
        builder.add("Total Output", output)
                .print();
    }

    private static int calculateWithSlope(char[][] map, final int slopeX, final int slopeY) {
        final int width = map[0].length;
        int currentX = slopeX;
        int currentY = slopeY;
        int amountOfTrees = 0;
        do {
            char ch = map[currentY][currentX];

            char[] chars = map[currentY];
            if (ch == TREE) {
                amountOfTrees++;
            }

            currentX = (currentX + slopeX) % width;
            currentY += slopeY;
        } while (currentY < map.length);
        return amountOfTrees;
    }

    private static char[][] toArray(@SuppressWarnings("SameParameterValue") List<String> input) {
        int width = input.get(0).length();
        int height = input.size();
        char[][] array = new char[height][width];

        for (int y = 0; y < height; y++) {
            final String str = input.get(y);
            array[y] = str.toCharArray();
        }

        return array;
    }

    private static void printMap(char[][] map, PrintStream stream) {
        for (char[] row : map) {
            for (char c : row) {
                stream.print(c);
            }
            stream.println();
        }
    }
}
