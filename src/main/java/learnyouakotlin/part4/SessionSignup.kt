package learnyouakotlin.part4

sealed interface SessionSignup {
    val capacity: Int
    val signups: Set<AttendeeId>
}

sealed interface ScheduledSession : SessionSignup {
    fun withCapacity(newCapacity: Int): AvailableSession
}

fun newSessionSignup(capacity: Int) =
    AvailableSession(capacity = capacity)

data class AvailableSession(
    override val capacity: Int = 0,
    override val signups: Set<AttendeeId> = emptySet()
) : ScheduledSession {
    init {
        check(signups.size < capacity) {
            "must have fewer signups than capacity"
        }
    }
    
    override fun withCapacity(newCapacity: Int): AvailableSession {
        return copy(capacity = newCapacity)
    }
    
    fun signUp(attendeeId: AttendeeId): SessionSignup = when {
        signups.contains(attendeeId) -> this
        else -> {
            val newSignups = signups + attendeeId
            when (newSignups.size) {
                capacity -> FullSession(newSignups)
                else ->AvailableSession(capacity, newSignups)
            }
        }
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) =
        copy(signups = signups - attendeeId)
    
    fun start() =
        StartedSession(capacity, signups)
}

data class FullSession(
    override val signups: Set<AttendeeId>
) : ScheduledSession {
    
    override val capacity: Int = signups.size
    
    override fun withCapacity(newCapacity: Int): AvailableSession {
        return AvailableSession(newCapacity, signups)
    }
}

data class StartedSession(
    override val capacity: Int,
    override val signups: Set<AttendeeId>
) : SessionSignup
