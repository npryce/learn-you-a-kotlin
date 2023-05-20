package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
}

sealed class Open : SignupSheet() {
    fun sessionStarted(): Closed =
        Closed(sessionId, capacity, signups)
    
    fun cancelSignUp(attendeeId: AttendeeId): Available {
        return Available(sessionId, capacity, signups - attendeeId)
    }
}

data class Available @JvmOverloads constructor(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId> = emptySet()
) : Open() {
    init {
        check(signups.size <= capacity) {
            "cannot have more sign-ups than capacity"
        }
    }
    
    fun signUp(attendeeId: AttendeeId): Available {
        return copy(signups = signups + attendeeId)
    }
    
}

data class Full(
    override val sessionId: SessionId,
    override val signups: Set<AttendeeId>
): Open() {
    override val capacity: Int
        get() = signups.size
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet()
