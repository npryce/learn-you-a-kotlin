package learnyouakotlin.part4

class SignupSheet {
    var sessionId: SessionId? = null
    
    var capacity = 0
        set(value) {
            check(field == 0) { "you cannot change the capacity after it has been set" }
            field = value
        }
    
    var signups: Set<AttendeeId> = emptySet()
        private set
    
    var isSessionStarted = false
        private set
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    constructor()
    
    constructor(sessionId: SessionId, capacity: Int) {
        this.sessionId = sessionId
        this.capacity = capacity
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
        return this
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        signups = signups - attendeeId
        return this
    }
    
    fun sessionStarted(): SignupSheet {
        isSessionStarted = true
        return this
    }
}
