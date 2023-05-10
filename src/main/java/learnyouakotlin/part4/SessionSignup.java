package learnyouakotlin.part4;

import java.util.LinkedHashSet;
import java.util.Set;

public class SessionSignup {
    private int capacity;
    private final LinkedHashSet<AttendeeId> signups = new LinkedHashSet<>();
    private boolean isSessionStarted = false;

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int newCapacity) {
        if (isSessionStarted) {
            throw new IllegalStateException("you cannot change the capacity after the session as started");
        }

        if (signups.size() > newCapacity) {
            throw new IllegalStateException("you cannot change the capacity to fewer than the number of signups");
        }

        this.capacity = newCapacity;
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
