package learnyouakotlin.part4

class SignupSheet(val sessionId: SessionId, val capacity: Int) {
    var signups: Set<AttendeeId> = emptySet()
        private set
    
    var isSessionStarted = false
        private set
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
        return this
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        signups = signups - attendeeId
        return this
    }
    
    fun sessionStarted(): SignupSheet {
        isSessionStarted = true
        return this
    }
}
