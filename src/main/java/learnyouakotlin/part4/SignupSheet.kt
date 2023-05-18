package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val isSessionStarted: Boolean
    abstract val signups: Set<AttendeeId>
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    abstract fun sessionStarted(): SignupSheet
}

data class Open @JvmOverloads constructor(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val isSessionStarted: Boolean = false,
    override val signups: Set<AttendeeId> = emptySet()
) : SignupSheet() {
    init {
        check(signups.size <= capacity) {
            "cannot have more sign-ups than capacity"
        }
    }
    
    override fun sessionStarted(): SignupSheet =
        Closed(sessionId, capacity, signups)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet =
        copy(signups = signups + attendeeId)
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet =
        copy(signups = signups - attendeeId)
}

data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>

) : SignupSheet() {
    override val isSessionStarted: Boolean
        get() = true
    
    override fun sessionStarted(): SignupSheet {
        return this
    }
}
