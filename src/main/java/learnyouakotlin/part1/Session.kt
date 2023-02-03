package learnyouakotlin.part1

data class Session(
    @JvmField val title: String,
    @JvmField val subtitle: String?,
    @JvmField val slots: Slots,
    @JvmField val presenters: List<Presenter>
) {
    constructor(title: String, subtitle: String?, slots: Slots, vararg presenters: Presenter) : this(
        title,
        subtitle,
        slots,
        presenters.asList()
    )
    
    fun withPresenters(newLineUp: List<Presenter>): Session {
        return copy(presenters = newLineUp)
    }
    
    fun withTitle(newTitle: String): Session {
        return copy(title = newTitle)
    }
    
    fun withSubtitle(newSubtitle: String?): Session {
        return copy(subtitle = newSubtitle)
    }
}