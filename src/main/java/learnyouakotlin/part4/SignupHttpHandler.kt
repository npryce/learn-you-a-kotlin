package learnyouakotlin.part4

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import jakarta.ws.rs.HttpMethod
import jakarta.ws.rs.core.Response
import org.glassfish.jersey.uri.UriTemplate
import java.io.OutputStreamWriter
import java.util.stream.Collectors

class SignupHttpHandler(private val transactor: Transactor<SignupBook>) : HttpHandler {
    override fun handle(exchange: HttpExchange) {
        val params = HashMap<String, String>()
        val matchedRoute = routes.firstOrNull { it.match(exchange.requestURI.path, params) }
        if (matchedRoute == null) {
            sendResponse(exchange, Response.Status.NOT_FOUND, "resource not found")
            return
        }
        transactor.perform { book: SignupBook ->
            val sheet = book.sheetFor(SessionId.of(params["sessionId"]))
            if (sheet == null) {
                sendResponse(exchange, Response.Status.NOT_FOUND, "session not found")
                return@perform
            }
            when (matchedRoute) {
                signupsRoute -> handleSignups(exchange, sheet)
                signupRoute -> handleSignup(exchange, book, sheet, AttendeeId.of(params["attendeeId"]))
                closedRoute -> handleClosed(exchange, book, sheet)
            }
        }
    }

    private fun handleSignups(exchange: HttpExchange, sheet: SignupSheet) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> sendResponse(
                exchange,
                Response.Status.OK,
                sheet.signups.stream()
                    .map { obj: AttendeeId -> obj.value }
                    .collect(Collectors.joining("\n"))
            )

            else -> sendMethodNotAllowed(exchange)
        }
    }

    private fun handleSignup(
        exchange: HttpExchange,
        book: SignupBook,
        sheet: SignupSheet,
        attendeeId: AttendeeId) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> sendResponse(exchange, Response.Status.OK, sheet.isSignedUp(attendeeId))
            HttpMethod.POST -> when (sheet) {
                is Open ->
                    try {
                        book.save(sheet.signUp(attendeeId))
                        sendResponse(exchange, Response.Status.OK, "subscribed")
                    } catch (e: IllegalStateException) {
                        sendResponse(exchange, Response.Status.CONFLICT, e.message)
                    }
                is Closed -> TODO()
            }

            HttpMethod.DELETE -> when (sheet) {
                is Open -> try {
                    book.save(sheet.cancelSignUp(attendeeId))
                    sendResponse(exchange, Response.Status.OK, "unsubscribed")
                } catch (e: IllegalStateException) {
                    sendResponse(exchange, Response.Status.CONFLICT, e.message)
                }

                is Closed -> TODO()
            }
            else -> sendMethodNotAllowed(exchange)
        }
    }

    private fun handleClosed(exchange: HttpExchange, book: SignupBook, sheet: SignupSheet) {
        when (exchange.requestMethod) {
            HttpMethod.GET -> sendResponse(exchange, Response.Status.OK, sheet.isClosed)
            HttpMethod.POST -> when (sheet) {
                is Open -> {
                    book.save(sheet.close())
                    sendResponse(exchange, Response.Status.OK, "closed")
                }

                is Closed -> TODO()
            }
            else -> sendMethodNotAllowed(exchange)
        }
    }

    companion object {
        @JvmField
        val signupsRoute = UriTemplate("/sessions/{sessionId}/signups")

        @JvmField
        val signupRoute = UriTemplate("/sessions/{sessionId}/signups/{attendeeId}")

        @JvmField
        val closedRoute = UriTemplate("/sessions/{sessionId}/closed")

        private val routes = listOf(signupsRoute, signupRoute, closedRoute)

        private fun sendResponse(exchange: HttpExchange, status: Response.Status, bodyValue: Any?) {
            exchange.responseHeaders.add("Content-Type", "text/plain")
            exchange.sendResponseHeaders(status.statusCode, 0)
            val body = OutputStreamWriter(exchange.responseBody)
            body.write(bodyValue.toString())
            body.flush()
        }

        private fun sendMethodNotAllowed(exchange: HttpExchange) {
            sendResponse(exchange, Response.Status.METHOD_NOT_ALLOWED,
                exchange.requestMethod + " method not allowed")
        }
    }
}
