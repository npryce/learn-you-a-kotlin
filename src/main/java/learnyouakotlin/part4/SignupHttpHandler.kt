package learnyouakotlin.part4

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import jakarta.ws.rs.HttpMethod
import jakarta.ws.rs.core.Response.Status
import jakarta.ws.rs.core.Response.Status.CONFLICT
import jakarta.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED
import jakarta.ws.rs.core.Response.Status.NOT_FOUND
import jakarta.ws.rs.core.Response.Status.OK
import org.glassfish.jersey.uri.UriTemplate
import java.io.OutputStreamWriter

class SignupHttpHandler(private val transactor: Transactor<SignupBook>) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        exchange.use {
            val params = HashMap<String, String>()
            val matchedRoute = routes.firstOrNull { it.match(exchange.requestURI.path, params) }
            if (matchedRoute == null) {
                sendResponse(exchange, NOT_FOUND, "resource not found")
                return
            }
            
            transactor.perform { book: SignupBook ->
                val sheet = book.sheetFor(SessionId.of(params["sessionId"]))
                if (sheet == null) {
                    sendResponse(exchange, NOT_FOUND, "session not found")
                    return@perform
                }
                
                when (matchedRoute) {
                    signupsRoute -> handleSignups(exchange, sheet)
                    signupRoute -> handleSignup(exchange, book, sheet, AttendeeId.of(params["attendeeId"]))
                    startedRoute -> handleStarted(exchange, book, sheet)
                }
            }
        }
    }
    
    private fun handleSignups(exchange: HttpExchange, sheet: SignupSheet) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> {
                sendResponse(exchange, OK,
                    sheet.signups.joinToString("\n") { it.value })
            }
            
            else -> {
                sendMethodNotAllowed(exchange)
            }
        }
    }
    
    private fun handleSignup(
        exchange: HttpExchange,
        book: SignupBook,
        sheet: SignupSheet,
        attendeeId: AttendeeId
    ) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> {
                sendResponse(exchange, OK, sheet.isSignedUp(attendeeId))
            }
            
            HttpMethod.POST -> {
                try {
                    sheet.signUp(attendeeId)
                    book.save(sheet)
                    sendResponse(exchange, OK, "subscribed")
                } catch (e: IllegalStateException) {
                    sendResponse(exchange, CONFLICT, e.message)
                }
            }
            
            HttpMethod.DELETE -> {
                sheet.cancelSignUp(attendeeId)
                book.save(sheet)
                sendResponse(exchange, OK, "unsubscribed")
            }
            
            else -> {
                sendMethodNotAllowed(exchange)
            }
        }
    }
    
    private fun handleStarted(exchange: HttpExchange, book: SignupBook, sheet: SignupSheet) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> {
                sendResponse(exchange, OK, sheet.isSessionStarted)
            }
            
            HttpMethod.POST -> {
                sheet.sessionStarted()
                book.save(sheet)
                sendResponse(exchange, OK, "started")
            }
            
            else -> {
                sendMethodNotAllowed(exchange)
            }
        }
    }
    
    companion object {
        @JvmField
        val signupsRoute = UriTemplate("/sessions/{sessionId}/signups")
        
        @JvmField
        val signupRoute = UriTemplate("/sessions/{sessionId}/signups/{attendeeId}")
        
        @JvmField
        val startedRoute = UriTemplate("/sessions/{sessionId}/started")
        
        private val routes = listOf(signupsRoute, signupRoute, startedRoute)
        
        private fun sendResponse(exchange: HttpExchange, status: Status, bodyValue: Any?) {
            exchange.responseHeaders.add("Content-Type", "text/plain")
            exchange.sendResponseHeaders(status.statusCode, 0)
            val body = OutputStreamWriter(exchange.responseBody)
            body.write(bodyValue.toString())
            body.flush()
        }
        
        private fun sendMethodNotAllowed(exchange: HttpExchange) {
            sendResponse(
                exchange, METHOD_NOT_ALLOWED,
                exchange.requestMethod + " method not allowed"
            )
        }
    }
}
