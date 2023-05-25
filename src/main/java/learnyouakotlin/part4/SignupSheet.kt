package learnyouakotlin.part4

class SignupSheet private constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val signups: Set<AttendeeId>,
    val isClosed: Boolean
) {
    constructor(sessionId: SessionId, capacity: Int) :
        this(sessionId, capacity, emptySet(), false)
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun close(): SignupSheet {
        return SignupSheet(sessionId, capacity, signups, true)
    }
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean {
        return signups.contains(attendeeId)
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        check(!isFull) { "session is full" }
        return SignupSheet(sessionId, capacity, signups + attendeeId, false)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return SignupSheet(sessionId, capacity, signups - attendeeId, false)
    }
}
