package learnyouakotlin.part4

import java.util.Objects

class AttendeeId private constructor(value: String?) {
    private val value: String
    
    init {
        require(!(value == null || value.isEmpty())) { "empty value" }
        this.value = value
    }
    
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as AttendeeId
        return value == that.value
    }
    
    override fun hashCode(): Int {
        return Objects.hash(value)
    }
    
    override fun toString(): String {
        return javaClass.simpleName + ":" + value
    }
    
    fun internalRepresentation(): String {
        return value
    }
    
    companion object {
        fun of(value: String?): AttendeeId {
            return AttendeeId(value)
        }
    }
}
