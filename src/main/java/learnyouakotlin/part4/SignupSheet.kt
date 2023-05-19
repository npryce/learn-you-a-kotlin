package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
    
    abstract fun sessionStarted(): SignupSheet
}

sealed class Open : SignupSheet() {
    override fun sessionStarted(): SignupSheet =
        Closed(sessionId, capacity, signups)
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet =
        Available(sessionId, capacity, signups = signups - attendeeId)
}

data class Available @JvmOverloads constructor(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId> = emptySet()
) : Open() {
    init {
        check(signups.size <= capacity) {
            "cannot have more sign-ups than capacity"
        }
    }
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        val newSignups = signups + attendeeId
        return when (newSignups.size) {
            capacity -> Full(sessionId, newSignups)
            else -> copy(signups = newSignups)
        }
    }
}

data class Full(
    override val sessionId: SessionId,
    override val signups: Set<AttendeeId>
) : Open() {
    override val capacity: Int
        get() = signups.size
}


data class Closed(
    override val sessionId: SessionId,
    override val capacity: Int,
    override val signups: Set<AttendeeId>

) : SignupSheet() {
    
    override fun sessionStarted(): SignupSheet {
        return this
    }
}
