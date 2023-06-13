package example.signup

class SignupSheet(val sessionId: SessionId, val capacity: Int) {
    private val signups = LinkedHashSet<AttendeeId>()
    var isClosed = false
        private set
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun getSignups(): Set<AttendeeId> {
        return java.util.Set.copyOf(signups)
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
    
    fun close() {
        isClosed = true
    }
}
