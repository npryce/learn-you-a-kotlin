package conf.signup.server

import org.http4k.core.Method
import org.http4k.core.Method.DELETE
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.function.Predicate


class SignupAppTests {
    private val exampleSessionId = SessionId(UUID.randomUUID().toString())
    private val book = InMemorySignupBook()
    private val api = SignupApp(InMemoryTransactor(book))
    
    
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
    
    private fun signUp(expectedOutcome: (Response)->Boolean, sessionId: SessionId, attendeeId: AttendeeId) {
        apiCall(
            expectedOutcome, POST, signupRoute.generate(
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
    
    private fun cancelSignUp(expectedResult: (Response)->Boolean, sessionId: SessionId, attendeeId: AttendeeId) {
        apiCall(
            expectedResult, DELETE, signupRoute.generate(
                mapOf(
                    "sessionId" to sessionId.value,
                    "attendeeId" to attendeeId.value
                )
            )
        )
    }
    
    private fun getSignups(sessionId: SessionId): Set<AttendeeId> =
        apiCall(
            isSuccessful, GET, signupsRoute.generate(
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
            isSuccessful, GET, closedRoute.generate(
                mapOf(
                    "sessionId" to sessionId.value
                )
            )
        ).toBoolean()
    
    private fun closeSession(sessionId: SessionId) {
        apiCall(
            isSuccessful, POST, closedRoute.generate(
                mapOf(
                    "sessionId" to sessionId.value
                )
            )
        )
    }
    
    private fun apiCall(expectedResult: Predicate<Response>, method: Method, uri: String): String {
        val request = Request(method, uri)
        val response = api(request)
        assertTrue(expectedResult.test(response)) { "expected $expectedResult" }
        return response.bodyString()
    }
    
    companion object {
        private val alice: AttendeeId = AttendeeId("alice")
        private val bob: AttendeeId = AttendeeId("bob")
        private val carol: AttendeeId = AttendeeId("carol")
        private val dave: AttendeeId = AttendeeId("dave")
        
        private val failsWithConflict = object : (Response)->Boolean {
            override fun toString() = "fails with conflict"
            override fun invoke(response: Response): Boolean =
                response.status == Status.CONFLICT
        }
        
        private val isSuccessful = object : (Response)->Boolean {
            override fun toString() = "is successful"
            override fun invoke(response: Response): Boolean =
                response.status.successful
        }
    }
}
