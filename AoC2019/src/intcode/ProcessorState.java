package intcode;

public enum ProcessorState {
    INITIAL, RUNNING, PAUSED, HALTED, ERROR;

    public boolean isStopped() {
        return this == HALTED || this == ERROR;
    }
}
