package learnyouakotlin.part4

sealed class SignupSheet {
    abstract val sessionId: SessionId
    abstract val capacity: Int
    abstract val signups: Set<AttendeeId>
    
    fun isSignedUp(attendeeId: AttendeeId): Boolean =
        signups.contains(attendeeId)
}

data class Open @JvmOverloads constructor(
    override val sessionId: SessionId,
    override val capacity: Int,
    val isSessionStarted: Boolean = false,
    override val signups: Set<AttendeeId> = emptySet()
) : SignupSheet() {
    init {
        check(signups.size <= capacity) {
            "cannot have more sign-ups than capacity"
        }
    }
    
    fun sessionStarted(): SignupSheet =
        copy(isSessionStarted = true)
    
    fun signUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return copy(signups = signups + attendeeId)
    }
    
    fun cancelSignUp(attendeeId: AttendeeId): SignupSheet {
        check(!isSessionStarted) { "you cannot change sign-ups for a session after it has started" }
        return copy(signups = signups - attendeeId)
    }
}
