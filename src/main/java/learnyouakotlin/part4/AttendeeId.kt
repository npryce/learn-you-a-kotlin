package learnyouakotlin.part4;

import java.util.Objects;

public final class AttendeeId {
    private final String value;

    private AttendeeId(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("empty value");
        }

        this.value = value;
    }

    public static AttendeeId of(String value) {
        return new AttendeeId(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AttendeeId that = (AttendeeId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + value;
    }

    public String internalRepresentation() {
        return value;
    }
}
