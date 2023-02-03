package learnyouakotlin.part1

import java.util.Arrays

data class Session(
    @JvmField val title: String,
    @JvmField val subtitle: String?,
    @JvmField val slots: Slots,
    @JvmField val presenters: List<Presenter>
) {
    constructor(title: String, subtitle: String?, slots: Slots, vararg presenters: Presenter?) : this(
        title,
        subtitle,
        slots,
        Arrays.asList<Presenter>(*presenters)
    )
    
    fun withPresenters(newLineUp: List<Presenter>): Session {
        return Session(title, subtitle, slots, newLineUp)
    }
    
    fun withTitle(newTitle: String): Session {
        return Session(newTitle, subtitle, slots, presenters)
    }
    
    fun withSubtitle(newSubtitle: String?): Session {
        return Session(title, newSubtitle, slots, presenters)
    }
}