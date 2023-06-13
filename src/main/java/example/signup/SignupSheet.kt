package example.signup


sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return attendeeId in signups
    }
}

sealed class Open : SignupSheet() {
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        return Available(sessionId, capacity, signups = signups - attendeeId)
    }
    
    fun close(): SignupSheet {
        return Closed(sessionId, capacity, signups)
    }
}

data class Available @JvmOverloads constructor (
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups : Set<AttendeeId> = emptySet(),
) : Open() {
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isFull) { "session is full" }
        return copy(signups = signups + attendeeId)
    }
    
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
}