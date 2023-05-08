package learnyouakotlin.part4;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;


public class SessionSignupTests {
    private static final AttendeeId alice = AttendeeId.of("alice");
    private static final AttendeeId bob = AttendeeId.of("bob");
    private static final AttendeeId carol = AttendeeId.of("carol");
    private static final AttendeeId dave = AttendeeId.of("dave");
    private static final AttendeeId eve = AttendeeId.of("eve");

    SessionSignup signup = new SessionSignup();

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
        assertThrows(IllegalStateException.class, () -> {
            signup.signUp(dave);
        });
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
}
