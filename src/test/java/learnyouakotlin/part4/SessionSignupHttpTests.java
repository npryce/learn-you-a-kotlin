package learnyouakotlin.part4;

import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static jakarta.ws.rs.HttpMethod.*;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static jakarta.ws.rs.core.Response.Status.Family.familyOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static learnyouakotlin.part4.SignupHttpHandler.signupTemplate;
import static learnyouakotlin.part4.SignupHttpHandler.startedTemplate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SessionSignupHttpTests {
    private static final AttendeeId alice = AttendeeId.of("alice");
    private static final AttendeeId bob = AttendeeId.of("bob");
    private static final AttendeeId carol = AttendeeId.of("carol");
    private static final AttendeeId dave = AttendeeId.of("dave");

    public static final SessionId exampleSessionId = SessionId.of("example-session");

    private final InMemorySignupBook book = new InMemorySignupBook();
    private final SignupHttpHandler api = new SignupHttpHandler(book);

    SignupSheet sheet = new SignupSheet();

    {
        sheet.setSessionId(exampleSessionId);
        book.add(sheet);
    }

    @Test
    public void collects_signups() {
        sheet.setCapacity(15);

        assertEquals(Set.of(), sheet.getSignups());

        signUp(exampleSessionId, alice);
        assertEquals(Set.of(alice), sheet.getSignups());

        signUp(exampleSessionId, bob);
        assertEquals(Set.of(alice, bob), sheet.getSignups());

        signUp(exampleSessionId, carol);
        assertEquals(Set.of(alice, bob, carol), sheet.getSignups());

        signUp(exampleSessionId, dave);
        assertEquals(Set.of(alice, bob, carol, dave), sheet.getSignups());
    }

    @Test
    public void each_attendee_can_only_sign_up_once() {
        sheet.setCapacity(3);

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, alice);

        assertTrue(!sheet.isFull());
        assertEquals(Set.of(alice), sheet.getSignups());
    }

    @Test
    public void can_cancel_signup() {
        sheet.setCapacity(15);

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);
        signUp(exampleSessionId, carol);

        cancelSignUp(exampleSessionId, bob);

        assertEquals(Set.of(alice, carol), sheet.getSignups());
    }

    @Test
    public void can_only_sign_up_to_capacity() {
        sheet.setCapacity(3);

        assertTrue(!sheet.isFull());
        signUp(exampleSessionId, alice);

        assertTrue(!sheet.isFull());
        signUp(exampleSessionId, bob);

        assertTrue(!sheet.isFull());
        signUp(exampleSessionId, carol);

        assertTrue(sheet.isFull());

        signUp(failsWithConflict, exampleSessionId, dave);
    }

    @Test
    public void cannot_sign_up_after_session_has_started() {
        sheet.setCapacity(3);

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);

        assertTrue(!isSessionStarted(exampleSessionId));
        startSession(exampleSessionId);

        assertTrue(isSessionStarted(exampleSessionId));
        signUp(failsWithConflict, exampleSessionId, carol);
    }

    private void signUp(SessionId sessionId, AttendeeId attendeeId) {
        signUp(isSuccessful, sessionId, attendeeId);
    }

    private void signUp(Predicate<HttpExchange> expectedOutcome, SessionId sessionId, AttendeeId attendeeId) {
        apiCall(expectedOutcome, POST, signupTemplate.createURI(Map.of(
            "sessionId", sessionId.getValue(),
            "attendeeId", attendeeId.getValue())));
    }

    private void cancelSignUp(SessionId sessionId, AttendeeId attendeeId) {
        apiCall(isSuccessful, DELETE, signupTemplate.createURI(Map.of(
            "sessionId", sessionId.getValue(),
            "attendeeId", attendeeId.getValue())));
    }

    private boolean isSessionStarted(SessionId sessionId) {
        return Boolean.parseBoolean(
            apiCall(isSuccessful, GET, startedTemplate.createURI(Map.of(
                "sessionId", sessionId.getValue()))));
    }

    private void startSession(SessionId sessionId) {
        apiCall(isSuccessful, POST, startedTemplate.createURI(Map.of(
            "sessionId", sessionId.getValue())));
    }

    private String apiCall(Predicate<HttpExchange> expectedResult, String method, String uri) {
        final var exchange = new InMemoryHttpExchange(method, uri);

        try {
            api.handle(exchange);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        assertTrue(expectedResult.test(exchange), () -> "expected " + expectedResult);

        return exchange.getResponseBody().toString(UTF_8);
    }

    private static final Predicate<HttpExchange> failsWithConflict = new Predicate<HttpExchange>() {
        @Override
        public String toString() {
            return "fails with conflict";
        }

        @Override
        public boolean test(HttpExchange exchange) {
            return exchange.getResponseCode() == CONFLICT.getStatusCode();
        }
    };

    private static final Predicate<HttpExchange> isSuccessful = new Predicate<HttpExchange>() {
        @Override
        public String toString() {
            return "is successful";
        }

        @Override
        public boolean test(HttpExchange exchange) {
            return familyOf(exchange.getResponseCode()) == SUCCESSFUL;
        }
    };
}
