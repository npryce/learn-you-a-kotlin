package learnyouakotlin.part2

import learnyouakotlin.part1.Session

val Session.subtitleOrPrompt: String
    get() = subtitle ?: "click to enter subtitle"

fun List<Session>.find(title: String): Session? =
    firstOrNull { it.title.equals(title, ignoreCase = true) }