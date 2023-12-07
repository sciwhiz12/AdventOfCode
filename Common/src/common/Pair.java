package common;

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

    public int hashCode() {
        return Objects.hash(first, last);
    }

    public boolean equals(Object other) {
        if (other == null) return false;
        if (!(other instanceof Pair)) return false;
        @SuppressWarnings("rawtypes")
        Pair o = (Pair) other;
        if (this.first.equals(o.first) && this.last.equals(o.last)) return true;
        return false;
    }
}
