package learnyouakotlin.part4

class SignupSheet {
    var sessionId: SessionId? = null
    
    var capacity = 0
        set(value) {
            check(field == 0) { "you cannot change the capacity after it has been set" }
            field = value
        }
    
    private val signups = LinkedHashSet<AttendeeId>()
    
    fun getSignups(): Set<AttendeeId> {
        return java.util.Set.copyOf(signups)
    }
    
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
        signups.add(attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        signups.remove(attendeeId)
    }
}
