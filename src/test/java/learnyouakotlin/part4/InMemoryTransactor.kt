package learnyouakotlin.part4

import learnyouakotlin.part4.Transactor.Mode
import learnyouakotlin.part4.Transactor.Mode.ReadOnly
import learnyouakotlin.part4.Transactor.Mode.ReadWrite
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class InMemoryTransactor<Resource>(private val resource: Resource) : Transactor<Resource> {
    private val lock = ReentrantReadWriteLock()
    
    override fun <T> perform(mode: Mode, work: (Resource) -> T): T =
        when (mode) {
            ReadOnly -> lock.read { work(resource) }
            ReadWrite -> lock.write { work(resource) }
        }
}
