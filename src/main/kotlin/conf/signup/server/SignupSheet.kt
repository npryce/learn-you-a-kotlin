package conf.signup.server

class SignupSheet() {
    constructor(sessionId: SessionId?, capacity: Int) : this() {
        this.sessionId = sessionId
        this.capacity = capacity
    }
    
    var sessionId: SessionId? = null
    
    var capacity = 0
        set(value) {
            check(capacity == 0) { "you cannot change the capacity after it has been set" }
            field = value
        }
    
    val signups = mutableSetOf<AttendeeId>()
    
    var isClosed = false
        private set
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun close() {
        isClosed = true
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    fun signUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups.add(attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        signups.remove(attendeeId)
    }
}
