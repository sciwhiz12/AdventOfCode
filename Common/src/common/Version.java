package common;

import java.nio.file.Path;

public enum Version {
    AoC2019;

    public Path getPath() {
        return Path.of(this.name());
    }
}
