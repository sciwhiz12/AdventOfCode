package intcode;

public class OutputParameter extends Parameter {
    protected OutputParameter(Processor processor, ParameterMode mode, long value) {
        super(processor, mode, value);
        if (mode == ParameterMode.IMMEDIATE) {
            throw new IllegalArgumentException("mode IMMEDIATE is invalid for output parameter");
        }
    }

    protected OutputParameter(ParameterMode mode, long value) {
        this(null, mode, value);
    }

    @Override
    public long value(Processor proc) {
        return switch (mode) {
            case POSITION -> value;
            case RELATIVE -> proc.relativeBase + value;
            case IMMEDIATE -> throw new IllegalStateException("mode is IMMEDIATE?");
        };
    }

    @Override
    public String toString() {
        return "O[" + mode + "," + value + "]";
    }

    public void setMemoryValue(Processor proc, long newValue) {
        proc.setValueAt(value(proc), newValue);
    }

    public void setMemoryValue(long newValue) {
        if (proc == null) {
            throw new IllegalStateException("No attached processor");
        }
        setMemoryValue(proc, newValue);
    }

    public static OutputParameter from(Processor processor, int position) {
        int counter = processor.getProgramCounter();
        long opcode = processor.getValueAt(counter) % 100;
        ParameterMode mode = ParameterMode.of(opcode, position);
        long value = processor.getValueAt(counter + position);
        return new OutputParameter(processor, mode, value);
    }
}
