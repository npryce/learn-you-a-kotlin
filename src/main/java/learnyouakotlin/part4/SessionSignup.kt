package learnyouakotlin.part4

sealed interface SessionSignup {
    val capacity: Int
    val signups: Set<AttendeeId>
    val isFull: Boolean get() = signups.size == capacity
}

fun newSessionSignup(capacity: Int) =
    ScheduledSessionSignup(capacity = capacity)

data class ScheduledSessionSignup(
    override val capacity: Int = 0,
    override val signups: Set<AttendeeId> = emptySet()
) : SessionSignup {
    init {
        check(signups.size <= capacity) {
            "you cannot set the capacity to fewer than the number of signups"
        }
    }
    
    fun withCapacity(newCapacity: Int): ScheduledSessionSignup {
        return copy(capacity = newCapacity)
    }
    
    override val isFull: Boolean
        get() = signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): ScheduledSessionSignup = when {
        signups.contains(attendeeId) -> this
        else -> {
            check(!isFull) { "session is full" }
            copy(signups = signups + attendeeId)
        }
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) =
        copy(signups = signups - attendeeId)
    
    fun start() =
        StartedSession(capacity, signups)
}

data class StartedSession(
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SessionSignup
