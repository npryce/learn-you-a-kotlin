package learnyouakotlin.part1

import java.util.Arrays
import java.util.Collections
import java.util.Objects

class Session(@JvmField val title: String, @JvmField val subtitle: String?, @JvmField val slots: Slots, presenters: List<Presenter>?) {
    @JvmField
    val presenters: List<Presenter>
    
    init {
        this.presenters = Collections.unmodifiableList(ArrayList(presenters))
    }
    
    constructor(title: String, subtitle: String?, slots: Slots, vararg presenters: Presenter?) : this(
        title,
        subtitle,
        slots,
        Arrays.asList<Presenter>(*presenters)
    )
    
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val session = o as Session
        return title == session.title && subtitle == session.subtitle && slots == session.slots && presenters == session.presenters
    }
    
    override fun hashCode(): Int {
        return Objects.hash(title, subtitle, slots, presenters)
    }
    
    override fun toString(): String {
        return "Session{" +
            "title='" + title + '\'' +
            ", subtitle='" + subtitle + '\'' +
            ", slots=" + slots +
            ", presenters=" + presenters +
            '}'
    }
    
    fun withPresenters(newLineUp: List<Presenter>?): Session {
        return Session(title, subtitle, slots, newLineUp)
    }
    
    fun withTitle(newTitle: String): Session {
        return Session(newTitle, subtitle, slots, presenters)
    }
    
    fun withSubtitle(newSubtitle: String?): Session {
        return Session(title, newSubtitle, slots, presenters)
    }
}