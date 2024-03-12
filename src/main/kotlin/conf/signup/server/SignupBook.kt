package conf.signup.server

interface SignupBook {
    fun sheetFor(session: SessionId): SignupSheet?
    fun save(signup: SignupSheet)
}
