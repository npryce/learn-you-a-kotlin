package learnyouakotlin.part4;

import java.util.Objects;

public abstract class Identifier {
    private final String value;

    protected Identifier(String value) {
        this.value = value;
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("empty value");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
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
