package learnyouakotlin.part4

interface SignupBook {
    fun sheetFor(session: SessionId): SignupSheet?
    fun save(signup: SignupSheet)
}
