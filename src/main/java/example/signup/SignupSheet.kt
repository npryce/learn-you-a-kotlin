package example.signup


sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    abstract val isClosed: Boolean
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return attendeeId in signups
    }
}

data class Open @JvmOverloads constructor (
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups : Set<AttendeeId> = emptySet(),
    override val isClosed : Boolean = false
) : SignupSheet() {
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        return copy(signups = signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return copy(signups = signups - attendeeId)
    }
    
    fun close(): SignupSheet {
        return Closed(sessionId, capacity, signups)
    }
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
    override val isClosed: Boolean = true
}