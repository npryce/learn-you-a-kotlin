package learnyouakotlin.part4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class SessionSignupTests {
    @Test
    fun collects_signups() {
        var signup = newSessionSignup(15)
        
        assertTrue(signup.signups.isEmpty())
        signup = signup.signUp(alice) as AvailableSession
        
        assertEquals(setOf(alice), signup.signups)
        signup = signup.signUp(bob) as AvailableSession
        
        assertEquals(setOf(alice, bob), signup.signups)
        signup = signup.signUp(carol) as AvailableSession
        
        assertEquals(setOf(alice, bob, carol), signup.signups)
        signup = signup.signUp(dave) as AvailableSession
        
        assertEquals(setOf(alice, bob, carol, dave), signup.signups)
    }
    
    @Test
    fun each_attendee_can_only_sign_up_once() {
        var signup = newSessionSignup(3)
        
        signup = signup.signUp(alice) as AvailableSession
        signup = signup.signUp(alice) as AvailableSession
        signup = signup.signUp(alice) as AvailableSession
        assertEquals(setOf(alice), signup.signups)
    }
    
    @Test
    fun can_cancel_signup() {
        var signup : SessionSignup = newSessionSignup(15)
        
        assertIs<AvailableSession>(signup)
        
        signup = signup.signUp(alice) as AvailableSession
        signup = signup.signUp(bob) as AvailableSession
        signup = signup.signUp(carol) as AvailableSession
        signup = signup.cancelSignUp(bob)
        assertEquals(setOf(alice, carol), signup.signups)
    }
    
    @Test
    fun can_only_sign_up_to_capacity() {
        var signup : SessionSignup = newSessionSignup(3)
        
        assertIs<AvailableSession>(signup)
        
        signup = signup.signUp(alice) as AvailableSession
        signup = signup.signUp(bob) as AvailableSession
        signup = signup.signUp(carol)
        assertIs<FullSession>(signup)
    }
    
    @Test
    fun can_increase_capacity() {
        var signup : SessionSignup = FullSession(signups = setOf(alice,bob))
        
        assertIs<FullSession>(signup)
        
        signup = signup.withCapacity(4)
        assertIs<AvailableSession>(signup)
        signup = signup.signUp(carol) as AvailableSession
        signup = signup.signUp(dave)
        assertIs<FullSession>(signup)
    }
    
    @Test
    fun cannot_reduce_capacity_to_fewer_than_number_of_signups() {
        var signup : SessionSignup = newSessionSignup(5)
        assertIs<AvailableSession>(signup)
        
        signup = signup.signUp(alice) as AvailableSession
        signup = signup.signUp(bob) as AvailableSession
        signup = signup.signUp(carol) as AvailableSession
        signup = signup.signUp(dave) as AvailableSession
        assertFailsWith<IllegalStateException> { signup.withCapacity(3) }
    }
    
    @Test
    fun cannot_sign_up_after_session_has_started() {
        var signup : SessionSignup = newSessionSignup(capacity = 3)
        
        assertIs<AvailableSession>(signup)
        
        signup = signup.signUp(alice) as AvailableSession
        signup = signup.signUp(bob) as AvailableSession
        signup = signup.start()
        assertIs<StartedSession>(signup)
    }
    
    companion object {
        private val alice = AttendeeId("alice")
        private val bob = AttendeeId("bob")
        private val carol = AttendeeId("carol")
        private val dave = AttendeeId("dave")
    }
}
