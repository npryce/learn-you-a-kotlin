package learnyouakotlin.part1


data class Session(
    val title: String,
    val subtitle: String?,
    val slots: Slots,
    val presenters: List<Presenter>
) {

    constructor(
        title: String,
        subtitle: String?,
        slots: Slots,
        vararg presenters: Presenter
    ) : this(title, subtitle, slots, presenters.asList())

    fun withPresenters(vararg newLineUp: Presenter) = copy(presenters = newLineUp.asList())
}
