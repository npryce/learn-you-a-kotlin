package learnyouakotlin.part4

data class SessionSignup(
    val capacity: Int = 0,
    val signups: Set<AttendeeId> = emptySet(),
    val isSessionStarted: Boolean = false
) {
    init {
        check(signups.size <= capacity) {
            "you cannot set the capacity to fewer than the number of signups"
        }
    }
    
    fun withCapacity(newCapacity: Int): SessionSignup {
        check(!isSessionStarted) {
            "you cannot change the capacity after the session as started"
        }
        return copy(capacity = newCapacity)
    }
    
    val isFull: Boolean
        get() = signups.size == capacity
    
    fun signUp(attendeeId: AttendeeId): SessionSignup = when {
        signups.contains(attendeeId) -> this
        else -> {
            check(!isSessionStarted) { "cannot sign up for session after it has started" }
            check(!isFull) { "session is full" }
            copy(signups = signups + attendeeId)
        }
    }
    
    fun cancelSignUp(attendeeId: AttendeeId) =
        copy(signups = signups - attendeeId)
    
    fun start() =
        copy(isSessionStarted = true)
}
