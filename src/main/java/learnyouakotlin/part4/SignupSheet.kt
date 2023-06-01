package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    fun isSignedUp(attendeeId: AttendeeId): Boolean = attendeeId in signups
}

data class Open(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>,
) : SignupSheet() {
    constructor(
        sessionId: SessionId,
        capacity: Int
    ) : this(sessionId, capacity, emptySet())

    init {
        check(signups.size <= capacity)
    }

    fun signUp(attendeeId: AttendeeId): Open = copy(signups = signups + attendeeId)

    fun cancelSignUp(attendeeId: AttendeeId): Open = copy(signups = signups - attendeeId)

    fun close(): Closed = Closed(sessionId, capacity, signups)
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>,
) : SignupSheet() {
}
