package tk.sciwhiz12.aoc2021;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Day2 {
    // For Part 1 solution, disable the use of aim
    private static final boolean USE_AIM = true;

    public static void main(String[] args) {
        final List<String> lines = Helpers.readLines("day2.txt");

        class SubmarinePosition {
            int horizontalPosition = 0;
            int depth = 0;
            int aim = 0;
        }


        enum SubmarineInstruction implements BiConsumer<SubmarinePosition, Integer> {
            FORWARD((pos, amount) -> {
                pos.horizontalPosition += amount;
                if (USE_AIM) pos.depth += pos.aim * amount;
            }),
            DOWN((pos, amount) -> {
                if (!USE_AIM) pos.depth += amount;
                else pos.aim += amount;
            }),
            UP((pos, amount) -> {
                if (!USE_AIM) pos.depth -= amount;
                else pos.aim -= amount;
            });

            private final BiConsumer<SubmarinePosition, Integer> action;

            SubmarineInstruction(BiConsumer<SubmarinePosition, Integer> action) {
                this.action = action;
            }

            @Override
            public void accept(SubmarinePosition position, Integer amount) {
                action.accept(position, amount);
            }
        }

        record SubmarineInstructionEntry(SubmarineInstruction instruction,
                                         int amount) implements Consumer<SubmarinePosition> {
            public static SubmarineInstructionEntry parse(String str) {
                final String[] split = str.split(" ");
                return new SubmarineInstructionEntry(
                    SubmarineInstruction.valueOf(split[0].toUpperCase(Locale.ROOT)),
                    Integer.parseInt(split[1])
                );
            }

            @Override
            public void accept(SubmarinePosition position) {
                this.instruction.accept(position, this.amount);
            }
        }

        final SubmarinePosition pos = new SubmarinePosition();

        lines.stream()
            .map(SubmarineInstructionEntry::parse)
            .forEach(entry -> entry.accept(pos));

        System.out.printf("horizontalPosition = %s, depth = %s, aim = %s, horizontalPosition * depth = %s",
            pos.horizontalPosition, pos.depth, pos.aim, pos.horizontalPosition * pos.depth);
    }
}
