package learnyouakotlin.part4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SessionSignupTests {
    @Test
    fun collects_signups() {
        val signup = SessionSignup(capacity = 15)
        
        assertEquals(emptySet(), signup.signups)
        signup.signUp(alice)
        
        assertEquals(setOf(alice), signup.signups)
        signup.signUp(bob)
        
        assertEquals(setOf(alice, bob), signup.signups)
        signup.signUp(carol)
        
        assertEquals(setOf(alice, bob, carol), signup.signups)
        signup.signUp(dave)
        
        assertEquals(setOf(alice, bob, carol, dave), signup.signups)
    }
    
    @Test
    fun each_attendee_can_only_sign_up_once() {
        val signup = SessionSignup(capacity = 3)
        
        signup.signUp(alice)
        signup.signUp(alice)
        signup.signUp(alice)
        assertTrue(!signup.isFull)
        assertEquals(setOf(alice), signup.signups)
    }
    
    @Test
    fun can_cancel_signup() {
        val signup = SessionSignup(capacity = 15)
        
        signup.signUp(alice)
        signup.signUp(bob)
        signup.signUp(carol)
        signup.cancelSignUp(bob)
        assertEquals(setOf(alice, carol), signup.signups)
    }
    
    @Test
    fun can_only_sign_up_to_capacity() {
        val signup = SessionSignup(capacity = 3)
        
        assertTrue(!signup.isFull)
        signup.signUp(alice)
        assertTrue(!signup.isFull)
        signup.signUp(bob)
        assertTrue(!signup.isFull)
        signup.signUp(carol)
        assertTrue(signup.isFull)
        assertFailsWith<IllegalStateException> { signup.signUp(dave) }
    }
    
    @Test
    fun duplicate_signup_ignored_when_full() {
        val signup = SessionSignup(capacity = 3)
        
        signup.signUp(alice)
        signup.signUp(bob)
        signup.signUp(carol)
        signup.signUp(alice) // does not throw
    }
    
    @Test
    fun cannot_sign_up_after_session_has_started() {
        val signup = SessionSignup(capacity = 3)
        
        signup.signUp(alice)
        signup.signUp(bob)
        assertTrue(!signup.isSessionStarted)
        signup.start()
        assertTrue(signup.isSessionStarted)
        assertFailsWith<IllegalStateException> { signup.signUp(carol) }
    }
    
    @Test
    fun ignores_duplicate_signup_after_session_has_started() {
        val signup = SessionSignup(capacity = 3)
        
        signup.signUp(alice)
        signup.start()
        signup.signUp(alice) // Does not throw
    }
    
    @Test
    fun can_increase_capacity() {
        val signup = SessionSignup(capacity = 2)
        
        signup.signUp(alice)
        signup.signUp(bob)
        assertTrue(signup.isFull)
        
        signup.capacity = 4
        assertTrue(!signup.isFull)
        signup.signUp(carol)
        signup.signUp(dave)
        assertEquals(4, signup.capacity)
        assertTrue(signup.isFull)
    }
    
    @Test
    fun cannot_reduce_capacity_to_fewer_than_number_of_signups() {
        val signup = SessionSignup(capacity = 4)
        
        signup.signUp(alice)
        signup.signUp(bob)
        signup.signUp(carol)
        signup.signUp(dave)
        assertFailsWith<IllegalStateException> { signup.capacity = 3 }
    }
    
    @Test
    fun cannot_reduce_capacity_after_session_started() {
        val signup = SessionSignup(capacity = 3)
        signup.signUp(alice)
        signup.signUp(bob)
        signup.signUp(carol)
        signup.start()
        assertFailsWith<IllegalStateException> { signup.capacity = 6 }
    }
    
    companion object {
        private val alice = AttendeeId("alice")
        private val bob = AttendeeId("bob")
        private val carol = AttendeeId("carol")
        private val dave = AttendeeId("dave")
    }
}
