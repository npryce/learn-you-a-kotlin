package learnyouakotlin.part4

class SignupSheet {
    var sessionId: SessionId? = null
    
    var capacity = 0
        set(value) {
            check(field == 0) { "you cannot change the capacity after it has been set" }
            field = value
        }
    
    var signups = setOf<AttendeeId>()
        private set
    
    var isSessionStarted = false
        private set
    
    constructor()
    
    constructor(sessionId: SessionId?, capacity: Int) {
        this.sessionId = sessionId
        this.capacity = capacity
    }
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun sessionStarted() {
        isSessionStarted = true
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId) {
        check(!isSessionStarted) { "you cannot sign up for session after it has started" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        signups = signups - attendeeId
    }
}
