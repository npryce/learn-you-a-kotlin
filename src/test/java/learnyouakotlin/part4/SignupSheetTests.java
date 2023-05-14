package learnyouakotlin.part4;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class SignupSheetTests {
    private static final AttendeeId alice = AttendeeId.of("alice");
    private static final AttendeeId bob = AttendeeId.of("bob");
    private static final AttendeeId carol = AttendeeId.of("carol");
    private static final AttendeeId dave = AttendeeId.of("dave");
    public static final SessionId exampleSessionId = SessionId.of("example-session");


    @Test
    public void collects_signups() {
        SignupSheet sheet = new SignupSheet();
        sheet.setSessionId(exampleSessionId);
        sheet.setCapacity(15);

        assertEquals(Set.of(), sheet.getSignups());

        sheet.signUp(alice);
        assertEquals(Set.of(alice), sheet.getSignups());

        sheet.signUp(bob);
        assertEquals(Set.of(alice, bob), sheet.getSignups());

        sheet.signUp(carol);
        assertEquals(Set.of(alice, bob, carol), sheet.getSignups());

        sheet.signUp(dave);
        assertEquals(Set.of(alice, bob, carol, dave), sheet.getSignups());
    }

    @Test
    public void each_attendee_can_only_sign_up_once() {
        SignupSheet sheet = new SignupSheet();
        sheet.setSessionId(exampleSessionId);
        sheet.setCapacity(3);

        sheet.signUp(alice);
        sheet.signUp(alice);
        sheet.signUp(alice);

        assertTrue(!sheet.isFull());
        assertEquals(Set.of(alice), sheet.getSignups());
    }

    @Test
    public void can_cancel_signup() {
        SignupSheet sheet = new SignupSheet();
        sheet.setSessionId(exampleSessionId);
        sheet.setCapacity(15);

        sheet.signUp(alice);
        sheet.signUp(bob);
        sheet.signUp(carol);

        sheet.cancelSignUp(bob);

        assertEquals(Set.of(alice, carol), sheet.getSignups());
    }

    @Test
    public void can_only_sign_up_to_capacity() {
        SignupSheet sheet = new SignupSheet();
        sheet.setSessionId(exampleSessionId);
        sheet.setCapacity(3);

        assertTrue(!sheet.isFull());
        sheet.signUp(alice);

        assertTrue(!sheet.isFull());
        sheet.signUp(bob);

        assertTrue(!sheet.isFull());
        sheet.signUp(carol);

        assertTrue(sheet.isFull());
        assertThrows(IllegalStateException.class, () ->
            sheet.signUp(dave));
    }

    @Test
    public void cannot_sign_up_after_session_has_started() {
        SignupSheet sheet = new SignupSheet();
        sheet.setSessionId(exampleSessionId);
        sheet.setCapacity(3);

        sheet.signUp(alice);
        sheet.signUp(bob);

        assertTrue(!sheet.isSessionStarted());
        sheet.sessionStarted();

        assertTrue(sheet.isSessionStarted());
        assertThrows(IllegalStateException.class, () ->
            sheet.signUp(carol));
    }
}
