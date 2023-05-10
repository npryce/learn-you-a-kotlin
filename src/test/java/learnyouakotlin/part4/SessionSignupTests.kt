package learnyouakotlin.part4

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class SessionSignupTests {
    @Test
    fun collects_signups() {
        var signup = newSessionSignup(15)
        
        assertEquals(emptySet(), signup.signups)
        signup = signup.signUp(alice)
        
        assertEquals(setOf(alice), signup.signups)
        signup = signup.signUp(bob)
        
        assertEquals(setOf(alice, bob), signup.signups)
        signup = signup.signUp(carol)
        
        assertEquals(setOf(alice, bob, carol), signup.signups)
        signup = signup.signUp(dave)
        
        assertEquals(setOf(alice, bob, carol, dave), signup.signups)
    }
    
    @Test
    fun each_attendee_can_only_sign_up_once() {
        var signup = newSessionSignup(3)
        
        signup = signup.signUp(alice)
        signup = signup.signUp(alice)
        signup = signup.signUp(alice)
        assertTrue(!signup.isFull)
        assertEquals(setOf(alice), signup.signups)
    }
    
    @Test
    fun can_cancel_signup() {
        var signup = newSessionSignup(15)
        
        signup = signup.signUp(alice)
        signup = signup.signUp(bob)
        signup = signup.signUp(carol)
        signup = signup.cancelSignUp(bob)
        assertEquals(setOf(alice, carol), signup.signups)
    }
    
    @Test
    fun can_only_sign_up_to_capacity() {
        var signup = newSessionSignup(3)
        
        assertTrue(!signup.isFull)
        signup = signup.signUp(alice)
        assertTrue(!signup.isFull)
        signup = signup.signUp(bob)
        assertTrue(!signup.isFull)
        signup = signup.signUp(carol)
        assertTrue(signup.isFull)
        assertFailsWith<IllegalStateException> { signup.signUp(dave) }
    }
    
    @Test
    fun duplicate_signup_ignored_when_full() {
        var signup = newSessionSignup(3)
        
        signup = signup.signUp(alice)
        signup = signup.signUp(bob)
        signup = signup.signUp(carol)
        signup = signup.signUp(alice) // does not throw
    }
    
    @Test
    fun can_increase_capacity() {
        var signup = newSessionSignup(2)
        
        signup = signup.signUp(alice)
        signup = signup.signUp(bob)
        assertTrue(signup.isFull)
        
        signup = signup.withCapacity(4)
        assertTrue(!signup.isFull)
        
        signup = signup.signUp(carol)
        signup = signup.signUp(dave)
        assertEquals(4, signup.capacity)
        assertTrue(signup.isFull)
    }
    
    @Test
    fun cannot_reduce_capacity_to_fewer_than_number_of_signups() {
        var signup = newSessionSignup(4)
        
        signup = signup.signUp(alice)
        signup = signup.signUp(bob)
        signup = signup.signUp(carol)
        signup = signup.signUp(dave)
        assertFailsWith<IllegalStateException> { signup.withCapacity(3) }
    }
    
    companion object {
        private val alice = AttendeeId("alice")
        private val bob = AttendeeId("bob")
        private val carol = AttendeeId("carol")
        private val dave = AttendeeId("dave")
    }
}
