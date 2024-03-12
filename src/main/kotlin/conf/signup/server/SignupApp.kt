package conf.signup.server

import conf.signup.server.Transactor.Mode.ReadOnly
import conf.signup.server.Transactor.Mode.ReadWrite
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.HEAD
import org.http4k.core.Method.OPTIONS
import org.http4k.core.Method.POST
import org.http4k.core.Method.TRACE
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.CONFLICT
import org.http4k.core.Status.Companion.METHOD_NOT_ALLOWED
import org.http4k.core.Status.Companion.NOT_FOUND
import org.http4k.core.Status.Companion.OK
import org.http4k.core.UriTemplate
import org.http4k.routing.bind
import org.http4k.routing.path
import org.http4k.routing.routes

val signupsPath = "/sessions/{sessionId}/signups"
val signupPath = "/sessions/{sessionId}/signups/{attendeeId}"
val closedPath = "/sessions/{sessionId}/closed"

val signupsRoute = UriTemplate.from(signupsPath)
val signupRoute = UriTemplate.from(signupPath)
val closedRoute = UriTemplate.from(closedPath)


fun Request.txMode() = when(method) {
    GET, HEAD, OPTIONS, TRACE -> ReadOnly
    else -> ReadWrite
}

fun SignupApp(transactor: Transactor<SignupBook>): HttpHandler =
    routes(
        signupsPath bind { rq ->
            val sessionId = SessionId(rq.path("sessionId")!!)
            transactor.perform(rq.txMode()) { book ->
                val sheet = book.sheetFor(sessionId)
                    ?: return@perform sessionNotFoundError(sessionId)
                
                when (rq.method) {
                    GET -> Response(OK)
                        .body(sheet.signups.joinToString("\n") { it.value })
                    else ->
                        methodNotAllowedError(rq.method)
                }
            }
        },
        signupPath bind { rq ->
            val sessionId = SessionId(rq.path("sessionId")!!)
            val attendeeId = AttendeeId(rq.path("attendeeId")!!)
            
            transactor.perform(rq.txMode()) { book ->
                val sheet = book.sheetFor(sessionId)
                    ?: return@perform sessionNotFoundError(sessionId)
                
                when (rq.method) {
                    GET -> Response(OK).body(sheet.isSignedUp(attendeeId))
                    POST -> {
                        try {
                            sheet.signUp(attendeeId)
                            book.save(sheet)
                            Response(OK).body("subscribed")
                        } catch (e: IllegalStateException) {
                            Response(CONFLICT).body(e.message ?: "cannot sign up")
                        }
                    }
                    DELETE -> {
                        try {
                            sheet.cancelSignUp(attendeeId)
                            book.save(sheet)
                            Response(OK, "unsubscribed")
                        } catch (e: IllegalStateException) {
                            Response(CONFLICT).body(e.message ?: "cannot cancel signup")
                        }
                    }
                    else -> methodNotAllowedError(rq.method)
                }
            }
        },
        
        closedPath bind { rq ->
            val sessionId = SessionId(rq.path("sessionId")!!)
            
            transactor.perform(rq.txMode()) { book ->
                val sheet = book.sheetFor(sessionId)
                    ?: return@perform sessionNotFoundError(sessionId)
                
                when (rq.method) {
                    GET -> Response(OK).body(sheet.isClosed)
                    POST -> {
                        sheet.close()
                        book.save(sheet)
                        Response(OK).body("closed")
                    }
                    else -> methodNotAllowedError(rq.method)
                }
            }
        }
    )

private fun methodNotAllowedError(method: Method) =
    Response(METHOD_NOT_ALLOWED, "$method not allowed")

private fun sessionNotFoundError(sessionId: SessionId) =
    Response(NOT_FOUND, "session $sessionId not found")

private fun Response.body(contents: Boolean) =
    body(contents.toString())
