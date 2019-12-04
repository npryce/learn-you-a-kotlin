package learnyouakotlin.part1

import java.util.*

class Presenter(val name: String) {
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val presenter = o as Presenter
        return name == presenter.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name)
    }

    override fun toString(): String {
        return name
    }

}