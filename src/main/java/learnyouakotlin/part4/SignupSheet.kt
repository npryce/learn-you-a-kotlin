package learnyouakotlin.part4

class SignupSheet {
    var sessionId: SessionId? = null
    
    var capacity = 0
        set(value) {
            check(capacity == 0) { "you cannot change the capacity after it has been set" }
            field = value
        }
    
    var signups = emptySet<AttendeeId>()
        private set
    
    var isClosed = false
        private set
    
    constructor(sessionId: SessionId?, capacity: Int) {
        this.sessionId = sessionId
        this.capacity = capacity
    }
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun close(): SignupSheet {
        isClosed = true
        return this
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups += attendeeId
        return this
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        signups -= attendeeId
        return this
    }
}
