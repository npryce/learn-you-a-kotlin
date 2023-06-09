package example.signup

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        attendeeId in signups
    
    companion object {
        @JvmStatic
        fun newSignupSheet(sessionId: SessionId, capacity: Int): Open {
            return Open(sessionId, capacity)
        }
    }
}

data class Open(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId> = emptySet()
) : SignupSheet() {
    
    private val isFull: Boolean
        get() = signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isFull) { "session is full" }
        return copy(signups = signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet =
        copy(signups = signups - attendeeId)
    
    fun close(): SignupSheet =
        Closed(sessionId, capacity, signups)
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
}
