package learnyouakotlin.part4

class SignupSheet @JvmOverloads constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val isSessionStarted: Boolean = false
) {
    var signups = setOf<AttendeeId>()
        private set
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun sessionStarted(): SignupSheet {
        return SignupSheet(sessionId, capacity, isSessionStarted=true)
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot sign up for session after it has started" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
        return this
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        signups = signups - attendeeId
        return this
    }
}
