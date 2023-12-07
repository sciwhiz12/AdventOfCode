package intcode;

import common.PrettyString;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Scanner;
import java.util.function.Predicate;

public class Processor implements Runnable {
    private ProcessorState state = ProcessorState.INITIAL;
    private long[] memory;
    private int programCounter;
    private String errorMessage = null;
    private Instant start;
    private Instant end;
    PrintWriter log;
    PrintStream consoleOut;
    Scanner consoleIn;
    int relativeBase = 0;

    public Processor(long[] memory, PrintWriter logger, PrintStream consoleOut, Scanner consoleIn) {
        this.memory = Arrays.copyOf(memory, memory.length);
        this.log = logger;
        this.consoleIn = consoleIn;
        this.consoleOut = consoleOut;
    }

    public Processor(long[] memory, PrintWriter logger) {
        this(memory, logger, System.out, new Scanner(System.in));
    }

    public Processor(long[] memory) {
        this(memory, new PrintWriter(Writer.nullWriter()));
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int newValue) {
        if (!state.isStopped()) {
            programCounter = newValue;
            if (programCounter < 0) {
                onError("Program counter is negative");
            } else if (programCounter > memory.length) {
                onError("Program counter is over memory length");
            }
        }
    }

    public long[] getCurrentMemoryState() {
        return Arrays.copyOf(memory, memory.length);
    }

    public int getMemorySize() {
        return memory.length;
    }

    public long getValueAt(int position) {
        if (position < 0) {
            throw new IndexOutOfBoundsException(position);
        }
        return position > memory.length ? 0 : memory[position];
    }

    public long getValueAt(long position) {
        return getValueAt((int) position);
    }

    public void setValueAt(int position, long newValue) {
        if (state.isStopped()) {
            return;
        }
        if (position < 0) {
            throw new IndexOutOfBoundsException(position);
        } else if (position >= memory.length) {
            log.println("Expanding memory size to " + position);
            memory = Arrays.copyOf(memory, position + 1);
        }
        memory[position] = newValue;
    }

    public void setValueAt(long position, long newValue) {
        setValueAt((int) position, newValue);
    }

    public ProcessorState getState() {
        return state;
    }

    public Instant getStartTime() {
        if (state == ProcessorState.INITIAL) {
            throw new IllegalStateException("Processor have not run yet");
        }
        return start;
    }

    public Instant getEndTime() {
        if (!state.isStopped()) {
            throw new IllegalStateException("Processor is not halted");
        }
        return start;
    }

    public void halt() {
        if (!state.isStopped()) {
            state = ProcessorState.HALTED;
            end = Instant.now();
        }
    }

    void onError(String errorMsg) {
        if (!state.isStopped()) {
            end = Instant.now();
            state = ProcessorState.ERROR;
            errorMessage = errorMsg;
        }
    }

    void runOnce() {
        if (!state.isStopped()) {
            long currentInst = memory[programCounter];
            Instruction instruction = Instruction.forOpcode(currentInst);
            if (instruction == null) {
                onError(String.format("Invalid instruction opcode at [%s] : %s", programCounter, currentInst));
                return;
            }
            int advance = instruction.action.apply(this);
            if (advance > 0) {
                log.println("Advancing program counter by: " + advance);
            }
            programCounter += advance;
        }
    }

    public void runUntil(Predicate<Processor> breakpoint) {
        if (state.isStopped()) {
            throw new IllegalStateException("Cannot restart halted processor!");
        }

        if (state == ProcessorState.INITIAL) {
            log.println('\n');
            log.println(" === === === === === ===");
            log.println("Processor is starting execution.");
            log.println("--- --- --- ");
            log.println("Initial memory state: ");
            log.println(PrettyString.indexedTable(memory, programCounter));
            log.println("--- --- --- ");
            start = Instant.now();
            log.println("Start time is " + start.toString());
            log.println(" === === === === === ===");
        } else if (state == ProcessorState.PAUSED) {
            log.println(" === === === === === ===");
            log.println("Processor is resuming execution.");
            log.println("--- --- --- ");
            log.println("Current memory state: ");
            log.println(PrettyString.indexedTable(memory, programCounter));
            log.println(" === === === === === ===");
        }
        state = ProcessorState.RUNNING;

        while (!state.isStopped()) {
            log.println("Current program counter: " + programCounter);
            log.println("Current relative base: " + relativeBase);
            try {
                runOnce();
            } catch (Exception e) {
                onError("JVM Exception occurred: " + e);
                log.println("JVM Exception occurred!");
                e.printStackTrace(log);
            }
            if (!state.isStopped()) {
                log.println(" --- --- ");
                log.println("Current memory state: ");
                log.println(PrettyString.indexedTable(memory, programCounter));
                if (programCounter > memory.length) {
                    log.println(" -!- --- --- -!-");
                    log.println("Program counter has reached end-of-memory. Halting processor.");
                    halt();
                } else if (breakpoint.test(this)) {
                    state = ProcessorState.PAUSED;
                    log.println(" -!- --- --- -!-");
                    log.println("Processor paused due to breakpoint.");
                    log.println(" === === === === === ===");
                    return;
                }
            }
            log.println(" === === ===");
        }
        if (state == ProcessorState.ERROR) {
            log.println();
            log.println(" == ! == ! == ! == ! == ! ==");
            log.println("ERROR in Processor execution. Halted.");
            if (errorMessage != null) {
                log.println("Error message: " + errorMessage);
            }
            log.println(" == ! == ! == ! == ! == ! ==");
            log.println();
        } else if (state == ProcessorState.HALTED) {
            log.println("Processor halted normally.");
        }
        log.println(" --- --- ");
        log.println("Final memory state:");
        log.println(PrettyString.indexedTable(memory, programCounter));
        log.println(" --- --- ");
        log.println("End time is " + end.toString());
        log.println("Run time is " + Duration.between(start, end).toString());
        log.println(" === === === === === ===");
    }

    public void run() {
        runUntil(y -> false);
    }
}
