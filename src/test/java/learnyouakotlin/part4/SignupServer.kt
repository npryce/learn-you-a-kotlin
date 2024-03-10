@file:JvmName("SignupServer")

package learnyouakotlin.part4

import com.sun.net.httpserver.HttpServer
import org.glassfish.jersey.uri.UriTemplate
import java.net.InetSocketAddress
import java.util.concurrent.Executors
import java.util.function.Consumer


/**
 * Run the signup handler with in-memory storage, for manual testing
 */
fun main() {
    val book = InMemorySignupBook()
    
    for (i in 1..10) {
        val session = SignupSheet()
        session.sessionId = SessionId(i.toString())
        session.capacity = 20
        book.save(session)
    }
    
    val port = 9876
    val server = HttpServer.create(InetSocketAddress(port), 0)
    
    // So we don't have to worry that SignupSheet and SignupBook are not thread safe
    server.executor = Executors.newSingleThreadExecutor()
    server.createContext("/", SignupHttpHandler(InMemoryTransactor(book)))
    server.start()
    
    println("Ready:")
    SignupHttpHandler.routes.forEach(Consumer { template: UriTemplate ->
        println("- " + "http://localhost:" + port + template.template)
    })
}
