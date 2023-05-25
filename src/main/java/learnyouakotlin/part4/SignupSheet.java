package learnyouakotlin.part4;

import java.util.LinkedHashSet;
import java.util.Set;

public class SignupSheet {
    private SessionId sessionId;
    private int capacity;
    private final LinkedHashSet<AttendeeId> signups = new LinkedHashSet<>();
    private boolean isClosed = false;

    public SignupSheet() {
    }

    public SignupSheet(SessionId sessionId, int capacity) {
        this.sessionId = sessionId;
        this.capacity = capacity;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public void setSessionId(SessionId sessionId) {
        this.sessionId = sessionId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int newCapacity) {
        if (capacity != 0) {
            throw new IllegalStateException("you cannot change the capacity after it has been set");
        }

        this.capacity = newCapacity;
    }

    public boolean isFull() {
        return signups.size() == capacity;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void close() {
        isClosed = true;
    }

    public boolean isSignedUp(AttendeeId attendeeId) {
        return signups.contains(attendeeId);
    }

    public void signUp(AttendeeId attendeeId) {
        if (isClosed()) {
            throw new IllegalStateException("sign-up has closed");
        }

        if (isFull()) {
            throw new IllegalStateException("session is full");
        }

        signups.add(attendeeId);
    }

    public void cancelSignUp(AttendeeId attendeeId) {
        if (isClosed()) {
            throw new IllegalStateException("sign-up has closed");
        }

        signups.remove(attendeeId);
    }

    public Set<AttendeeId> getSignups() {
        return Set.copyOf(signups);
    }
}
