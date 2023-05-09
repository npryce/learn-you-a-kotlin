package learnyouakotlin.part4;

import java.util.HashSet;
import java.util.Set;

public class SessionSignup {
    private int capacity;
    private final Set<AttendeeId> signups = new HashSet<>();
    private boolean isSessionStarted = false;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isFull() {
        return signups.size() == capacity;
    }

    public boolean isSessionStarted() {
        return isSessionStarted;
    }

    public void start() {
        isSessionStarted = true;
    }

    public void signUp(AttendeeId attendeeId) {
        if (signups.contains(attendeeId)) {
            return;
        }

        if (isSessionStarted()) {
            throw new IllegalStateException("cannot sign up for session after it has started");
        }

        if (isFull()) {
            throw new IllegalStateException("session is full");
        }

        signups.add(attendeeId);
    }

    public void cancelSignUp(AttendeeId attendeeId) {
        signups.remove(attendeeId);
    }

    public Set<AttendeeId> getSignups() {
        return Set.copyOf(signups);
    }
}
