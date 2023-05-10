package learnyouakotlin.part4

class SessionSignup {
    var capacity = 0
        set(newCapacity) {
            check(!isSessionStarted) {
                "you cannot change the capacity after the session as started"
            }
            check(_signups.size <= newCapacity) {
                "you cannot change the capacity to fewer than the number of signups"
            }
            field = newCapacity
        }
    
    private val _signups = LinkedHashSet<AttendeeId>()
    
    val signups: Set<AttendeeId>
        get() = _signups.toSet()
    
    var isSessionStarted = false
        private set
    
    val isFull: Boolean
        get() = _signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId) {
        if (_signups.contains(attendeeId)) {
            return
        }
        check(!isSessionStarted) { "cannot sign up for session after it has started" }
        check(!isFull) { "session is full" }
        _signups.add(attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        _signups.remove(attendeeId)
    }
    
    fun start() {
        isSessionStarted = true
    }
}
