package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
}

class Open private constructor(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
    
    constructor(sessionId: SessionId, capacity: Int) : this(
        sessionId = sessionId,
        capacity = capacity,
        signups = emptySet()
    )
    
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        return Open(sessionId, capacity, signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        return Open(sessionId, capacity, signups - attendeeId)
    }
    
    fun sessionStarted(): SignupSheet =
        Closed(sessionId, capacity, signups)
}

class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet()
