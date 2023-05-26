package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    abstract val isClosed: Boolean
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
}

data class Open(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    constructor(sessionId: SessionId, capacity: Int) :
        this(sessionId, capacity, emptySet())
    
    fun close(): SignupSheet =
        Closed(sessionId, capacity, signups)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return copy(signups = signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return copy(signups = signups - attendeeId)
    }
    
    override val isClosed: Boolean
        get() = false
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
    override val isClosed: Boolean
        get() = true
}