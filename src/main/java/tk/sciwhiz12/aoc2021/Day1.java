package tk.sciwhiz12.aoc2021;

import com.google.common.collect.EvictingQueue;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class Day1 {
    public static void main(String[] args) {
        final List<String> lines = Helpers.readLines("day1.txt");

        enum Change {
            INCREASE, NEUTRAL, DECREASE;

            public static Change compare(int comparisonOutput) {
                if (comparisonOutput > 0) return DECREASE;
                if (comparisonOutput < 0) return INCREASE;
                return NEUTRAL;
            }
        }

        class Counter implements IntConsumer {
            private boolean start = true;
            private int previous = -1;
            private Map<Change, Integer> changes = new EnumMap<>(Change.class);

            @Override
            public void accept(int value) {
                if (!start) {
                    changes.compute(Change.compare(Integer.compareUnsigned(previous, value)),
                        (k, prev) -> prev == null ? 1 : prev + 1);
                }
                start = false;
                previous = value;
            }
        }

        final Counter counter = new Counter();

        lines.stream()
            .mapToInt(Integer::parseUnsignedInt)
            .forEach(counter);

        System.out.println(counter.changes.getOrDefault(Change.INCREASE, 0));

        // Part two

        @SuppressWarnings("UnstableApiUsage")
        class SlidingCounter implements IntConsumer {
            private boolean start = true;
            private int previousSum = -1;
            private EvictingQueue<Integer> queue = EvictingQueue.create(3);
            private Map<Change, Integer> changes = new EnumMap<>(Change.class);

            @Override
            public void accept(int value) {
                queue.add(value);
                if (queue.remainingCapacity() != 0) return;

                final int sum = queue.stream()
                    .mapToInt(a -> a)
                    .sum();

                if (!start) {
                    changes.compute(Change.compare(Integer.compareUnsigned(previousSum, sum)),
                        (k, prev) -> prev == null ? 1 : prev + 1);
                }
                start = false;

                previousSum = sum;
            }
        }

        final SlidingCounter slidingCounter = new SlidingCounter();

        lines.stream()
            .mapToInt(Integer::parseUnsignedInt)
            .forEach(slidingCounter);

        System.out.println(slidingCounter.changes.getOrDefault(Change.INCREASE, 0));
    }
}
