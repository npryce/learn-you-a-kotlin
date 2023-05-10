package learnyouakotlin.part4

class SessionSignup(
    val capacity: Int = 0,
    val signups: Set<AttendeeId> = emptySet(),
    val isSessionStarted: Boolean = false
) {
    fun withCapacity(newCapacity: Int): SessionSignup {
        check(!isSessionStarted) {
            "you cannot change the capacity after the session as started"
        }
        check(signups.size <= newCapacity) {
            "you cannot change the capacity to fewer than the number of signups"
        }
        return SessionSignup(newCapacity, signups, isSessionStarted)
    }
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): SessionSignup {
        if (signups.contains(attendeeId)) {
            return this
        }
        
        check(!isSessionStarted) { "cannot sign up for session after it has started" }
        check(!isFull) { "session is full" }
        
        return SessionSignup(capacity, signups + attendeeId, isSessionStarted)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SessionSignup {
        return SessionSignup(capacity, signups - attendeeId, isSessionStarted)
    }
    
    fun start(): SessionSignup {
        return SessionSignup(capacity, signups, isSessionStarted = true)
    }
}
