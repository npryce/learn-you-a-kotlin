package learnyouakotlin.part4;


import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Run the signup handler with in-memory storage, for manual testing
 */
public class SignupServer {
    public static void main(String[] args) throws IOException {
        final var book = new InMemorySignupBook();
        for (int i = 1; i <= 10; i++) {
            SignupSheet session = new SignupSheet();
            session.setSessionId(SessionId.of(Integer.toString(i)));
            session.setCapacity(20);
            book.save(session);
        }

        int port = 9876;
        final var server = HttpServer.create(new InetSocketAddress(port), 0);
        // So we don't have to worry that SignupSheet and SignupBook are not thread safe
        server.setExecutor(Executors.newSingleThreadExecutor());
        server.createContext("/", new SignupHttpHandler(new InMemoryTransactor<>(book)));
        server.start();

        System.out.println("Ready:");
        SignupHttpHandler.routes.forEach(template -> {
            System.out.println("- " + "http://localhost:" + port + template.getTemplate());
        });
    }
}