package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
}

sealed class Open : SignupSheet() {
    abstract override val sessionId: SessionId
    abstract override val capacity: Int
    abstract override val signups: Set<AttendeeId>
    
    fun close(): SignupSheet =
        Closed(sessionId, capacity, signups)
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        return Available(sessionId, capacity, signups = signups - attendeeId)
    }
}

data class Available(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : Open() {
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    constructor(sessionId: SessionId, capacity: Int) :
        this(sessionId, capacity, emptySet())
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        return copy(signups = signups + attendeeId)
    }
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet()