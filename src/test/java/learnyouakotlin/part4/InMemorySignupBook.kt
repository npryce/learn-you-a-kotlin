package learnyouakotlin.part4

class InMemorySignupBook : SignupBook {
    private val signupsById: MutableMap<SessionId, SignupSheet> = HashMap()
    
    override fun sheetFor(session: SessionId): SignupSheet? {
        // Return a copy of the sheet, to emulate behaviour of database
        return signupsById[session]?.copy()
    }
    
    private fun SignupSheet.copy(): SignupSheet {
        val copy = SignupSheet(sessionId, capacity)
        signups.forEach { copy.signUp(it) }
        if (isClosed) copy.close()
        return copy
    }
    
    override fun save(signup: SignupSheet) {
        val sessionId = signup.sessionId ?: error("SignupSheet has no sessionId")
        signupsById[sessionId] = signup
    }
}
