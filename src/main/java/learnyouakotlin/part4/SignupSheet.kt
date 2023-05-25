package learnyouakotlin.part4

class SignupSheet {
    var sessionId: SessionId? = null
    private var capacity = 0
    private val signups = LinkedHashSet<AttendeeId>()
    var isClosed = false
        private set
    
    constructor()
    constructor(sessionId: SessionId?, capacity: Int) {
        this.sessionId = sessionId
        this.capacity = capacity
    }
    
    fun getCapacity(): Int {
        return capacity
    }
    
    fun setCapacity(newCapacity: Int) {
        check(capacity == 0) { "you cannot change the capacity after it has been set" }
        capacity = newCapacity
    }
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun close() {
        isClosed = true
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups.add(attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        signups.remove(attendeeId)
    }
    
    fun getSignups(): Set<AttendeeId> {
        return java.util.Set.copyOf(signups)
    }
}
