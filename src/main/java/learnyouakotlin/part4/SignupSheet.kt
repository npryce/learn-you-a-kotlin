package learnyouakotlin.part4

data class SignupSheet(
    val sessionId: SessionId,
    val capacity: Int,
    val signups: Set<AttendeeId>,
    val isClosed: Boolean
) {
    constructor(
        sessionId: SessionId,
        capacity: Int
    ) : this(sessionId, capacity, emptySet(), false)

    init {
        check(signups.size <= capacity)
    }

    fun isSignedUp(attendeeId: AttendeeId): Boolean = attendeeId in signups

    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return copy(signups = signups + attendeeId)
    }

    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return copy(signups = signups - attendeeId)
    }

    fun close(): SignupSheet = copy(isClosed = true)
}
