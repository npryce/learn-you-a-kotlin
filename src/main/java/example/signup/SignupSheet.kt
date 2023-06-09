package example.signup

class SignupSheet(val sessionId: SessionId, val capacity: Int) {
    private val signups = LinkedHashSet<AttendeeId>()
    
    fun getSignups(): Set<AttendeeId> =
        signups.toSet()
    
    var isClosed = false
        private set
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        attendeeId in signups
    
    fun signUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups += attendeeId
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        signups -= attendeeId
    }
    
    fun close() {
        isClosed = true
    }
    
    companion object {
        @JvmStatic
        fun newSignupSheet(sessionId: SessionId, capacity: Int): SignupSheet {
            return SignupSheet(sessionId, capacity)
        }
    }
}
