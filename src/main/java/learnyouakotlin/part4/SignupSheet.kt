package learnyouakotlin.part4

class SignupSheet private constructor(
    val sessionId: SessionId,
    val capacity: Int,
    val signups: Set<AttendeeId>,
    val isClosed: Boolean
) {
    init {
        check(signups.size <= capacity) { "session is full" }
    }
    
    constructor(sessionId: SessionId, capacity: Int) :
        this(sessionId, capacity, emptySet(), false)
    
    fun close(): SignupSheet =
        SignupSheet(sessionId, capacity, signups, true)
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return SignupSheet(sessionId, capacity, signups + attendeeId, false)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isClosed) { "sign-up has closed" }
        return SignupSheet(sessionId, capacity, signups - attendeeId, false)
    }
}
