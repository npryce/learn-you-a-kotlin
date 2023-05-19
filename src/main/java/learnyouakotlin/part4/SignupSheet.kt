package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
}

sealed class Open : SignupSheet()

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
}

data class Full(
    override val sessionId: SessionId,
    override val signups: Set<AttendeeId>
) : Open() {
    override val capacity: Int get() = signups.size
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SignupSheet()


fun SignupSheet.isSignedUp(attendeeId: AttendeeId): Boolean =
    signups.contains(attendeeId)

fun Available.signUp(attendeeId: AttendeeId): Open {
    val newSignups = signups + attendeeId
    return when (newSignups.size) {
        capacity -> Full(sessionId, newSignups)
        else -> copy(signups = newSignups)
    }
}

fun Open.cancelSignUp(attendeeId: AttendeeId): Available =
    Available(sessionId, capacity, signups = signups - attendeeId)

fun Open.sessionStarted(): Closed =
    Closed(sessionId, capacity, signups)

