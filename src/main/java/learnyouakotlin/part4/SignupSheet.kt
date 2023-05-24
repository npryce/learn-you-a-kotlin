package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    abstract val isSessionStarted: Boolean
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
}

class Open private constructor(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>,
    override val isSessionStarted: Boolean
) : SignupSheet() {
    constructor(sessionId: SessionId, capacity: Int) : this(
        sessionId = sessionId,
        capacity = capacity,
        signups = emptySet(),
        isSessionStarted = false
    )
    
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return Open(sessionId, capacity, signups + attendeeId, isSessionStarted)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return Open(sessionId, capacity, signups - attendeeId, isSessionStarted)
    }
    
    fun sessionStarted(): SignupSheet =
        Open(sessionId, capacity, signups, true)
}
