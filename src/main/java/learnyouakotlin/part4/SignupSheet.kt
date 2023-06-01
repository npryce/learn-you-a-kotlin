package learnyouakotlin.part4

class SignupSheet(
        val sessionId: SessionId,
        val capacity: Int
) {
    var signups = emptySet<AttendeeId>()
        private set

    var isClosed = false
        private set

    private val isFull: Boolean
        get() = signups.size == capacity

    fun isSignedUp(attendeeId: AttendeeId): Boolean = attendeeId in signups

    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
        return this
    }

    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        signups = signups - attendeeId
        return this
    }

    fun close(): SignupSheet {
        isClosed = true
        return this
    }
}
