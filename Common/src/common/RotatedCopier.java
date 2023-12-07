package common;

import java.io.IOException;
import java.nio.file.Path;

public class RotatedCopier {
    // test/boom.json (suffix ".bak")-> test/boom.json.bak
    // test/boom.json.bak -> test/boom.json.bak_1
    public static void move(Path input, int maxFiles, String suffix) throws IOException {

    }

    public static String getSuffix(String suffix, int generation) {
        if (generation < 0) {
            throw new IllegalArgumentException("generation is negative");
        }
        if (generation == 1) {
            return suffix;
        } else {
            return suffix + "_" + (generation - 1);
        }
    }
}
