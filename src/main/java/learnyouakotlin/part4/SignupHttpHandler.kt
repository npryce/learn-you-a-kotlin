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
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.stream.Collectors

class SignupHttpHandler(private val transactor: Transactor<SignupBook>) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val params = HashMap<String, String>()
        val matchedRoute = matchRoute(exchange, params)
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
            if (matchedRoute === signupsRoute) {
                handleSignups(exchange, sheet)
            } else if (matchedRoute === signupRoute) {
                handleSignup(exchange, book, sheet, AttendeeId.of(params["attendeeId"]))
            } else if (matchedRoute === closedRoute) {
                handleClosed(exchange, book, sheet)
            }
        }
    }
    
    private fun handleSignups(exchange: HttpExchange, sheet: SignupSheet) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> {
                sendResponse(exchange, OK,
                    sheet.signups.stream()
                        .map { obj: AttendeeId -> obj.value }
                        .collect(Collectors.joining("\n")))
            }
            
            else -> {
                sendMethodNotAllowed(exchange)
            }
        }
    }
    
    private fun handleSignup(exchange: HttpExchange, book: SignupBook, sheet: SignupSheet, attendeeId: AttendeeId) {
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
                try {
                    sheet.cancelSignUp(attendeeId)
                    book.save(sheet)
                    sendResponse(exchange, OK, "unsubscribed")
                } catch (e: IllegalStateException) {
                    sendResponse(exchange, CONFLICT, e.message)
                }
            }
            
            else -> {
                sendMethodNotAllowed(exchange)
            }
        }
    }
    
    private fun handleClosed(exchange: HttpExchange, book: SignupBook, sheet: SignupSheet) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> {
                sendResponse(exchange, OK, sheet.isClosed)
            }
            
            HttpMethod.POST -> {
                sheet.close()
                book.save(sheet)
                sendResponse(exchange, OK, "closed")
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
        val closedRoute = UriTemplate("/sessions/{sessionId}/closed")
        
        @JvmField
        val routes = listOf(signupsRoute, signupRoute, closedRoute)
        
        private fun matchRoute(exchange: HttpExchange, paramsOut: HashMap<String, String>): UriTemplate? {
            for (t in routes) {
                if (t.match(exchange.requestURI.path, paramsOut)) {
                    return t
                }
            }
            return null
        }
        
        @Throws(IOException::class)
        private fun sendResponse(exchange: HttpExchange, status: Status, bodyValue: Any?) {
            exchange.responseHeaders.add("Content-Type", "text/plain")
            exchange.sendResponseHeaders(status.statusCode, 0)
            val body = OutputStreamWriter(exchange.responseBody)
            body.write(bodyValue.toString())
            body.flush()
        }
        
        @Throws(IOException::class)
        private fun sendMethodNotAllowed(exchange: HttpExchange) {
            sendResponse(
                exchange, METHOD_NOT_ALLOWED,
                exchange.requestMethod + " method not allowed"
            )
        }
    }
}
