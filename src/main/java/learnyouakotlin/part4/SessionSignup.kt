package learnyouakotlin.part4

class SessionSignup(capacity: Int = 0) {
    private var _capacity = capacity
    
    var capacity: Int
        set(newCapacity) {
            withCapacity(newCapacity)
        }
        get() = _capacity
    
    fun withCapacity(newCapacity: Int) : SessionSignup{
        check(!isSessionStarted) {
            "you cannot change the capacity after the session as started"
        }
        check(_signups.size <= newCapacity) {
            "you cannot change the capacity to fewer than the number of signups"
        }
        _capacity = newCapacity
        return this
    }
    
    private val _signups = LinkedHashSet<AttendeeId>()
    
    val signups: Set<AttendeeId>
        get() = _signups.toSet()
    
    var isSessionStarted = false
        private set
    
    val isFull: Boolean
        get() = _signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): SessionSignup {
        if (_signups.contains(attendeeId)) {
            return this
        }
        check(!isSessionStarted) { "cannot sign up for session after it has started" }
        check(!isFull) { "session is full" }
        _signups.add(attendeeId)
        return this
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SessionSignup {
        _signups.remove(attendeeId)
        return this
    }
    
    fun start(): SessionSignup {
        isSessionStarted = true
        return this
    }
}
