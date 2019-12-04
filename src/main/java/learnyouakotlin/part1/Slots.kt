package learnyouakotlin.part1;

import java.util.Objects;

public class Slots {
    public final int start, endInclusive;

    public Slots(int start, int endInclusive) {
        this.start = start;
        this.endInclusive = endInclusive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Slots slots = (Slots) o;
        return start == slots.start &&
                endInclusive == slots.endInclusive;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, endInclusive);
    }

    @Override
    public String toString() {
        return "Slots{" +
                "start=" + start +
                ", endInclusive=" + endInclusive +
                '}';
    }
}
