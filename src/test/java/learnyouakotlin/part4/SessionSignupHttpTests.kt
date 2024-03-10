package learnyouakotlin.part4

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import jakarta.ws.rs.HttpMethod
import jakarta.ws.rs.HttpMethod.POST
import jakarta.ws.rs.core.Response.Status.CONFLICT
import jakarta.ws.rs.core.Response.Status.Family.SUCCESSFUL
import jakarta.ws.rs.core.Response.Status.Family.familyOf
import learnyouakotlin.part4.SignupHttpHandler.Companion.closedRoute
import learnyouakotlin.part4.SignupHttpHandler.Companion.signupRoute
import learnyouakotlin.part4.SignupHttpHandler.Companion.signupsRoute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.charset.StandardCharsets.UTF_8
import java.util.UUID
import java.util.function.Predicate


class SessionSignupHttpTests {
    private val exampleSessionId = SessionId(UUID.randomUUID().toString())
    private val book = InMemorySignupBook()
    private val api: HttpHandler = SignupHttpHandler(InMemoryTransactor(book))
    
    
    @Test
    fun collects_signups() {
        book.save(SignupSheet(exampleSessionId, 15))
        
        assertEquals(setOf<Any>(), getSignups(exampleSessionId))
        
        signUp(exampleSessionId, alice)
        assertEquals(setOf(alice), getSignups(exampleSessionId))
        
        signUp(exampleSessionId, bob)
        assertEquals(setOf(alice, bob), getSignups(exampleSessionId))
        
        signUp(exampleSessionId, carol)
        assertEquals(setOf(alice, bob, carol), getSignups(exampleSessionId))
        
        signUp(exampleSessionId, dave)
        assertEquals(setOf(alice, bob, carol, dave), getSignups(exampleSessionId))
    }
    
    @Test
    fun each_attendee_can_only_sign_up_once() {
        book.save(SignupSheet(exampleSessionId, 3))
        
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, alice)
        
        assertEquals(setOf(alice), getSignups(exampleSessionId))
    }
    
    @Test
    fun can_only_sign_up_to_capacity() {
        book.save(SignupSheet(exampleSessionId, 3))
        
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, bob)
        signUp(exampleSessionId, carol)
        
        signUp(failsWithConflict, exampleSessionId, dave)
    }
    
    @Test
    fun cancelling_a_signup_frees_capacity_when_not_full() {
        book.save(SignupSheet(exampleSessionId, 15))
        
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, bob)
        signUp(exampleSessionId, carol)
        
        cancelSignUp(exampleSessionId, carol)
        assertEquals(setOf(alice, bob), getSignups(exampleSessionId))
        
        signUp(exampleSessionId, dave)
        assertEquals(setOf(alice, bob, dave), getSignups(exampleSessionId))
    }
    
    @Test
    fun cancelling_a_signup_frees_capacity_when_full() {
        book.save(SignupSheet(exampleSessionId, 3))
        
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, bob)
        signUp(exampleSessionId, carol)
        
        cancelSignUp(exampleSessionId, bob)
        assertEquals(setOf(alice, carol), getSignups(exampleSessionId))
        
        signUp(exampleSessionId, dave)
        assertEquals(setOf(alice, carol, dave), getSignups(exampleSessionId))
    }
    
    @Test
    fun cannot_sign_up_when_sheet_closed() {
        book.save(SignupSheet(exampleSessionId, 3))
        
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, bob)
        
        assertTrue(!isSessionClosed(exampleSessionId))
        closeSession(exampleSessionId)
        
        assertTrue(isSessionClosed(exampleSessionId))
        signUp(failsWithConflict, exampleSessionId, carol)
    }
    
    @Test
    fun cannot_cancel_a_sign_up_after_sheet_closed() {
        book.save(SignupSheet(exampleSessionId, 3))
        
        signUp(exampleSessionId, alice)
        signUp(exampleSessionId, bob)
        closeSession(exampleSessionId)
        
        cancelSignUp(failsWithConflict, exampleSessionId, alice)
    }
    
    private fun signUp(sessionId: SessionId, attendeeId: AttendeeId) {
        signUp(isSuccessful, sessionId, attendeeId)
    }
    
    private fun signUp(expectedOutcome: Predicate<HttpExchange>, sessionId: SessionId, attendeeId: AttendeeId) {
        apiCall(
            expectedOutcome, POST, signupRoute.createURI(
                mapOf(
                    "sessionId" to sessionId.value,
                    "attendeeId" to attendeeId.value
                )
            )
        )
    }
    
    private fun cancelSignUp(sessionId: SessionId, attendeeId: AttendeeId) {
        cancelSignUp(isSuccessful, sessionId, attendeeId)
    }
    
    private fun cancelSignUp(expectedResult: Predicate<HttpExchange>, sessionId: SessionId, attendeeId: AttendeeId) {
        apiCall(
            expectedResult, HttpMethod.DELETE, signupRoute.createURI(
                mapOf(
                    "sessionId" to sessionId.value,
                    "attendeeId" to attendeeId.value
                )
            )
        )
    }
    
    private fun getSignups(sessionId: SessionId): Set<AttendeeId> =
        apiCall(
            isSuccessful, HttpMethod.GET, signupsRoute.createURI(
                mapOf(
                    "sessionId" to sessionId.value
                )
            )
        ).lines()
            .filter { it.isNotBlank() }
            .map(::AttendeeId)
            .toSet()
    
    private fun isSessionClosed(sessionId: SessionId): Boolean =
        apiCall(
            isSuccessful, HttpMethod.GET, closedRoute.createURI(
                mapOf(
                    "sessionId" to sessionId.value
                )
            )
        ).toBoolean()
    
    private fun closeSession(sessionId: SessionId) {
        apiCall(
            isSuccessful, POST, closedRoute.createURI(
                mapOf(
                    "sessionId" to sessionId.value
                )
            )
        )
    }
    
    private fun apiCall(expectedResult: Predicate<HttpExchange>, method: String, uri: String): String {
        val exchange = InMemoryHttpExchange(method, uri)
        api.handle(exchange)
        assertTrue(expectedResult.test(exchange)) { "expected $expectedResult" }
        return exchange.responseBody.toString(UTF_8)
    }
    
    companion object {
        private val alice: AttendeeId = AttendeeId("alice")
        private val bob: AttendeeId = AttendeeId("bob")
        private val carol: AttendeeId = AttendeeId("carol")
        private val dave: AttendeeId = AttendeeId("dave")
        
        private val failsWithConflict: Predicate<HttpExchange> = object : Predicate<HttpExchange> {
            override fun toString() = "fails with conflict"
            
            override fun test(exchange: HttpExchange): Boolean =
                exchange.responseCode == CONFLICT.statusCode
        }
        
        private val isSuccessful: Predicate<HttpExchange> = object : Predicate<HttpExchange> {
            override fun toString() = "is successful"
            
            override fun test(exchange: HttpExchange): Boolean =
                familyOf(exchange.responseCode) == SUCCESSFUL
        }
    }
}
