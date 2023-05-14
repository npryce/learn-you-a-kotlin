package learnyouakotlin.part4;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.uri.UriTemplate;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import static jakarta.ws.rs.HttpMethod.*;
import static jakarta.ws.rs.core.Response.Status.*;


public class SignupHttpHandler implements HttpHandler {
    public static UriTemplate signupTemplate =
        new UriTemplate("/sessions/{sessionId}/signups/{attendeeId}");
    public static UriTemplate startedTemplate =
        new UriTemplate("/sessions/{sessionId}/started");

    private final SignupBook book;

    public SignupHttpHandler(SignupBook book) {
        this.book = book;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try (exchange) {
            String path = exchange.getRequestURI().getPath();
            final var params = new HashMap<String, String>();

            if (signupTemplate.match(path, params)) {
                handleSignup(exchange, params);
            } else if (startedTemplate.match(path, params)) {
                handleStarted(exchange, params);
            } else {
                sendResponse(exchange, NOT_FOUND, "resource not found");
            }
        } catch (Exception e) {
            sendResponse(exchange, INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void handleSignup(HttpExchange exchange, Map<String, String> params) throws IOException {
        final var sheet = book.sheetFor(SessionId.of(params.get("sessionId")));
        if (sheet == null) {
            sendResponse(exchange, NOT_FOUND, "session not found");
            return;
        }

        final var attendeeId = AttendeeId.of(params.get("attendeeId"));

        switch (exchange.getRequestMethod()) {
            case GET -> {
                sendResponse(exchange, OK, sheet.isSignedUp(attendeeId));
            }
            case POST -> {
                try {
                    sheet.signUp(attendeeId);
                    sendResponse(exchange, OK, "subscribed");
                } catch (IllegalStateException e) {
                    sendResponse(exchange, CONFLICT, e.getMessage());
                }
            }
            case DELETE -> {
                sheet.cancelSignUp(attendeeId);
                sendResponse(exchange, OK, "unsubscribed");
            }
            default -> {
                sendResponse(exchange, METHOD_NOT_ALLOWED,
                    exchange.getRequestMethod() + " method not supported");
            }
        }
    }

    private void handleStarted(HttpExchange exchange, HashMap<String, String> params) throws IOException {
        final var sheet = book.sheetFor(SessionId.of(params.get("sessionId")));
        if (sheet == null) {
            sendResponse(exchange, NOT_FOUND, "session not found");
            return;
        }

        switch (exchange.getRequestMethod()) {
            case GET -> {
                sendResponse(exchange, OK, sheet.isSessionStarted());
            }
            case POST -> {
                sheet.sessionStarted();
                sendResponse(exchange, OK, "started");
            }
            default -> {
                sendResponse(exchange, METHOD_NOT_ALLOWED,
                    exchange.getRequestMethod() + " method not supported");
            }
        }
    }

    private static void sendResponse(HttpExchange exchange, Response.Status status, Object bodyValue) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(status.getStatusCode(), 0);
        final var body = new OutputStreamWriter(exchange.getResponseBody());
        body.write(bodyValue.toString());
        body.flush();
    }
}
