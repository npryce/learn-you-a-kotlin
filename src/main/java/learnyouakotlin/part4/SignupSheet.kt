package learnyouakotlin.part4

class SignupSheet
    @JvmOverloads constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val signups: Set<AttendeeId> = emptySet(),
    val isSessionStarted: Boolean = false
) {
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        check(!isFull) { "session is full" }
        return SignupSheet(sessionId, capacity, signups + attendeeId, isSessionStarted)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return SignupSheet(sessionId, capacity, signups - attendeeId, isSessionStarted)
    }
    
    fun sessionStarted(): SignupSheet =
        SignupSheet(sessionId, capacity, signups, true)
}
