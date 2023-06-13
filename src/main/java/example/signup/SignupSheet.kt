package example.signup;

import java.util.LinkedHashSet;
import java.util.Set;

public class SignupSheet {
    private final SessionId sessionId;
    private final int capacity;
    private final LinkedHashSet<AttendeeId> signups = new LinkedHashSet<>();
    private boolean isClosed = false;

    public SignupSheet(SessionId sessionId, int capacity) {
        this.sessionId = sessionId;
        this.capacity = capacity;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isFull() {
        return signups.size() == capacity;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public Set<AttendeeId> getSignups() {
        return Set.copyOf(signups);
    }

    public boolean isSignedUp(AttendeeId attendeeId) {
        return signups.contains(attendeeId);
    }

    public void signUp(AttendeeId attendeeId) {
        if (isClosed()) throw new IllegalStateException("sign-up has closed");
        if (isFull()) throw new IllegalStateException("session is full");

        signups.add(attendeeId);
    }

    public void cancelSignUp(AttendeeId attendeeId) {
        if (isClosed()) throw new IllegalStateException("sign-up has closed");

        signups.remove(attendeeId);
    }

    public void close() {
        isClosed = true;
    }
}
