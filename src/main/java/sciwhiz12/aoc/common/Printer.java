package sciwhiz12.aoc.common;

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Printer {

    public static void print(Map<String, Object> values) {
        print(System.out, values);
    }

    public static void print(PrintStream stream, Map<String, Object> values) {
        stream.printf(" ==========[[ OUTPUTS: %s ]]========== %n", Timings.getCallerID());
        values.forEach((name, value) -> {
            stream.printf("  %s :: %s%n", name, Objects.toString(value));
        });
        stream.printf(" ===================================== %n");
    }

    public static class Builder {
        public static Builder outputs() {
            return new Builder();
        }

        private final Map<String, Object> values = new LinkedHashMap<>();
        private PrintStream stream = System.out;

        private Builder() {
        }

        public Builder add(String name, Object obj) {
            values.put(name, obj);
            return this;
        }

        public Builder stream(PrintStream stream) {
            this.stream = stream;
            return this;
        }

        public void print() {
            Printer.print(stream, values);
        }
    }
}
