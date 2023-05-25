package learnyouakotlin.part4

class SignupSheet(val sessionId: SessionId, val capacity: Int) {
    var signups = emptySet<AttendeeId>()
        private set
    
    var isClosed = false
        private set
    
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
