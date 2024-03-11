@file:JvmName("SignupServer")

package learnyouakotlin.part4

import org.http4k.server.SunHttp
import org.http4k.server.asServer


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
    
    val transactor = InMemoryTransactor(book)
    val app = SignupApp(transactor)
    val server = app.asServer(SunHttp(9876))
    server.start()
    
    println("Ready:")
    listOf(signupsRoute, signupRoute, closedRoute).forEach { template ->
        println(" http://localhost:${server.port()}/$template")
    }
}
