package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    fun isSignedUp(attendeeId: AttendeeId): Boolean = attendeeId in signups
}

sealed class Open : SignupSheet() {
    abstract override val sessionId: SessionId
    abstract override val capacity: Int
    abstract override val signups: Set<AttendeeId>

    fun cancelSignUp(attendeeId: AttendeeId): Available =
        Available(
            sessionId,
            capacity,
            signups - attendeeId
        )

    fun close(): Closed = Closed(sessionId, capacity, signups)
}

data class Available(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>,
) : Open() {
    constructor(
        sessionId: SessionId,
        capacity: Int
    ) : this(sessionId, capacity, emptySet())

    init {
        check(signups.size <= capacity)
    }

    fun signUp(attendeeId: AttendeeId): Open {
        val newSignups = signups + attendeeId
        return when (newSignups.size) {
            capacity -> Full(sessionId, newSignups)
            else -> copy(signups = newSignups)
        }
    }
}

data class Full(
    override val sessionId: SessionId,
    override val signups: Set<AttendeeId>,
) : Open() {
    override val capacity: Int = signups.size
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>,
) : SignupSheet() {
}
