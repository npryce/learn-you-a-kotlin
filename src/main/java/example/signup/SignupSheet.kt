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
        val newSignups = signups + attendeeId
        return when (newSignups.size) {
            capacity -> Full(sessionId, newSignups)
            else -> copy(signups = newSignups)
        }
    }
}

data class Full(
    override val sessionId: SessionId,
    override val signups: Set<AttendeeId>
) : Open() {
    override val capacity: Int = signups.size
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet() {
}