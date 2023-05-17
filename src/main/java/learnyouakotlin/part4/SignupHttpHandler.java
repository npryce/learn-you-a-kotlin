package learnyouakotlin.part4;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.uri.UriTemplate;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static jakarta.ws.rs.HttpMethod.*;
import static jakarta.ws.rs.core.Response.Status.*;


public class SignupHttpHandler implements HttpHandler {
    public static final UriTemplate signupsRoute =
        new UriTemplate("/sessions/{sessionId}/signups");
    public static final UriTemplate signupRoute =
        new UriTemplate("/sessions/{sessionId}/signups/{attendeeId}");
    public static final UriTemplate startedRoute =
        new UriTemplate("/sessions/{sessionId}/started");

    private static final List<UriTemplate> routes =
        List.of(signupsRoute, signupRoute, startedRoute);


    private final Transactor<SignupBook> transactor;

    public SignupHttpHandler(Transactor<SignupBook> transactor) {
        this.transactor = transactor;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final var params = new HashMap<String, String>();

        final var matchedRoute = matchRoute(exchange, params);
        if (matchedRoute == null) {
            sendResponse(exchange, NOT_FOUND, "resource not found");
            return;
        }

        transactor.perform(book -> {
            final var sheet = book.sheetFor(SessionId.of(params.get("sessionId")));
            if (sheet == null) {
                sendResponse(exchange, NOT_FOUND, "session not found");
                return;
            }

            if (matchedRoute == signupsRoute) {
                handleSignups(exchange, sheet);
            } else if (matchedRoute == signupRoute) {
                handleSignup(exchange, book, sheet, AttendeeId.of(params.get("attendeeId")));
            } else if (matchedRoute == startedRoute) {
                handleStarted(exchange, book, sheet);
            }
        });
    }

    private void handleSignups(HttpExchange exchange, SignupSheet sheet) throws IOException {
        switch (exchange.getRequestMethod()) {
            case GET -> {
                sendResponse(exchange, OK,
                    sheet.getSignups().stream()
                        .map(Identifier::getValue)
                        .collect(Collectors.joining("\n")));
            }
            default -> {
                sendMethodNotAllowed(exchange);
            }
        }
    }

    private void handleSignup(HttpExchange exchange, SignupBook book, SignupSheet sheet, AttendeeId attendeeId) throws IOException {
        switch (exchange.getRequestMethod()) {
            case GET -> {
                sendResponse(exchange, OK, sheet.isSignedUp(attendeeId));
            }
            case POST -> {
                try {
                    sheet.signUp(attendeeId);
                    book.save(sheet);
                    sendResponse(exchange, OK, "subscribed");
                } catch (IllegalStateException e) {
                    sendResponse(exchange, CONFLICT, e.getMessage());
                }
            }
            case DELETE -> {
                try {
                    sheet.cancelSignUp(attendeeId);
                    book.save(sheet);
                    sendResponse(exchange, OK, "unsubscribed");
                } catch (IllegalStateException e) {
                    sendResponse(exchange, CONFLICT, e.getMessage());
                }
            }
            default -> {
                sendMethodNotAllowed(exchange);
            }
        }
    }

    private void handleStarted(HttpExchange exchange, SignupBook book, SignupSheet sheet) throws IOException {
        switch (exchange.getRequestMethod()) {
            case GET -> {
                sendResponse(exchange, OK, sheet.isSessionStarted());
            }
            case POST -> {
                sheet.sessionStarted();
                book.save(sheet);
                sendResponse(exchange, OK, "started");
            }
            default -> {
                sendMethodNotAllowed(exchange);
            }
        }
    }

    private static @Nullable UriTemplate matchRoute(HttpExchange exchange, HashMap<String, String> paramsOut) {
        for (final var t : routes) {
            if (t.match(exchange.getRequestURI().getPath(), paramsOut)) {
                return t;
            }
        }
        return null;
    }

    private static void sendResponse(HttpExchange exchange, Response.Status status, Object bodyValue) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(status.getStatusCode(), 0);
        final var body = new OutputStreamWriter(exchange.getResponseBody());
        body.write(bodyValue.toString());
        body.flush();
    }

    private static void sendMethodNotAllowed(HttpExchange exchange) throws IOException {
        sendResponse(exchange, METHOD_NOT_ALLOWED,
            exchange.getRequestMethod() + " method not allowed");
    }
}
