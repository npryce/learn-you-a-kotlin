package learnyouakotlin.part4

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import jakarta.ws.rs.HttpMethod
import jakarta.ws.rs.core.Response.Status
import jakarta.ws.rs.core.Response.Status.CONFLICT
import jakarta.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR
import jakarta.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED
import jakarta.ws.rs.core.Response.Status.NOT_FOUND
import jakarta.ws.rs.core.Response.Status.OK
import org.glassfish.jersey.uri.UriTemplate
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.List
import java.util.stream.Collectors

class SignupHttpHandler(private val transactor: Transactor<SignupBook>) : HttpHandler {
    @Throws(IOException::class)
    override fun handle(exchange: HttpExchange) {
        try {
            exchange.use {
                val params = HashMap<String, String>()
                val matchedRoute = matchRoute(exchange, params)
                if (matchedRoute == null) {
                    sendResponse(exchange, NOT_FOUND, "resource not found")
                    return
                }
                transactor.perform { book: SignupBook ->
                    val sheet = book.sheetFor(SessionId.of(params["sessionId"]!!))
                    if (sheet == null) {
                        sendResponse(exchange, NOT_FOUND, "session not found")
                        return@perform
                    }
                    if (matchedRoute === signupsRoute) {
                        handleSignups(exchange, book, sheet)
                    } else if (matchedRoute === signupRoute) {
                        handleSignup(exchange, book, sheet, params)
                    } else if (matchedRoute === startedRoute) {
                        handleStarted(exchange, book, sheet)
                    }
                }
            }
        } catch (e: Exception) {
            sendResponse(exchange, INTERNAL_SERVER_ERROR, e.message)
        }
    }
    
    @Throws(IOException::class)
    private fun handleSignups(exchange: HttpExchange, book: SignupBook, sheet: SignupSheet) {
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
    
    @Throws(IOException::class)
    private fun handleSignup(
        exchange: HttpExchange,
        book: SignupBook,
        sheet: SignupSheet,
        params: Map<String, String>
    ) {
        val attendeeId = AttendeeId.of(params["attendeeId"])
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
    
    @Throws(IOException::class)
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
        private val routes = List.of(signupsRoute, signupRoute, startedRoute)
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
