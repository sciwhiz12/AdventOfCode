package intcode;

public abstract class Parameter {
    protected final ParameterMode mode;
    protected final long value;
    protected Processor proc;

    protected Parameter(Processor processor, ParameterMode modeIn, long valueIn) {
        this.proc = processor;
        this.mode = modeIn;
        this.value = valueIn;
    }

    public abstract long value(Processor proc);

    public abstract String toString();

    public long value() {
        if (proc == null) {
            throw new IllegalStateException("No attached processor");
        }
        return value(proc);
    }

    public ParameterMode getMode() {
        return mode;
    }

    public long rawValue() {
        return value;
    }

    public void setAttachedProcessor(Processor proc) {
        this.proc = proc;
    }

    public Processor getAttachedProcessor() {
        return proc;
    }

    ;

//    // Position is 0-based
//    public static Parameter from(String opcode, int opcodeLen, int position, long value) {
//        StringBuilder stringOp = new StringBuilder(opcode).reverse();
//        String modeStr = stringOp.substring(opcodeLen - 1);
//        ParameterMode mode = ParameterMode.POSITION;
//        if (modeStr.length() > position) {
//            String modeNum = modeStr.substring(position, position + 1);
//            mode = ParameterMode.values()[Integer.parseInt(modeNum)];
//        }
//        return new Parameter(mode, value);
//    }
//
//    // Position is 1-based
//    public static ResolvedParameter from(Processor processor, int opcodeLen, int position) {
//        int counter = processor.getProgramCounter();
//        String opcode = String.format("%02d", processor.getValueAt(counter));
//        long value = processor.getValueAt(counter + position);
//
//        Parameter param = from(opcode, opcodeLen, position, value);
//        return new ResolvedParameter(processor, param.mode, param.value);
//    }
//
//    // Position is 1-based
//    public static ResolvedParameter from(Processor processor, int position) {
//        int counter = processor.getProgramCounter();
//        String opcode = String.format("%02d", processor.getValueAt(counter));
//        long value = processor.getValueAt(counter + position);
//
//        Parameter param = from(opcode, Instruction.OPCODE_LENGTH, position, value);
//        return new ResolvedParameter(processor, param.mode, param.value);
//    }
}
