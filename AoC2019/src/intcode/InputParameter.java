package intcode;

import common.NumberUtil;

public class InputParameter extends Parameter {
    protected InputParameter(Processor processor, ParameterMode mode, long value) {
        super(processor, mode, value);
    }

    protected InputParameter(ParameterMode mode, long value) {
        this(null, mode, value);
    }

    @Override
    public long value(Processor proc) {
        return switch (mode) {
            case POSITION -> proc.getValueAt(value);
            case IMMEDIATE -> value;
            case RELATIVE -> proc.getValueAt(proc.relativeBase + value);
        };
    }

    @Override
    public String toString() {
        return "I[" + mode + "," + value + "]";
    }

    // Position is 1-based
    public static InputParameter from(Processor processor, int position) {
        processor.log.println("Creating input parameter with position: " + position);
        int counter = processor.getProgramCounter();
        processor.log.println(" > program counter " + counter);
        long opcode = processor.getValueAt(counter);
        processor.log.println(" > opcode " + opcode);
        long cutOp = opcode / 100; // remove two last digits, the opcode
        processor.log.println(" > mode settings " + cutOp);
        processor.log.println(" > mode digit " + NumberUtil.getDigit(cutOp, position - 1));
        ParameterMode mode = ParameterMode.values()[NumberUtil.getDigit(cutOp, position - 1)];
        processor.log.println(" > param mode " + mode);
        long value = processor.getValueAt(counter + position);
        processor.log.println(" > value " + value);
        return new InputParameter(processor, mode, value);
    }
}
