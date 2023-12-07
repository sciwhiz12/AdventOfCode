package common;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Timings {
    private static final Clock timingsClock = Clock.systemUTC();
    private static final Map<String, Instant> startTimes = new HashMap<>();

    public static void start() {
        Instant start = Instant.now(timingsClock);
        String callerID = getCallerID();
        System.out.println("[Timings] Started at " + start.toString() + " for caller ID " + callerID);
        startTimes.put(callerID, start);
    }

    public static void stop() {
        Instant end = Instant.now(timingsClock);
        String callerID = getCallerID();
        Instant start = startTimes.remove(callerID);
        if (start == null) { throw new IllegalStateException("Called stop without having called start: " + callerID); }
        System.out.println("[Timings] Ended at " + end.toString() + " for caller ID " + callerID);
        Duration duration = Duration.between(start, end);
        System.out.println("[Timings] Duration: " + duration.toString());
    }

    private static String getCallerID() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(Timings.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                return ste.getClassName();
            }
        }
        throw new IllegalStateException("Could not find calling class!");
    }
}
