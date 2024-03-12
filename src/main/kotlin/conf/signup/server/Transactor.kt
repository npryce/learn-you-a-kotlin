package conf.signup.server

interface Transactor<out Resource> {
    enum class Mode { ReadOnly, ReadWrite }
    
    fun <T> perform(mode: Mode, work: (Resource)-> T): T
}
