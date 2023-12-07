package sciwhiz12.aoc.common;

import java.util.Objects;

public class Pair<F, L> {
    public F first;
    public L last;

    public static <F, L> Pair<F, L> of(F first, L last) {
        return new Pair<>(first, last);
    }

    public Pair(F first, L last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return first.equals(pair.first) &&
                last.equals(pair.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last);
    }
}
