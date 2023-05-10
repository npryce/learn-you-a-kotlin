package learnyouakotlin.part4

@JvmInline
value class AttendeeId(private val value: String) {
    init {
        require(value.isNotEmpty()) { "empty value" }
    }
    
    override fun toString(): String {
        return this::class.simpleName + ":" + value
    }
    
    fun internalRepresentation(): String {
        return value
    }
}
