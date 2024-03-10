package learnyouakotlin.part4

class InMemoryTransactor<Resource>(private val resource: Resource) : Transactor<Resource> {
    override fun <T> perform(work: (Resource)-> T): T {
        return work(resource)
    }
}
