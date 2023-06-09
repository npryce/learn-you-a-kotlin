package example.signup

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        attendeeId in signups
    
    companion object {
        @JvmStatic
        fun newSignupSheet(sessionId: SessionId, capacity: Int): Available {
            return Available(sessionId, capacity)
        }
    }
}

sealed class Open : SignupSheet() {
    fun cancelSignUp(attendeeId: AttendeeId): Available =
        Available(sessionId, capacity, signups - attendeeId)
    
    fun close(): Closed =
        Closed(sessionId, capacity, signups)
}

data class Available(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId> = emptySet()
) : Open() {
    private val isFull: Boolean = signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): Open {
        check(!isFull) { "session is full" }
        return copy(signups = signups + attendeeId)
    }
}

data class Full(
    override val sessionId: SessionId,
    override val signups: Set<AttendeeId>
) : Open() {
    override val capacity: Int
        get() = signups.size
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet()
