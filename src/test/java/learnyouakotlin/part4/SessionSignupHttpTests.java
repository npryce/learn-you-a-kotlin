package learnyouakotlin.part4;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

import static jakarta.ws.rs.HttpMethod.*;
import static jakarta.ws.rs.core.Response.Status.CONFLICT;
import static jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL;
import static jakarta.ws.rs.core.Response.Status.Family.familyOf;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toCollection;
import static learnyouakotlin.part4.SignupHttpHandler.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class SessionSignupHttpTests {
    private static final AttendeeId alice = AttendeeId.of("alice");
    private static final AttendeeId bob = AttendeeId.of("bob");
    private static final AttendeeId carol = AttendeeId.of("carol");
    private static final AttendeeId dave = AttendeeId.of("dave");

    private final SessionId exampleSessionId = SessionId.of(UUID.randomUUID().toString());

    private final InMemorySignupBook book = new InMemorySignupBook();

    private final HttpHandler api = new SignupHttpHandler(new InMemoryTransactor<>(book));


    @Test
    public void collects_signups() {
        book.save(new SignupSheet(exampleSessionId, 15));

        assertEquals(Set.of(), getSignups(exampleSessionId));

        signUp(exampleSessionId, alice);
        assertEquals(Set.of(alice), getSignups(exampleSessionId));

        signUp(exampleSessionId, bob);
        assertEquals(Set.of(alice, bob), getSignups(exampleSessionId));

        signUp(exampleSessionId, carol);
        assertEquals(Set.of(alice, bob, carol), getSignups(exampleSessionId));

        signUp(exampleSessionId, dave);
        assertEquals(Set.of(alice, bob, carol, dave), getSignups(exampleSessionId));
    }

    @Test
    public void each_attendee_can_only_sign_up_once() {
        book.save(new SignupSheet(exampleSessionId, 3));

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, alice);

        assertEquals(Set.of(alice), getSignups(exampleSessionId));
    }

    @Test
    public void can_only_sign_up_to_capacity() {
        book.save(new SignupSheet(exampleSessionId, 3));

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);
        signUp(exampleSessionId, carol);

        signUp(failsWithConflict, exampleSessionId, dave);
    }

    @Test
    public void cancelling_a_signup_frees_capacity_when_not_full() {
        book.save(new SignupSheet(exampleSessionId, 15));

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);
        signUp(exampleSessionId, carol);

        cancelSignUp(exampleSessionId, carol);
        assertEquals(Set.of(alice, bob), getSignups(exampleSessionId));

        signUp(exampleSessionId, dave);
        assertEquals(Set.of(alice, bob, dave), getSignups(exampleSessionId));
    }

    @Test
    public void cancelling_a_signup_frees_capacity_when_full() {
        book.save(new SignupSheet(exampleSessionId, 3));

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);
        signUp(exampleSessionId, carol);

        cancelSignUp(exampleSessionId, bob);
        assertEquals(Set.of(alice, carol), getSignups(exampleSessionId));

        signUp(exampleSessionId, dave);
        assertEquals(Set.of(alice, carol, dave), getSignups(exampleSessionId));
    }

    @Test
    public void cannot_sign_up_when_sheet_closed() {
        book.save(new SignupSheet(exampleSessionId, 3));

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);

        assertTrue(!isSessionClosed(exampleSessionId));
        closeSession(exampleSessionId);

        assertTrue(isSessionClosed(exampleSessionId));
        signUp(failsWithConflict, exampleSessionId, carol);
    }

    @Test
    public void cannot_cancel_a_sign_up_after_sheet_closed() {
        book.save(new SignupSheet(exampleSessionId, 3));

        signUp(exampleSessionId, alice);
        signUp(exampleSessionId, bob);
        closeSession(exampleSessionId);

        cancelSignUp(failsWithConflict, exampleSessionId, alice);
    }

    @Test
    public void closing_sheet_is_idempotent() {
        book.save(new SignupSheet(exampleSessionId, 3));

        signUp(exampleSessionId, alice);

        closeSession(exampleSessionId);
        closeSession(exampleSessionId);

        signUp(failsWithConflict, exampleSessionId, carol);
        cancelSignUp(failsWithConflict, exampleSessionId, alice);
    }

    @Test
    public void can_close_an_empty_sheet() {
        book.save(new SignupSheet(exampleSessionId, 3));

        closeSession(exampleSessionId);
        signUp(failsWithConflict, exampleSessionId, carol);
    }


    private void signUp(SessionId sessionId, AttendeeId attendeeId) {
        signUp(isSuccessful, sessionId, attendeeId);
    }

    private void signUp(Predicate<HttpExchange> expectedOutcome, SessionId sessionId, AttendeeId attendeeId) {
        apiCall(expectedOutcome, POST, signupRoute.createURI(Map.of(
            "sessionId", sessionId.getValue(),
            "attendeeId", attendeeId.getValue())));
    }

    private void cancelSignUp(SessionId sessionId, AttendeeId attendeeId) {
        cancelSignUp(isSuccessful, sessionId, attendeeId);
    }

    private void cancelSignUp(Predicate<HttpExchange> expectedResult, SessionId sessionId, AttendeeId attendeeId) {
        apiCall(expectedResult, DELETE, signupRoute.createURI(Map.of(
            "sessionId", sessionId.getValue(),
            "attendeeId", attendeeId.getValue())));
    }

    private Set<AttendeeId> getSignups(SessionId sessionId) {
        return apiCall(isSuccessful, GET, signupsRoute.createURI(Map.of(
            "sessionId", sessionId.getValue()))
        ).lines()
            .map(AttendeeId::of)
            .collect(toCollection(LinkedHashSet::new));
    }

    private boolean isSessionClosed(SessionId sessionId) {
        return Boolean.parseBoolean(apiCall(isSuccessful, GET, closedRoute.createURI(Map.of(
            "sessionId", sessionId.getValue()))));
    }

    private void closeSession(SessionId sessionId) {
        apiCall(isSuccessful, POST, closedRoute.createURI(Map.of(
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

    private static final Predicate<HttpExchange> failsWithConflict = new Predicate<>() {
        @Override
        public String toString() {
            return "fails with conflict";
        }

        @Override
        public boolean test(HttpExchange exchange) {
            return exchange.getResponseCode() == CONFLICT.getStatusCode();
        }
    };

    private static final Predicate<HttpExchange> isSuccessful = new Predicate<>() {
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
