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
}

data class Available (
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : Open() {
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    constructor(sessionId: SessionId, capacity: Int) :
        this(sessionId, capacity, emptySet())
}

data class Full(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : Open()

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet()


fun Available.signUp(attendeeId: AttendeeId): Open =
    withSignups(signups + attendeeId)

fun Open.cancelSignUp(attendeeId: AttendeeId): Available =
    Available(sessionId, capacity, signups = signups - attendeeId)

fun Open.close(): Closed =
    Closed(sessionId, capacity, signups)

private fun Available.withSignups(newSignups: Set<AttendeeId>): Open =
    when (newSignups.size) {
        capacity -> Full(sessionId, capacity, newSignups)
        else -> copy(signups = newSignups)
    }
