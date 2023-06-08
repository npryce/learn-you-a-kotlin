package example.signup;

public final class AttendeeId extends Identifier {
    private AttendeeId(String value) {
        super(value);
    }

    public static AttendeeId of(String value) {
        return new AttendeeId(value);
    }
}
