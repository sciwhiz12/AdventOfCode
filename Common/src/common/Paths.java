package common;

import java.nio.file.Path;

public class Paths {
    public static Path get(Version ver, String... path) {
        return ver.getPath().resolve(Path.of("resources", path)).toAbsolutePath();
    }
}
