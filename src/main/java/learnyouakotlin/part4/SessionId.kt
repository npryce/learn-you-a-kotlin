package learnyouakotlin.part4;

public final class SessionId extends Identifier {
    private SessionId(String value) {
        super(value);
    }

    public static SessionId of(String value) {
        return new SessionId(value);
    }
}
