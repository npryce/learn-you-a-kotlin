package learnyouakotlin.part4

import learnyouakotlin.part4.Transactor.Mode
import learnyouakotlin.part4.Transactor.Mode.readOnly
import learnyouakotlin.part4.Transactor.Mode.readWrite
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class InMemoryTransactor<Resource>(private val resource: Resource) : Transactor<Resource> {
    private val lock = ReentrantReadWriteLock()
    
    override fun <T> perform(mode: Mode, work: (Resource) -> T): T =
        when (mode) {
            readOnly -> lock.read { work(resource) }
            readWrite -> lock.write { work(resource) }
        }
}
