package learnyouakotlin.part2

import learnyouakotlin.part1.Session

val Session.subtitleOrPrompt: String
    get() = this.subtitle ?: "click to enter subtitle"

typealias SessionList = List<Session>

fun SessionList.findWithTitle(title: String): Session? =
    firstOrNull { it.title.equals(title, ignoreCase = true) }