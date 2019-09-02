package learnyouakotlin.part1


data class Session(
    val title: String,
    val subtitle: String?,
    val slots: Slots,
    val presenters: List<Presenter>
) {

    constructor(title: String, subtitle: String?, slots: Slots, vararg presenters: Presenter) :
        this(title, subtitle, slots, presenters.toList())

    fun withPresenters(newLineUp: List<Presenter>) = copy(presenters = newLineUp)

    fun withTitle(newTitle: String) = copy(title = newTitle)

    fun withSubtitle(newSubtitle: String?) = copy(subtitle = newSubtitle)
}
