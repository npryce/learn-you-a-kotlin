package learnyouakotlin.part4

data class SignupSheet @JvmOverloads constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val isSessionStarted: Boolean = false,
    val signups: Set<AttendeeId> = emptySet()
) {
    init {
        check(signups.size <= capacity) {
            "cannot have more sign-ups than capacity"
        }
    }
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun sessionStarted(): SignupSheet =
        copy(isSessionStarted = true)
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return copy(signups = signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return copy(signups = signups - attendeeId)
    }
}
