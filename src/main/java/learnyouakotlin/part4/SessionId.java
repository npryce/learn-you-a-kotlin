package learnyouakotlin.part4;

import javax.annotation.Nonnull;

public final class SessionId extends Identifier {
    private SessionId(String value) {
        super(value);
    }

    public static SessionId of(@Nonnull String value) {
        return new SessionId(value);
    }
}
