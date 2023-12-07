package intcode;

import java.util.function.Function;

public enum Instruction {
    ADD(1, proc -> {
        var a = InputParameter.from(proc, 1);
        var b = InputParameter.from(proc, 2);
        var dest = OutputParameter.from(proc, 3);

        long newVal = a.value() + b.value();
        dest.setMemoryValue(newVal);
        proc.log.println(String.format("(ADD): %s + %s = %s -> %s", a, b, newVal, dest));
        return 4;
    }),
    MUL(2, proc -> {
        var a = InputParameter.from(proc, 1);
        var b = InputParameter.from(proc, 2);
        var dest = OutputParameter.from(proc, 3);

        long newVal = a.value() * b.value();
        dest.setMemoryValue(newVal);
        proc.log.println(String.format("(MUL): %s * %s = %s -> %s", a, b, newVal, dest));
        return 4;
    }),
    INPUT(3, proc -> {
        var dest = OutputParameter.from(proc, 1);

        proc.log.println("(INPUT): Awaiting console input...");
        proc.consoleOut.print("Please input a number: ");
        long input = proc.consoleIn.nextLong();

        dest.setMemoryValue(input);
        proc.log.println(String.format("(INPUT): %s -> %s", input, dest));
        return 2;
    }),
    OUTPUT(4, proc -> {
        var in = InputParameter.from(proc, 1);

        proc.consoleOut.println(in.value);
        proc.log.println(String.format("(OUTPUT): Writing %s to console", in));
        return 2;
    }),
    JUMP_IF_TRUE(5, proc -> {
        var test = InputParameter.from(proc, 1);
        var pointer = InputParameter.from(proc, 2);

        proc.log.print(String.format("(JUMP_IF_TRUE): TEST %s, POINTER %s ; test is ", test, pointer));
        boolean success = test.value() != 0;
        if (success) {
            proc.log.println("non-zero, jumping");
            proc.setProgramCounter((int) pointer.value());
        } else {
            proc.log.println("zero");
        }
        return success ? 0 : 3;
    }),
    JUMP_IF_FALSE(6, proc -> {
        var test = InputParameter.from(proc, 1);
        var pointer = InputParameter.from(proc, 2);

        proc.log.print(String.format("(JUMP_IF_FALSE): TEST %s, POINTER %s ; test is ", test, pointer));
        boolean success = test.value() == 0;
        if (success) {
            proc.log.println("zero, jumping");
            proc.setProgramCounter((int) pointer.value());
        } else {
            proc.log.println("non-zero");
        }
        return success ? 0 : 3;
    }),
    LESS_THAN(7, proc -> {
        var a = InputParameter.from(proc, 1);
        var b = InputParameter.from(proc, 2);
        var dest = OutputParameter.from(proc, 3);

        proc.log.print(String.format("(LESS_THAN): %s < %s is ", a, b));
        if (a.value() < b.value()) {
            proc.log.print("TRUE; 1");
            proc.setValueAt(dest.value(), 1);
        } else {
            proc.log.print("FALSE; 0");
            proc.setValueAt(dest.value(), 0);
        }
        proc.log.println(" -> " + dest);
        return 4;
    }),
    EQUAL(8, proc -> {
        var a = InputParameter.from(proc, 1);
        var b = InputParameter.from(proc, 2);
        var dest = OutputParameter.from(proc, 3);

        proc.log.print(String.format("(EQUAL): %s < %s is ", a, b));
        if (a.value() == b.value()) {
            proc.log.print("TRUE; 1");
            proc.setValueAt(dest.value(), 1);
        } else {
            proc.log.print("FALSE; 0");
            proc.setValueAt(dest.value(), 0);
        }
        proc.log.println(" -> " + dest);
        return 4;
    }),
    RELATIVE_BASE_OFFSET(9, proc -> {
        var adj = InputParameter.from(proc, 1);

        int newBase = (int) (proc.relativeBase + adj.value());

        proc.log.println(String.format("(RELATIVE_BASE_OFFSET): %s + %s = new relative base: %s", proc.relativeBase, adj, newBase));
        proc.relativeBase = newBase;
        return 2;
    }),
    HALT(99, proc -> {
        proc.log.println("(HALT): Halting processor execution");
        proc.halt();
        return 0;
    });

    final long opcode;
    final Function<Processor, Integer> action;

    Instruction(long opcode, Function<Processor, Integer> process) {
        this.opcode = opcode;
        this.action = process;
    }

    public long getOpcode() {
        return opcode;
    }

    // TODO: caching
    public static Instruction forOpcode(long opcode) {
        long op = opcode % 100;
        for (Instruction inst : Instruction.values()) {
            if (inst.opcode == op) {
                return inst;
            }
        }
        return null;
    }
}
