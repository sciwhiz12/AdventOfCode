package intcode;

import common.NumberUtil;

public enum ParameterMode {
    POSITION, IMMEDIATE, RELATIVE;

    public static ParameterMode of(long opcode, int position) {
        long cutOp = opcode / 100; // remove two last digits, the opcode
        return ParameterMode.values()[NumberUtil.getDigit(cutOp, position)];
    }
}
