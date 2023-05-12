package learnyouakotlin.part4;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.HttpURLConnection.*;


public class SignupHandler implements HttpHandler {
    private static final Pattern pathPattern =
        Pattern.compile("^/sessions/(?<sessionId>[^/]+)/signups/(?<attendeeId>[^/]+)$");

    private final SessionSignups signups;

    public SignupHandler(SessionSignups signups) {
        this.signups = signups;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getRequestBody().close();

        try {
            final var match = pathPattern.matcher(exchange.getRequestURI().getPath());
            if (!match.matches()) {
                exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                return;
            }

            final var sessionId = pathParam(match, SessionId::of, "sessionId");
            final var attendeeId = pathParam(match, AttendeeId::of, "attendeeId");

            final var session = signups.load(sessionId);
            if (session == null) {
                exchange.sendResponseHeaders(HTTP_NOT_FOUND, 0);
                return;
            }

            switch (exchange.getRequestMethod()) {
                case "GET" -> {
                    sendResponseBody(exchange, HTTP_OK, Boolean.toString(session.isSignedUp(attendeeId)));
                }
                case "POST" -> {
                    session.signUp(attendeeId);
                    signups.save(session);
                    sendResponseBody(exchange, HTTP_OK, "subscribed");
                }
                case "DELETE" -> {
                    session.cancelSignUp(attendeeId);
                    signups.save(session);
                    sendResponseBody(exchange, HTTP_OK, "unsubscribed");
                }
                default -> {
                    exchange.sendResponseHeaders(HTTP_BAD_METHOD, 0);
                }
            }
        }
        catch (IllegalStateException e) {
            sendResponseBody(exchange, HTTP_CONFLICT, e.getMessage());
        }
        catch (Exception e) {
            exchange.sendResponseHeaders(HTTP_INTERNAL_ERROR, 0);
            PrintWriter writer = new PrintWriter(exchange.getResponseBody());
            e.printStackTrace(writer);
            writer.flush();
        }
        finally {
            exchange.getResponseBody().close();
        }
    }

    private static void sendResponseBody(HttpExchange exchange, int statusCode, String bodyString) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(statusCode, 0);
        final var body = new OutputStreamWriter(exchange.getResponseBody());
        body.write(bodyString);
        body.flush();
    }

    private static <T> T pathParam(Matcher match, Function<String, T> wrapper, String paramName) {
        var strValue = match.group(paramName);
        return strValue != null ? wrapper.apply(strValue) : null;
    }

    public static void main(String[] args) throws IOException {
        final var server = HttpServer.create(new InetSocketAddress(9876), 0);
        final var signups = new InMemorySessionSignups();

        for (int i = 1; i <= 10; i++) {
            SessionSignup session = new SessionSignup();
            session.setId(SessionId.of(Integer.toString(i)));
            session.setCapacity(20);
            signups.save(session);
        }

        server.createContext("/", new SignupHandler(signups));
        server.start();

        System.out.println("http://localhost:9876/{sessionId}/signup/{attendeeId}");
    }
}
