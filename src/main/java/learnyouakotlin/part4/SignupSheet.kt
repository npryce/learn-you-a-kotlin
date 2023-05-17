package learnyouakotlin.part4

class SignupSheet @JvmOverloads constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val isSessionStarted: Boolean = false,
    val signups: Set<AttendeeId> = emptySet()
) {
    
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
        return SignupSheet(sessionId, capacity, isSessionStarted, signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        return SignupSheet(sessionId, capacity, isSessionStarted, signups - attendeeId)
    }
}
