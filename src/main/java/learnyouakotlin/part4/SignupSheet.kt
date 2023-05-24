package learnyouakotlin.part4

class SignupSheet private constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val signups: Set<AttendeeId>,
    val isSessionStarted: Boolean
) {
    constructor(sessionId: SessionId, capacity: Int) : this(
        sessionId = sessionId,
        capacity = capacity,
        signups = emptySet(),
        isSessionStarted = false
    )
    
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return SignupSheet(sessionId, capacity, signups + attendeeId, isSessionStarted)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return SignupSheet(sessionId, capacity, signups - attendeeId, isSessionStarted)
    }
    
    fun sessionStarted(): SignupSheet =
        SignupSheet(sessionId, capacity, signups, true)
}
