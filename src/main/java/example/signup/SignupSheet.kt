package example.signup

class SignupSheet(val sessionId: SessionId, val capacity: Int) {
    var signups = emptySet<AttendeeId>()
        private set
    
    var isClosed = false
        private set
    
    private val isFull: Boolean
        get() = signups.size == capacity
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        attendeeId in signups
    
    fun signUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        signups = signups + attendeeId
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) {
        check(!isClosed) { "sign-up has closed" }
        signups = signups - attendeeId
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
