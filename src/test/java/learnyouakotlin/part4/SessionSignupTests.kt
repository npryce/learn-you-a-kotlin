package learnyouakotlin.part4;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class SessionSignupTests {
    private static final AttendeeId alice = AttendeeId.of("alice");
    private static final AttendeeId bob = AttendeeId.of("bob");
    private static final AttendeeId carol = AttendeeId.of("carol");
    private static final AttendeeId dave = AttendeeId.of("dave");

    private final SessionSignup signup = new SessionSignup();

    @Test
    public void collects_signups() {
        signup.setCapacity(15);

        assertEquals(Set.of(), signup.getSignups());

        signup.signUp(alice);
        assertEquals(Set.of(alice), signup.getSignups());

        signup.signUp(bob);
        assertEquals(Set.of(alice, bob), signup.getSignups());

        signup.signUp(carol);
        assertEquals(Set.of(alice, bob, carol), signup.getSignups());

        signup.signUp(dave);
        assertEquals(Set.of(alice, bob, carol, dave), signup.getSignups());
    }

    @Test
    public void each_attendee_can_only_sign_up_once() {
        signup.setCapacity(3);

        signup.signUp(alice);
        signup.signUp(alice);
        signup.signUp(alice);

        assertTrue(!signup.isFull());
        assertEquals(Set.of(alice), signup.getSignups());
    }

    @Test
    public void can_cancel_signup() {
        signup.setCapacity(15);

        signup.signUp(alice);
        signup.signUp(bob);
        signup.signUp(carol);

        signup.cancelSignUp(bob);

        assertEquals(Set.of(alice, carol), signup.getSignups());
    }

    @Test
    public void can_only_sign_up_to_capacity() {
        signup.setCapacity(3);

        assertTrue(!signup.isFull());
        signup.signUp(alice);

        assertTrue(!signup.isFull());
        signup.signUp(bob);

        assertTrue(!signup.isFull());
        signup.signUp(carol);

        assertTrue(signup.isFull());
        assertThrows(IllegalStateException.class, () ->
            signup.signUp(dave));
    }

    @Test
    public void duplicate_signup_ignored_when_full() {
        signup.setCapacity(3);
        signup.signUp(alice);
        signup.signUp(bob);
        signup.signUp(carol);

        signup.signUp(alice); // does not throw
    }

    @Test
    public void cannot_sign_up_after_session_has_started() {
        signup.setCapacity(3);

        signup.signUp(alice);
        signup.signUp(bob);

        assertTrue(!signup.isSessionStarted());
        signup.start();

        assertTrue(signup.isSessionStarted());
        assertThrows(IllegalStateException.class, () ->
            signup.signUp(carol));
    }

    @Test
    public void ignores_duplicate_signup_after_session_has_started() {
        signup.setCapacity(3);

        signup.signUp(alice);
        signup.start();
        signup.signUp(alice); // Does not throw
    }

    @Test
    public void can_increase_capacity() {
        signup.setCapacity(2);
        signup.signUp(alice);
        signup.signUp(bob);
        assertTrue(signup.isFull());

        signup.setCapacity(4);
        assertTrue(!signup.isFull());
        signup.signUp(carol);
        signup.signUp(dave);
        assertEquals(4, signup.getCapacity());
        assertTrue(signup.isFull());
    }

    @Test
    public void cannot_reduce_capacity_to_fewer_than_number_of_signups() {
        signup.setCapacity(4);
        signup.signUp(alice);
        signup.signUp(bob);
        signup.signUp(carol);
        signup.signUp(dave);

        assertThrows(IllegalStateException.class, () ->
            signup.setCapacity(3)
        );
    }

    @Test
    public void cannot_reduce_capacity_after_session_started() {
        signup.setCapacity(3);

        signup.signUp(alice);
        signup.signUp(bob);
        signup.signUp(carol);
        signup.start();

        assertThrows(IllegalStateException.class, () ->
            signup.setCapacity(6)
        );
    }
}
