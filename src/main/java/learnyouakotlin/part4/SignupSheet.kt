package learnyouakotlin.part4

class SignupSheet {
    var capacity = 0
        set(value) {
            check(capacity == 0) { "you cannot change the capacity after it has been set" }
            field = value
        }

    var sessionId: SessionId? = null
        set(value) {
            check(sessionId == null) { "you cannot change the sessionId after it has been set" }
            field = value
        }

    var signups = emptySet<AttendeeId>()
        private set

    var isClosed = false
        private set

    constructor()
    constructor(sessionId: SessionId, capacity: Int) {
        this.sessionId = sessionId
        this.capacity = capacity
    }

    private val isFull: Boolean
        get() = signups.size == capacity

    fun close() {
        isClosed = true
    }

    fun isSignedUp(attendeeId: AttendeeId): Boolean = attendeeId in signups

    fun signUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
    }

    fun cancelSignUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        signups = signups - attendeeId
    }
}
