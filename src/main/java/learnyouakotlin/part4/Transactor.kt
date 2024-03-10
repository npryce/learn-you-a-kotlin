package learnyouakotlin.part4

interface Transactor<Resource> {
    fun <T> perform(work: (Resource)-> T): T
}
