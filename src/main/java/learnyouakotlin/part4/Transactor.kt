package learnyouakotlin.part4

interface Transactor<out Resource> {
    enum class Mode { ReadOnly, ReadWrite }
    
    fun <T> perform(mode: Mode, work: (Resource)-> T): T
}
