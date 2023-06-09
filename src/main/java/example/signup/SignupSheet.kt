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

sealed class Open : SignupSheet()

data class Available(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId> = emptySet()
) : Open()

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
) : SignupSheet()



fun Available.signUp(attendeeId: AttendeeId): Open =
    withSignups(signups + attendeeId)

fun Open.cancelSignUp(attendeeId: AttendeeId): Available =
    Available(sessionId, capacity, signups - attendeeId)

fun Open.close(): Closed =
    Closed(sessionId, capacity, signups)

private fun Available.withSignups(newSignups: Set<AttendeeId>) =
    when (newSignups.size) {
        capacity -> Full(sessionId, newSignups)
        else -> copy(signups = newSignups)
    }
